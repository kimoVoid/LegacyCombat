package me.kimovoid.legacycombat.mixin.hitbox;

import com.llamalad7.mixinextras.sugar.Local;
import me.kimovoid.legacycombat.LegacyCombat;
import me.kimovoid.legacycombat.mixinterface.IServerPlayer;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow @Final private Entity entity;

    @Inject(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void syncHitboxEntityPosition(CallbackInfo ci) {
        if (!(this.entity instanceof ServerPlayer sp) || LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        float size = LegacyCombat.CONFIG.inflateHitboxes * 2;
        IServerPlayer player = (IServerPlayer) sp;
        player.lc_getHitboxEntity().absSnapTo(
                this.entity.getX(),
                this.entity.getY() - size,
                this.entity.getZ(),
                0,
                0
        );

        ClientboundEntityPositionSyncPacket sync = ClientboundEntityPositionSyncPacket.of(player.lc_getHitboxEntity());
        LegacyCombat.HITBOX.sendToAll(sync, sp, sp.level());
    }

    @Inject(
            method = "sendDirtyEntityData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayersAndSelf(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0
            )
    )
    private void syncHitboxEntityPose(CallbackInfo ci, @Local List<SynchedEntityData.DataValue<?>> ogList) {
        if (!(this.entity instanceof ServerPlayer sp) || LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        IServerPlayer player = (IServerPlayer) this.entity;
        ServerPlayer hitboxEntity = player.lc_getHitboxEntity();
        hitboxEntity.setPose(this.entity.getPose());

        SynchedEntityData entityData = hitboxEntity.getEntityData();
        List<SynchedEntityData.DataValue<?>> list = entityData.packDirty();
        if (list != null) {
            LegacyCombat.HITBOX.sendToAll(new ClientboundSetEntityDataPacket(hitboxEntity.getId(), list), sp, sp.level());
        }
    }

    @Inject(
            method = "sendDirtyEntityData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayersAndSelf(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 1
            )
    )
    private void syncHitboxEntityAttributes(CallbackInfo ci) {
        if (!(this.entity instanceof ServerPlayer sp) || LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        IServerPlayer player = (IServerPlayer) this.entity;
        ServerPlayer hitboxEntity = player.lc_getHitboxEntity();
        float size = LegacyCombat.CONFIG.inflateHitboxes * 2;

        AttributeInstance scale = sp.getAttribute(Attributes.SCALE);
        AttributeInstance hitboxScale = hitboxEntity.getAttribute(Attributes.SCALE);
        if (scale == null || hitboxScale == null)
            return;
        hitboxScale.setBaseValue(scale.getBaseValue() + size);

        Collection<AttributeInstance> syncableAttributes = hitboxEntity.getAttributes().getSyncableAttributes();
        if (!syncableAttributes.isEmpty()) {
            LegacyCombat.HITBOX.sendToAll(new ClientboundUpdateAttributesPacket(hitboxEntity.getId(), syncableAttributes), sp, sp.level());
        }
    }
}