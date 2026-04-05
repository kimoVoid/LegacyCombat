package me.kimovoid.legacycombat.mixin.nocooldown;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract void applyComponents(DataComponentPatch dataComponentPatch);
    @Shadow public abstract Item getItem();

    @Inject(
            method = {
                    "<init>(Lnet/minecraft/world/level/ItemLike;)V",
                    "<init>(Lnet/minecraft/core/Holder;)V",
                    "<init>(Lnet/minecraft/world/level/ItemLike;I)V",
                    "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/DataComponentPatch;)V",
                    "<init>(Ljava/lang/Void;)V",
                    "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V"
            },
            at = @At("TAIL")
    )
    private void applyPearlCooldownComponent(CallbackInfo ci) {
        if (this.getItem() != null && this.getItem() instanceof EnderpearlItem) {
            UseCooldown component = new UseCooldown(0.01F);
            this.applyComponents(DataComponentPatch.builder().set(DataComponents.USE_COOLDOWN, component).build());
        }
    }
}