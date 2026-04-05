package me.kimovoid.legacycombat.mixin.attackfrequency;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Redirect(method = "handleDamageEvent", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableDuration:I"))
    private int setAttackFrequency(LivingEntity instance) {
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