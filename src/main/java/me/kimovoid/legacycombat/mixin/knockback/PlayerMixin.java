package me.kimovoid.legacycombat.mixin.knockback;

import com.llamalad7.mixinextras.sugar.Local;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract float getAbsorptionAmount();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "causeExtraKnockback",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDDLnet/minecraft/world/entity/Entity;Lio/papermc/paper/event/entity/EntityKnockbackEvent$Cause;)V",
                    ordinal = 0
            )
    )
    private void setExtraKb(LivingEntity instance, double amount, double x, double y, Entity attacker, EntityKnockbackEvent.Cause cause) {
        instance.push(
                -Mth.sin(this.getYRot() * 0.017453292F) * amount * LegacyCombat.CONFIG.kbExtraHorizontal,
                LegacyCombat.CONFIG.kbExtraVertical,
                Mth.cos(this.getYRot() * 0.017453292F) * amount * LegacyCombat.CONFIG.kbExtraHorizontal,
                attacker
        );
    }

    @ModifyArg(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;)V"),
            index = 1
    )
    private float setSprintKb(float original, @Local(argsOnly = true) Entity target, @Local(name = "damageSource") DamageSource damageSource, @Local(name = "flag") boolean flag) {
        float sprintKb = (float) LegacyCombat.CONFIG.kbExtraSprint;
        return this.getKnockback(target, damageSource) + (flag ? sprintKb : 0.0F);
    }
}