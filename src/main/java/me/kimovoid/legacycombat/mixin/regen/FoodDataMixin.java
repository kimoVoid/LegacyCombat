package me.kimovoid.legacycombat.mixin.regen;

import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("deprecation")
@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow public int foodLevel = 20;
    @Shadow public float saturationLevel;
    @Shadow public float exhaustionLevel;
    @Shadow private int tickTimer;
    @Shadow public abstract void addExhaustion(float f);

    /**
     * @author kimoVoid
     * @reason forward-port 1.8 regeneration
     */
    @Overwrite
    public void tick(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.level();
        Difficulty difficulty = serverLevel.getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(serverPlayer, Math.max(this.foodLevel - 1, 0));
                if (!event.isCancelled()) {
                    this.foodLevel = event.getFoodLevel();
                }

                serverPlayer.connection.send(new ClientboundSetHealthPacket(serverPlayer.getBukkitEntity().getScaledHealth(), this.foodLevel, this.saturationLevel));
            }
        }

        boolean bl = serverLevel.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION);
        if (bl && this.foodLevel >= 18 && serverPlayer.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                serverPlayer.heal(1.0F);
                this.addExhaustion(3.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (serverPlayer.getHealth() > 10.0F || difficulty == Difficulty.HARD || serverPlayer.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    serverPlayer.hurt(serverPlayer.damageSources().starve(), 1.0F);
                }
                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }
}