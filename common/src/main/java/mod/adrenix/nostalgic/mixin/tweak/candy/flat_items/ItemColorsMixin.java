package mod.adrenix.nostalgic.mixin.tweak.candy.flat_items;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.helper.candy.flatten.ItemColorHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin
{
    /**
     * Simulates the old 2D item color rendering.
     */
    @ModifyReturnValue(
        method = "getColor",
        at = @At("RETURN")
    )
    private int nt_flat_items$modifyColor(int color, ItemStack itemStack, int tintIndex, @Local ItemColor itemColor)
    {
        if (ItemColorHelper.isReady() && itemColor != null)
            return ItemColorHelper.apply(itemColor, itemStack, tintIndex);

        return color;
    }
}
