package me.kimovoid.legacycombat.mixin.hitbox;

import com.llamalad7.mixinextras.sugar.Local;
import me.kimovoid.legacycombat.LegacyCombat;
import me.kimovoid.legacycombat.mixinterface.IServerPlayer;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void createHitboxEntity(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        if (LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        IServerPlayer sp = (IServerPlayer) player;
        if (sp.lc_getHitboxEntity() == null) {
            LegacyCombat.HITBOX.init(player, this.server);
        }
    }

    @Inject(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/bukkit/event/player/PlayerChangedWorldEvent;callEvent()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void onWorldChange(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, PlayerRespawnEvent.RespawnReason respawnReason, CallbackInfoReturnable<ServerPlayer> cir, @Local Level fromWorld, @Local Level level) {
        if (LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        IServerPlayer sp = (IServerPlayer) player;

        /* Remove previous entity */
        if (sp.lc_getHitboxEntity() != null) {
            LegacyCombat.HITBOX.players.remove(sp.lc_getHitboxEntity().getId());
            for (ServerPlayer on : this.server.getPlayerList().players) {
                if (on == player)
                    continue;
                LegacyCombat.HITBOX.removeOtherEntity(player, on);
            }
        }

        LegacyCombat.HITBOX.init(player, this.server);
    }
}