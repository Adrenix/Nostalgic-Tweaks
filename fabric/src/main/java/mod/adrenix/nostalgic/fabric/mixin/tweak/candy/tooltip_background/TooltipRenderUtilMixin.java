package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.tooltip_background;

import mod.adrenix.nostalgic.listener.client.TooltipListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin
{
    /**
     * Renders the old semi-transparent black background with the correct dimensional size.
     */
    @Inject(
        method = "renderTooltipBackground",
        at = @At("HEAD")
    )
    private static void nt_fabric_tooltip_background$onRender(GuiGraphics graphics, int x, int y, int width, int height, int z, CallbackInfo callback)
    {
        TooltipListener.render(graphics, x, y, width, height, z);
    }
}
