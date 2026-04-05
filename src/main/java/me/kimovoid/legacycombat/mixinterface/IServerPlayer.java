package me.kimovoid.legacycombat.mixinterface;

import net.minecraft.server.level.ServerPlayer;

public interface IServerPlayer {

    void lc_setHitboxEntity(ServerPlayer player);
    ServerPlayer lc_getHitboxEntity();
}