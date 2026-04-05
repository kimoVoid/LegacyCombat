package me.kimovoid.legacycombat.mixin.projectiles;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Entity {

    public FishingHookMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    private void setFishingHookVelocity(FishingHook instance, Vec3 vec3) {
        float velo = (float) LegacyCombat.CONFIG.rodVelocity;
        double velocityX = -Mth.sin(this.getYRot() / 180.0f * (float)Math.PI) * Mth.cos(this.getXRot() / 180.0f * (float)Math.PI) * velo;
        double velocityZ = Mth.cos(this.getYRot() / 180.0f * (float)Math.PI) * Mth.cos(this.getXRot() / 180.0f * (float)Math.PI) * velo;
        double velocityY = -Mth.sin(this.getXRot() / 180.0f * (float)Math.PI) * velo;
        this.setDeltaMovement(velocityX, velocityY, velocityZ);
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;snapTo(DDDFF)V"
            )
    )
    private void setInitialPosition(FishingHook instance, double x, double y, double z, float pitch, float yaw, @Local(argsOnly = true) Player p) {
        instance.snapTo(p.getX(), p.getY() + p.getEyeHeight(), p.getZ(), p.getYRot(), p.getXRot());
    }

    @Redirect(
            method = "retrieve(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)I",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;hookedIn:Lnet/minecraft/world/entity/Entity;",
                    ordinal = 2
            )
    )
    private Entity removePlayerPull(FishingHook instance, @Local LocalIntRef i) {
        if (instance.hookedIn instanceof Player) {
            i.set(3);
            return null;
        }
        return instance.hookedIn;
    }

    @Redirect(
            method = "setHookedEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"
            )
    )
    private void removeScreenHook(SynchedEntityData instance, EntityDataAccessor<Object> key, Object value, @Local(argsOnly = true) Entity hooked) {
        if (hooked instanceof Player) {
            return;
        }
        instance.set(key, value);
    }
}