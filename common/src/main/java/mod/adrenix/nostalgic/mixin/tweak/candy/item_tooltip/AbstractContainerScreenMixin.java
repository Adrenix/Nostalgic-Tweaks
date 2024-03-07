package mod.adrenix.nostalgic.mixin.tweak.candy.item_tooltip;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin
{
    /**
     * Disables rendering of tooltips when hovering over items.
     */
    @ModifyExpressionValue(
        method = "renderTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/Slot;hasItem()Z"
        )
    )
    private boolean nt_item_tooltip$disableTooltipRendering(boolean hasItem)
    {
        if (CandyTweak.OLD_NO_ITEM_TOOLTIPS.get())
            return false;

        return hasItem;
    }
}
