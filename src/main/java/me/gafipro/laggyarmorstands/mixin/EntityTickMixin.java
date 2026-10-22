package me.gafipro.laggyarmorstands.mixin;

import me.gafipro.laggyarmorstands.LaggyArmorStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In Minecraft 1.21.11, ArmorStandEntity inherits Entity.tick() instead of declaring
 * its own tick method. Hook the declaring class, then immediately filter to armor stands.
 */
@Mixin(Entity.class)
public abstract class EntityTickMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void laggyarmorstands$skipIdleArmorStandTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ArmorStandEntity stand
                && LaggyArmorStands.CONFIG.shouldSkipTick(stand)) {
            ci.cancel();
        }
    }
}
