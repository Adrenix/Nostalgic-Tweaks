package mod.adrenix.nostalgic.forge.mixin.embeddium.candy.flat_items;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.flatten.ItemColorHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin
{
    /* Shadows */

    @Shadow @Final private Map<Holder.Reference<Item>, ItemColor> itemColors;

    /* Injections */

    /**
     * Simulates the old 2D item color rendering when Embeddium is installed.
     */
    @Dynamic("Method sodium$getColorProvider is added by Embeddium. See: me.jellysquid.mods.sodium.mixin.core.model.colors.ItemColorsMixin.java")
    @ModifyReturnValue(
        remap = false,
        method = "sodium$getColorProvider",
        at = @At("RETURN")
    )
    private ItemColor nt_embeddium_flat_items$modifyColorProvider(ItemColor itemColor, ItemStack itemStack)
    {
        ItemColor forgeColor = this.itemColors.get(ForgeRegistries.ITEMS.getDelegate(itemStack.getItem()).orElse(null));

        if (ItemColorHelper.isReady() && forgeColor != null)
            return (itemStackToColor, tintIndex) -> ItemColorHelper.apply(forgeColor, itemStackToColor, tintIndex);

        return itemColor;
    }
}
