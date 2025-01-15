package mod.adrenix.nostalgic.mixin.tweak.gameplay.item_stacking;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    /* Shadows */

    @Shadow
    public abstract Item getItem();

    /* Injections */

    /**
     * Dynamically changes the max item stack size.
     */
    @ModifyReturnValue(
        method = "getMaxStackSize",
        at = @At("RETURN")
    )
    private int nt_item_stacking$modifyMaxStackSize(int maxStackSize)
    {
        Item item = this.getItem();

        if (maxStackSize == 1)
            return maxStackSize;

        if (GameplayTweak.OLD_FOOD_STACKING.get() && GameplayTweak.CUSTOM_FOOD_STACKING.get().containsItem(item))
            return GameplayTweak.CUSTOM_FOOD_STACKING.get().valueFrom(item);

        if (GameplayTweak.CUSTOM_ITEM_STACKING.get().containsItem(item))
            return GameplayTweak.CUSTOM_ITEM_STACKING.get().valueFrom(item);

        return maxStackSize;
    }
}
