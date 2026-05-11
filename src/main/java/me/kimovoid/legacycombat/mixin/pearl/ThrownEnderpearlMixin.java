package me.kimovoid.legacycombat.mixin.pearl;

import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Set;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {

    @ModifyArg(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/portal/TeleportTransition;<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFLjava/util/Set;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;Lorg/bukkit/event/player/PlayerTeleportEvent$TeleportCause;)V"
            ),
            index = 5
    )
    public Set<Relative> removeRelativeVelocity(Set<Relative> relatives) {
        return Relative.union(Relative.ROTATION);
    }
}