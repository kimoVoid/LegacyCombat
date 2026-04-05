package me.kimovoid.legacycombat.mixin.swordblocking;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract ItemStack getUseItem();
    @Shadow public abstract boolean isUsingItem();

    @Inject(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;FZ)F"
            )
    )
    private void reduceSwordBlockingDamage(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef f) {
        if (f.get() > 0.0F && this.isUsingItem() && this.getUseItem().is(ItemTags.SWORDS)) {
            f.set((1.0F + f.get()) * 0.5F);
        }
    }
}