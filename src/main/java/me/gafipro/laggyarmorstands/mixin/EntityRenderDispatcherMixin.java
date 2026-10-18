package me.gafipro.laggyarmorstands.mixin;

import me.gafipro.laggyarmorstands.LaggyArmorStands;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void laggyarmorstands$hideArmorStands(
            Entity entity,
            double x,
            double y,
            double z,
            float yaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (entity instanceof ArmorStandEntity
                && LaggyArmorStands.CONFIG.enabled
                && LaggyArmorStands.CONFIG.hideArmorStands) {
            ci.cancel();
        }
    }
}
