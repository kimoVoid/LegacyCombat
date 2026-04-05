package me.kimovoid.legacycombat.mixin.hitbox;

import com.llamalad7.mixinextras.sugar.Local;
import me.kimovoid.legacycombat.LegacyCombat;
import me.kimovoid.legacycombat.mixinterface.IServerPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CraftPlayer.class)
public abstract class CraftPlayerMixin {

    @Shadow public abstract ServerPlayer getHandle();

    @Inject(
            method = "unregisterEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void hideHitboxEntity(Entity other, CallbackInfo ci, @Local ServerPlayer otherPlayer) {
        if (LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        LegacyCombat.HITBOX.removeOtherEntity(otherPlayer, this.getHandle());
    }

    @Inject(
            method = "trackAndShowEntity(Lorg/bukkit/entity/Entity;Ljava/util/UUID;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void showHitboxEntity(org.bukkit.entity.Entity entity, UUID uuidOverride, CallbackInfo ci, @Local ServerPlayer otherPlayer) {
        if (LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        IServerPlayer sp = (IServerPlayer) otherPlayer;
        if (sp.lc_getHitboxEntity() == null) return;
        LegacyCombat.HITBOX.initOtherPlayer(this.getHandle(), otherPlayer);
    }
}