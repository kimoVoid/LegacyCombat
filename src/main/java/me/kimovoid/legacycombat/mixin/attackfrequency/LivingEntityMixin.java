package me.kimovoid.legacycombat.mixin.attackfrequency;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyConstant(method = "handleDamageEvent", constant = @Constant(intValue = 20))
    private int setAttackFrequency(int constant) {
        return LegacyCombat.CONFIG.attackFrequency;
    }

    @Redirect(
            method = "hurtServer",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableDuration:I"
            )
    )
    private int setInvulnerableDuration(LivingEntity instance) {
        return LegacyCombat.CONFIG.attackFrequency;
    }
}