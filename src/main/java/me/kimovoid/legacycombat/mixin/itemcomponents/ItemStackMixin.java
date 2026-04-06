package me.kimovoid.legacycombat.mixin.itemcomponents;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.Consumable;
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
    @Shadow public abstract DataComponentMap getComponents();

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
    private void applyComponents(CallbackInfo ci) {
        /* Hitbox margin */
        float margin = 0.075f;
        try {
            margin = LegacyCombat.CONFIG.inflateHitboxesItem;
        } catch (Exception ignored) { // during startup
        }

        AttackRange rangeComponent = new AttackRange(0, 3.0f, 0, 3.0f, margin, 1.0f);
        this.applyComponents(DataComponentPatch.builder().set(DataComponents.ATTACK_RANGE, rangeComponent).build());

        /* Sword blocking */
        if (this.getItem() != null && this.getItem().getDescriptionId().contains("sword")) {
            Consumable consumableComponent = Consumable.builder().consumeSeconds(Float.MAX_VALUE).animation(ItemUseAnimation.BLOCK).build();
            if (!this.getComponents().has(DataComponents.CONSUMABLE)) {
                this.applyComponents(DataComponentPatch.builder().set(DataComponents.CONSUMABLE, consumableComponent).build());
            }
        }

        /* Ender pearl cooldown */
        if (this.getItem() != null && this.getItem() instanceof EnderpearlItem) {
            UseCooldown pearlComponent = new UseCooldown(0.01F);
            this.applyComponents(DataComponentPatch.builder().set(DataComponents.USE_COOLDOWN, pearlComponent).build());
        }
    }
}