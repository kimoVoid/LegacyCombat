package me.kimovoid.legacycombat.mixin.swordblocking;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract DataComponentMap getComponents();
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
    private void applySwordBlockingComponent(CallbackInfo ci) {
        //OldCombatMod.LOGGER.info("Applying sword blocking component...");
        if (this.getItem() != null && this.getItem().getDescriptionId().contains("sword")) {
            Consumable component = Consumable.builder().consumeSeconds(Float.MAX_VALUE).animation(ItemUseAnimation.BLOCK).build();
            if (!this.getComponents().has(DataComponents.CONSUMABLE)) {
                this.applyComponents(DataComponentPatch.builder().set(DataComponents.CONSUMABLE, component).build());
            }
        }
    }
}