package mod.adrenix.nostalgic.mixin.tweak.candy.inventory_screen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/inventory/InventoryMenu$1")
public abstract class InventoryArmorSlotMixin
{
    /**
     * Disables the overlay texture that appears over armor slots within the inventory screen.
     */
    @ModifyReturnValue(
        method = "getNoItemIcon",
        at = @At("RETURN")
    )
    private Pair<ResourceLocation, ResourceLocation> nt_inventory_screen$setNoArmorItemIcon(Pair<ResourceLocation, ResourceLocation> original)
    {
        if (CandyTweak.DISABLE_EMPTY_ARMOR_TEXTURE.get() && GameUtil.isNotCreativeMode())
            return null;

        return original;
    }
}
