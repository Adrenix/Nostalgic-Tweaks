package mod.adrenix.nostalgic.mixin.common.world.item;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    /**
     * Controls which parts of the multiline-tooltip should be shown.
     * Controlled by various tooltip tweaks.
     */
    @Inject(method = "shouldShowInTooltip", at = @At("HEAD"), cancellable = true)
    private static void NT$onShouldShowInTooltip(int hideFlags, ItemStack.TooltipPart part, CallbackInfoReturnable<Boolean> callback)
    {
        boolean isVanillaShown = (hideFlags & part.getMask()) == 0;
        if (!isVanillaShown)
            return;

        boolean isShown = switch (part)
        {
            case DYE -> ModConfig.Candy.addDyeTip();
            case ENCHANTMENTS -> ModConfig.Candy.addEnchantmentTip();
            case MODIFIERS -> ModConfig.Candy.addModifiersTip();
            case ADDITIONAL, CAN_DESTROY, CAN_PLACE, UNBREAKABLE -> true;
        };

        callback.setReturnValue(isShown);
    }
}
