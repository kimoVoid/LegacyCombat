package me.kimovoid.legacycombat.mixin.projectiles;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FishingHook.class, Snowball.class, ThrownEgg.class})
public abstract class ProjectileKnockbackMixin extends Entity {

    public ProjectileKnockbackMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void doProjectileKnockback(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (this.level() instanceof ServerLevel serverLevel && entityHitResult.getEntity() instanceof LivingEntity) {
            Entity entity = entityHitResult.getEntity();
            LivingEntity shooter = ((Projectile) (Object) this).getOwner() instanceof LivingEntity livingEntity2 ? livingEntity2 : null;
            if (!entity.hurtServer(serverLevel, this.damageSources().thrown(this, shooter), 0.0001f)) {
                return;
            }

            /* Modify knockback dealt by projectile */
            if (entity != shooter) {
                Vec3 projDir = this.getForward();
                double horizontal = LegacyCombat.CONFIG.projHorizontal;
                double vertical = LegacyCombat.CONFIG.projVertical;
                entity.setDeltaMovement(new Vec3(-projDir.x * horizontal, vertical, projDir.z * horizontal));
                return;
            }

            /* Own addition -- self projectile hits */
            Vec3 entityDir = entity.getForward();
            double horizontal = LegacyCombat.CONFIG.srHorizontal;
            double vertical = LegacyCombat.CONFIG.srVertical;
            entity.setDeltaMovement(entityDir.x * horizontal, vertical, entityDir.z * horizontal);
            entity.resetFallDistance();
        }
    }
}