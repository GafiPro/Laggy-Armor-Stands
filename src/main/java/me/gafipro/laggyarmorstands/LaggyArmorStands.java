package me.gafipro.laggyarmorstands;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LaggyArmorStands implements ModInitializer {
    public static final String MOD_ID = "laggyarmorstands";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final LaggyArmorStandsConfig CONFIG = new LaggyArmorStandsConfig();

    @Override
    public void onInitialize() {
        CONFIG.load();
        LOGGER.info("Laggy Armor Stands loaded (hide={}, optimizeTicks={}, interval={})",
                CONFIG.hideArmorStands, CONFIG.optimizeStationaryTicks, CONFIG.skipInterval);
    }
}
