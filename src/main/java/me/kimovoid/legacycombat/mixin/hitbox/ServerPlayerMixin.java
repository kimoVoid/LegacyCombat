package me.kimovoid.legacycombat.mixin.hitbox;

import me.kimovoid.legacycombat.mixinterface.IServerPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IServerPlayer {

    @Unique ServerPlayer lc_hitboxEntity;

    @Override
    public ServerPlayer lc_getHitboxEntity() {
        return this.lc_hitboxEntity;
    }

    @Override
    public void lc_setHitboxEntity(ServerPlayer p) {
        this.lc_hitboxEntity = p;
    }
}