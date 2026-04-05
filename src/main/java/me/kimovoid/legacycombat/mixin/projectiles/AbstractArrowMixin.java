package me.kimovoid.legacycombat.mixin.projectiles;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {

    public AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Not vanilla but most people would probably want this.
     * MMC-styled bow boosts <3
     */
    @Inject(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;doKnockback(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)V"
            )
    )
    private void doBowBoostKnockback(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (((Projectile) (Object) this).getOwner() instanceof Player owner
                && entityHitResult.getEntity() instanceof Player victim
                && victim == owner) {
            Vec3 dir = victim.getForward();
            double horizontal = LegacyCombat.CONFIG.bbHorizontal;
            double vertical = LegacyCombat.CONFIG.bbVertical;
            victim.setDeltaMovement(new Vec3(dir.x * horizontal, vertical, dir.z * horizontal));
        }
    }
}