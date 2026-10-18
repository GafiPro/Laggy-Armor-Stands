package me.gafipro.laggyarmorstands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Configuration shared by the common tick mixin and the client renderer mixin. */
public final class LaggyArmorStandsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LaggyArmorStands.LOGGER;

    /** Master switch for every feature. */
    public boolean enabled = true;

    /** Client-side rendering toggle. Interaction and server tracking remain enabled. */
    public boolean hideArmorStands = true;

    /** Server-side tick reduction for stationary, safe-to-skip armor stands. */
    public boolean optimizeStationaryTicks = true;

    /** Process eligible armor stands every N world ticks (20 means about 95% fewer ticks). */
    public int skipInterval = 20;

    public boolean skipWhenOnGround = true;
    public boolean skipWhenNoGravity = true;
    public boolean dontSkipIfRiding = true;
    public boolean dontSkipIfHasPassengers = true;
    public boolean dontSkipIfOnFire = true;
    public boolean dontSkipIfMoving = true;
    public boolean dontSkipIfRecentlyHit = true;

    private Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("laggyarmorstands.json");
    }

    public void load() {
        Path path = getPath();
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                save();
                return;
            }

            String json = Files.readString(path, StandardCharsets.UTF_8);
            LaggyArmorStandsConfig loaded = GSON.fromJson(json, LaggyArmorStandsConfig.class);
            if (loaded == null) {
                throw new IOException("Configuration parsed to null");
            }

            enabled = loaded.enabled;
            hideArmorStands = loaded.hideArmorStands;
            optimizeStationaryTicks = loaded.optimizeStationaryTicks;
            skipInterval = clampInterval(loaded.skipInterval);
            skipWhenOnGround = loaded.skipWhenOnGround;
            skipWhenNoGravity = loaded.skipWhenNoGravity;
            dontSkipIfRiding = loaded.dontSkipIfRiding;
            dontSkipIfHasPassengers = loaded.dontSkipIfHasPassengers;
            dontSkipIfOnFire = loaded.dontSkipIfOnFire;
            dontSkipIfMoving = loaded.dontSkipIfMoving;
            dontSkipIfRecentlyHit = loaded.dontSkipIfRecentlyHit;

            // Persist clamped values and any newly introduced defaults.
            save();
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Could not load {}; keeping safe defaults.", path, exception);
            save();
        }
    }

    public void save() {
        Path path = getPath();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            LOGGER.warn("Could not save {}.", path, exception);
        }
    }

    /**
     * Returns true when this tick can be skipped without affecting moving, burning,
     * mounted, carrying, or recently-hit stands. The world clock (not entity age) is
     * used for staggering because a cancelled tick does not increment entity age.
     */
    public boolean shouldSkipTick(ArmorStandEntity stand) {
        if (!enabled || !optimizeStationaryTicks || stand.getWorld().isClient) {
            return false;
        }

        if (dontSkipIfRiding && stand.hasVehicle()) {
            return false;
        }
        if (dontSkipIfHasPassengers && stand.hasPassengers()) {
            return false;
        }
        if (dontSkipIfOnFire && stand.isOnFire()) {
            return false;
        }
        if (dontSkipIfMoving) {
            Vec3d velocity = stand.getVelocity();
            if (velocity.lengthSquared() > 1.0E-6) {
                return false;
            }
        }

        int interval = clampInterval(skipInterval);
        if (dontSkipIfRecentlyHit && stand.lastHitTime > 0L
                && stand.getWorld().getTime() - stand.lastHitTime < Math.max(20, interval)) {
            return false;
        }

        boolean stableOnGround = skipWhenOnGround && stand.isOnGround();
        boolean stableWithoutGravity = skipWhenNoGravity && stand.hasNoGravity();
        if (!stableOnGround && !stableWithoutGravity) {
            return false;
        }

        long phase = Math.floorMod(stand.getWorld().getTime() + (long) stand.getId(), interval);
        return phase != 0L;
    }

    private static int clampInterval(int value) {
        return Math.max(2, Math.min(100, value));
    }
}
