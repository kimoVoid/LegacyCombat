package me.kimovoid.legacycombat.mixin.crits;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {

    @Redirect(
            method = "canCriticalAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"
            )
    )
    private boolean allowSprintCrits(Player instance) {
        return false;
    }
}