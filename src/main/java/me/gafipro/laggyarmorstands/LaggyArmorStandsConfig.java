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

/** Configuration shared by the common tick mixin and the client render culling mixin. */
public final class LaggyArmorStandsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LaggyArmorStands.LOGGER;

    public boolean enabled = true;
    public boolean hideArmorStands = true;
    public boolean optimizeStationaryTicks = true;
    public int skipInterval = 20;

    public boolean skipWhenOnGround = true;
    public boolean skipWhenNoGravity = true;
    public boolean dontSkipIfRiding = true;
    public boolean dontSkipIfHasPassengers = true;
    public boolean dontSkipIfOnFire = true;
    public boolean dontSkipIfMoving = true;
    public boolean dontSkipIfHurt = true;

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
            dontSkipIfHurt = loaded.dontSkipIfHurt;

            // Rewrite the normalized configuration, adding defaults for newly introduced options.
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
     * Cancels only idle server ticks. Ineligible stands retain normal ticking.
     * The phase uses world time and the entity ID so cancelled ticks do not affect the schedule.
     */
    public boolean shouldSkipTick(ArmorStandEntity stand) {
        if (!enabled || !optimizeStationaryTicks || stand.getEntityWorld().isClient) {
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
        if (dontSkipIfHurt && stand.hurtTime > 0) {
            return false;
        }
        if (dontSkipIfMoving && stand.getVelocity().lengthSquared() > 1.0E-6) {
            return false;
        }

        int interval = clampInterval(skipInterval);
        boolean stableOnGround = skipWhenOnGround && stand.isOnGround();
        boolean stableWithoutGravity = skipWhenNoGravity && stand.hasNoGravity();
        if (!stableOnGround && !stableWithoutGravity) {
            return false;
        }

        long phase = Math.floorMod(stand.getEntityWorld().getTime() + (long) stand.getId(), interval);
        return phase != 0L;
    }

    private static int clampInterval(int value) {
        return Math.max(2, Math.min(100, value));
    }
}
