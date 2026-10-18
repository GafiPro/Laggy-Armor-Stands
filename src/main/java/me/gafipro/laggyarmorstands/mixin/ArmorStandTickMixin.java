package me.gafipro.laggyarmorstands.mixin;

import me.gafipro.laggyarmorstands.LaggyArmorStands;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandTickMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void laggyarmorstands$skipIdleTick(CallbackInfo ci) {
        ArmorStandEntity stand = (ArmorStandEntity) (Object) this;
        if (LaggyArmorStands.CONFIG.shouldSkipTick(stand)) {
            ci.cancel();
        }
    }
}
