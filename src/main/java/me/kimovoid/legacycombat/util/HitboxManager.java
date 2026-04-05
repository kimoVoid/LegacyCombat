package me.kimovoid.legacycombat.util;

import com.mojang.authlib.GameProfile;
import me.kimovoid.legacycombat.LegacyCombat;
import me.kimovoid.legacycombat.mixinterface.IServerPlayer;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class HitboxManager {

    /* Hitbox entity ID - Real player ID*/
    public Map<Integer, Integer> players = new HashMap<>();

    public void sendToAll(Packet<?> packet, ServerPlayer own, ServerLevel level) {
        for (ServerPlayer on : level.players()) {
            if (on == own || !canSee(on, own))
                continue;
            on.connection.send(packet);
        }
    }

    private boolean canSee(ServerPlayer p, ServerPlayer target) {
        return p.getBukkitEntity().canSee(target.getBukkitEntity());
    }

    public ClientboundAddEntityPacket getPacket(ServerPlayer player, ServerPlayer hitboxEntity, float size) {
        return new ClientboundAddEntityPacket(
                hitboxEntity.getId(),
                hitboxEntity.getUUID(),
                player.getX(),
                player.getY() - size,
                player.getZ(),
                0,
                0,
                hitboxEntity.getType(),
                0,
                Vec3.ZERO,
                0
        );
    }

    public ClientboundPlayerInfoUpdatePacket.Entry getEntry(ServerPlayer hitboxEntity) {
        GameProfile profile = hitboxEntity.getGameProfile();
        return new ClientboundPlayerInfoUpdatePacket.Entry(
                hitboxEntity.getUUID(),
                profile,
                false,
                69,
                hitboxEntity.gameMode.getGameModeForPlayer(),
                hitboxEntity.getTabListDisplayName(),
                true,
                -1,
                Optionull.map(hitboxEntity.getChatSession(), RemoteChatSession::asData)
        );
    }

    public void init(ServerPlayer player, MinecraftServer server) {
        IServerPlayer sp = (IServerPlayer) player;

        UUID uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "HitboxEntity");
        float size = LegacyCombat.CONFIG.inflateHitboxes * 2;

        ServerPlayer hitboxEntity = new ServerPlayer(server, player.level(), new GameProfile(uuid, ""), ClientInformation.createDefault());
        hitboxEntity.gameProfile = gameProfile;
        sp.lc_setHitboxEntity(hitboxEntity);

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);

        ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(actions, this.getEntry(hitboxEntity));

        this.sendToAll(playerInfoPacket, player, player.level());
        hitboxEntity.setPos(player.getX(), player.getY() - size, player.getZ());
        hitboxEntity.setInvisible(true);

        // Change scale
        AttributeInstance scale = hitboxEntity.getAttribute(Attributes.SCALE);
        if (scale != null) {
            scale.setBaseValue(1.0 + size);
        }

        // Add entity to world
        ClientboundAddEntityPacket addEntityPacket = this.getPacket(player, hitboxEntity, size);
        this.sendToAll(addEntityPacket, player, player.level());

        // Sync scale
        Collection<AttributeInstance> syncableAttributes = hitboxEntity.getAttributes().getSyncableAttributes();
        if (!syncableAttributes.isEmpty()) {
            this.sendToAll(new ClientboundUpdateAttributesPacket(hitboxEntity.getId(), syncableAttributes), player, player.level());
        }

        // Sync entity data
        hitboxEntity.setPose(player.getPose());
        SynchedEntityData entityData = hitboxEntity.getEntityData();
        List<SynchedEntityData.DataValue<?>> list = entityData.packDirty();
        if (list != null) {
            this.sendToAll(new ClientboundSetEntityDataPacket(hitboxEntity.getId(), list), player, player.level());
        }

        // Remove from tab
        ClientboundPlayerInfoRemovePacket playerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(hitboxEntity.getUUID()));
        this.sendToAll(playerInfoRemovePacket, player, player.level());

        LegacyCombat.HITBOX.players.put(hitboxEntity.getId(), player.getId());
        for (ServerPlayer on : player.level().players()) {
            this.initOtherPlayer(player, on);
        }
    }

    public void initOtherPlayer(ServerPlayer to, ServerPlayer from) {
        if (to == from)
            return;

        float size = LegacyCombat.CONFIG.inflateHitboxes * 2;
        ServerPlayer hitboxEntity = ((IServerPlayer) from).lc_getHitboxEntity();
        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);

        ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(actions, this.getEntry(hitboxEntity));
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                hitboxEntity.getId(),
                hitboxEntity.getUUID(),
                hitboxEntity.getX(),
                hitboxEntity.getY() - size,
                hitboxEntity.getZ(),
                hitboxEntity.getYRot(),
                hitboxEntity.getXRot(),
                hitboxEntity.getType(),
                0,
                Vec3.ZERO,
                hitboxEntity.getYHeadRot()
        );

        to.connection.send(playerInfoPacket);
        to.connection.send(addEntityPacket);

        // Sync attributes
        Collection<AttributeInstance> syncableAttributes = hitboxEntity.getAttributes().getSyncableAttributes();
        if (!syncableAttributes.isEmpty()) {
            to.connection.send(new ClientboundUpdateAttributesPacket(hitboxEntity.getId(), syncableAttributes));
        }

        // Sync entity data
        hitboxEntity.setInvisible(false);
        hitboxEntity.setInvisible(true);
        hitboxEntity.setPose(Pose.STANDING);
        hitboxEntity.setPose(from.getPose());
        SynchedEntityData entityData = hitboxEntity.getEntityData();
        List<SynchedEntityData.DataValue<?>> list = entityData.packDirty();
        if (list != null) {
            to.connection.send(new ClientboundSetEntityDataPacket(hitboxEntity.getId(), list));
        }

        // Remove from tab
        ClientboundPlayerInfoRemovePacket playerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(hitboxEntity.getUUID()));
        to.connection.send(playerInfoRemovePacket);
    }

    public void removeOtherEntity(ServerPlayer player, ServerPlayer on) {
        IServerPlayer sp = (IServerPlayer) player;
        if (sp == null || sp.lc_getHitboxEntity() == null) return;
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(sp.lc_getHitboxEntity().getId());
        on.connection.send(removeEntitiesPacket);
    }
}