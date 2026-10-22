package me.gafipro.laggyarmorstands.mixin;

import me.gafipro.laggyarmorstands.LaggyArmorStands;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Uses the 1.21.11 entity render-state pipeline rather than the removed legacy dispatcher. */
@Mixin(EntityRenderManager.class)
public abstract class EntityRenderManagerMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void laggyarmorstands$hideArmorStands(
            Entity entity,
            Frustum frustum,
            double x,
            double y,
            double z,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (entity instanceof ArmorStandEntity
                && LaggyArmorStands.CONFIG.enabled
                && LaggyArmorStands.CONFIG.hideArmorStands) {
            cir.setReturnValue(false);
        }
    }
}
