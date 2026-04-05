package me.kimovoid.legacycombat.mixin.hitbox;

import me.kimovoid.legacycombat.LegacyCombat;
import me.kimovoid.legacycombat.mixinterface.IServerPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {

    @Shadow public ServerPlayer player;

    @Shadow public abstract void handleInteract(ServerboundInteractPacket packet);

    public ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Inject(method = "removePlayerFromWorld(Lnet/kyori/adventure/text/Component;)V", at = @At("TAIL"))
    private void removeHitboxEntity(CallbackInfo ci) {
        if (LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        IServerPlayer player = (IServerPlayer) this.player;
        LegacyCombat.HITBOX.players.remove(player.lc_getHitboxEntity().getId());

        for (ServerPlayer on : this.server.getPlayerList().players) {
            if (on == this.player)
                continue;
            LegacyCombat.HITBOX.removeOtherEntity(this.player, on);
        }
    }

    @ModifyArg(method = "handleInteract", at = @At(value = "INVOKE", target = "Lio/papermc/paper/configuration/type/number/DoubleOr$Default;or(D)D"))
    private double addExtraReach(double v) {
        return v + LegacyCombat.CONFIG.inflateHitboxes;
    }

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    private void doInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        if (LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        if (packet.isAttack() && LegacyCombat.HITBOX.players.containsKey(packet.getEntityId())) {
            if (!LegacyCombat.HITBOX.players.containsKey(packet.getEntityId()))
                return;

            int realPlayerID = LegacyCombat.HITBOX.players.get(packet.getEntityId());

            ServerPlayer p = server.getPlayerList().players.stream()
                    .filter(on -> on.getId() == realPlayerID)
                    .findFirst()
                    .orElse(null);

            if (p == null)
                return;

            handleInteract(ServerboundInteractPacket.createAttackPacket(p, packet.isUsingSecondaryAction()));
            ci.cancel();
        }
    }
}