package me.kimovoid.legacycombat.mixin.hitbox;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getPickRadius", at = @At("RETURN"), cancellable = true)
    private void setHitboxInflation(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() + LegacyCombat.CONFIG.inflateHitboxes);
    }
}