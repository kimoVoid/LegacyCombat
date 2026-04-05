package me.kimovoid.legacycombat.mixin.knockback;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> holder);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author kimoVoid
     * @reason forward-port old knockback
     */
    @Overwrite
    public void knockback(double amount, double velocityX, double velocityZ, @Nullable Entity attacker, EntityKnockbackEvent.Cause eventCause) {
        double kbResistance = this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        amount *= 1.0 - kbResistance;
        if (!(amount <= 0.0)) {
            this.needsSync = true;
            Vec3 vec3 = this.getDeltaMovement();

            double x = vec3.x;
            double y = vec3.y;
            double z = vec3.z;

            double magnitude = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
            double friction = LegacyCombat.CONFIG.kbFriction - kbResistance;
            double horizontal = LegacyCombat.CONFIG.kbHorizontal * (1 - kbResistance);
            double vertical = LegacyCombat.CONFIG.kbVertical * (1 - kbResistance);

            /* Vanilla 1.8 KB calculations */
            if (!LegacyCombat.CONFIG.kbExperimental) {
                x /= friction;
                y /= friction;
                z /= friction;

                x -= velocityX / magnitude * horizontal;
                y += vertical;
                z -= velocityZ / magnitude * horizontal;

                if (y > LegacyCombat.CONFIG.kbVerticalLimit) {
                    y = LegacyCombat.CONFIG.kbVerticalLimit;
                }
            }

            /* Own KB calculations */
            else {
                x = -velocityX / magnitude * horizontal;
                y = vertical;
                z = -velocityZ / magnitude * horizontal;
            }

            this.setDeltaMovement(new Vec3(x, y, z));
        }
    }
}