package me.kimovoid.legacycombat.mixin.hitbox;

import me.kimovoid.legacycombat.LegacyCombat;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract void applyComponents(DataComponentPatch dataComponentPatch);

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
    private void applyNewHitboxMargin(CallbackInfo ci) {
        if (!LegacyCombat.CONFIG.inflateHitboxesNew)
            return;

        AttackRange component = new AttackRange(0, 3.0f, 0, 3.0f, 0.1f, 1.0f);
        this.applyComponents(DataComponentPatch.builder().set(DataComponents.ATTACK_RANGE, component).build());
    }
}