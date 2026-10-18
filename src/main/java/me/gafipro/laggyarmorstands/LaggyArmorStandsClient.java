package me.gafipro.laggyarmorstands;

import net.fabricmc.api.ClientModInitializer;

public final class LaggyArmorStandsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Rendering is handled by the client-only EntityRenderDispatcher mixin.
        // Keeping this entrypoint separate ensures dedicated servers never load client classes.
    }
}
