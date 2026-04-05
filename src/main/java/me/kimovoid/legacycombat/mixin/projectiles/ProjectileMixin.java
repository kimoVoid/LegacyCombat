package me.kimovoid.legacycombat.mixin.projectiles;

import com.llamalad7.mixinextras.sugar.Local;
import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {

    @Shadow @Nullable public abstract Entity getOwner();

    public ProjectileMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "canHitEntity",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;leftOwner:Z"
            )
    )
    private boolean fixOwnerTicks(Projectile instance, @Local(argsOnly = true) Entity entity) {
        return this.getOwner() != entity || this.tickCount >= LegacyCombat.CONFIG.projTickTime;
    }

    @Redirect(method = "shootFromRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getKnownMovement()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 removeRelativeVelocity(Entity instance) {
        return new Vec3(0, 0, 0);
    }

    @ModifyArg(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;getMovementToShoot(DDDFF)Lnet/minecraft/world/phys/Vec3;"), index = 4)
    private float removeInaccuracy(float inaccuracy) {
        return 0.0f;
    }
}