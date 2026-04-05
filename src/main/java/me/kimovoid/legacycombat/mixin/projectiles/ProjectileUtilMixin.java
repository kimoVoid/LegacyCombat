package me.kimovoid.legacycombat.mixin.projectiles;

import com.llamalad7.mixinextras.sugar.Local;
import me.kimovoid.legacycombat.LegacyCombat;
import me.kimovoid.legacycombat.util.LegacyProjectileUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @Redirect(
            method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;clip(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Ljava/util/Optional;"
            )
    )
    private static Optional<Vec3> replaceClip(AABB instance, Vec3 from, Vec3 to, @Local(argsOnly = true) Entity entity) {
        return LegacyProjectileUtil.INSTANCE.clip(instance.inflate(LegacyCombat.CONFIG.inflateHitboxes), from, to);
    }
}