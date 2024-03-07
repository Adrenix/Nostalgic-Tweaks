package mod.adrenix.nostalgic.mixin.tweak.candy.item_tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    /**
     * Controls which parts of the multiline item tooltip should be shown.
     */
    @ModifyReturnValue(
        method = "shouldShowInTooltip",
        at = @At("RETURN")
    )
    private static boolean nt_item_tooltip$shouldShowInTooltip(boolean shouldShowInTooltip, int hideFlags, ItemStack.TooltipPart part)
    {
        if (!shouldShowInTooltip || !ModTweak.ENABLED.get())
            return shouldShowInTooltip;

        return switch (part)
        {
            case DYE -> CandyTweak.SHOW_DYE_TIP.get();
            case ENCHANTMENTS -> CandyTweak.SHOW_ENCHANTMENT_TIP.get();
            case MODIFIERS -> CandyTweak.SHOW_MODIFIER_TIP.get();
            case ADDITIONAL, CAN_DESTROY, CAN_PLACE, UNBREAKABLE, UPGRADES -> true;
        };
    }
}
