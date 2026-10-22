package me.gafipro.laggyarmorstands.mixin;

import me.gafipro.laggyarmorstands.LaggyArmorStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In Minecraft 1.21.11 ArmorStandEntity inherits its tick implementation rather than
 * declaring its own. Injecting into ArmorStandEntity.tick therefore fails at startup.
 * Hook into the LivingEntity implementation and immediately filter to armor stands.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityTickMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void laggyarmorstands$skipIdleArmorStandTick(CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (living instanceof ArmorStandEntity stand
                && LaggyArmorStands.CONFIG.shouldSkipTick(stand)) {
            ci.cancel();
        }
    }
}
