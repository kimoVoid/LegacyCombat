package me.kimovoid.legacycombat.mixin.armor;

import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CombatRules.class)
public class CombatRulesMixin {

    @ModifyConstant(
            method = "getDamageAfterAbsorb",
            constant = @Constant(floatValue = 2.0F)
    )
    private static float oldArmorCalculations(float constant) {
        return 25.0F;
    }
}