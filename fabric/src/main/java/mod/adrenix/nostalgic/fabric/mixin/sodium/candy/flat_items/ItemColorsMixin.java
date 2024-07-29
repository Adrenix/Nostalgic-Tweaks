package mod.adrenix.nostalgic.fabric.mixin.sodium.candy.flat_items;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.flatten.ItemColorHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin
{
    /* Shadows */

    @Shadow @Final private IdMapper<ItemColor> itemColors;

    /* Injections */

    /**
     * Simulates the old 2D item color rendering when Sodium is installed.
     */
    @Dynamic("Method sodium$getColorProvider is added by Sodium. See: me.jellysquid.mods.sodium.mixin.core.model.colors.ItemColorsMixin.java")
    @ModifyReturnValue(
        remap = false,
        method = "sodium$getColorProvider",
        at = @At("RETURN")
    )
    private ItemColor nt_sodium_flat_items$modifyColorProvider(ItemColor itemColor, ItemStack itemStack)
    {
        ItemColor minecraftColor = this.itemColors.byId(BuiltInRegistries.ITEM.getId(itemStack.getItem()));

        if (ItemColorHelper.isReady() && minecraftColor != null)
            return (itemStackToColor, tintIndex) -> ItemColorHelper.apply(minecraftColor, itemStackToColor, tintIndex);

        return itemColor;
    }
}
