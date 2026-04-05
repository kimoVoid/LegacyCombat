package me.kimovoid.legacycombat.mixin.entitydatafix;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChunkMap.TrackedEntity.class)
public class TrackedEntityMixin {

    @Shadow @Final Entity entity;

    @Redirect(
            method = "sendToTrackingPlayersAndSelf",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void modifyEntityDataPacket(ServerGamePacketListenerImpl instance, Packet<?> packet) {
        if (!(packet instanceof ClientboundSetEntityDataPacket entityDataPacket && this.entity instanceof ServerPlayer p)) {
            instance.send(packet);
            return;
        }

        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>(entityDataPacket.packedItems());

        /* Fix double sneak animation */
        list.removeIf(data -> data.serializer().equals(EntityDataSerializers.POSE));

        /* Fix item use animation */
        for (SynchedEntityData.DataValue<?> data : list) {
            if (!data.serializer().equals(EntityDataSerializers.BYTE))
                continue;

            if (LegacyCombat.CONFIG.debug) {
                LegacyCombat.LOGGER.info("DataValue for {} [{}: {}]", p.displayName, data.id(), data.value());
            }

            if (data.id() == 8) {
                list.remove(data);
                break;
            }
        }

        /* Fake deaths */
        if (p.getBukkitEntity().hasMetadata("fakingDeath") && p.getEntityData().get(LivingEntity.DATA_HEALTH_ID) <= 0.0F) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id() != LivingEntity.DATA_HEALTH_ID.id()) continue;
                list.set(i, new SynchedEntityData.DataValue<>(
                        LivingEntity.DATA_HEALTH_ID.id(),
                        EntityDataSerializers.FLOAT,
                        p.getHealth()
                ));
                break;
            }
        }

        instance.send(new ClientboundSetEntityDataPacket(p.getId(), list));
    }
}