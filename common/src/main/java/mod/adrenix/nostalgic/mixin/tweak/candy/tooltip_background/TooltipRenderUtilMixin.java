package mod.adrenix.nostalgic.mixin.tweak.candy.tooltip_background;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.candy.screen.TooltipHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin
{
    /**
     * Prevents rendering the vanilla tooltip sprite backgrounds based on tweak context.
     */
    @WrapWithCondition(
        method = "renderTooltipBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private static boolean nt_tooltip_background$shouldRenderSprites(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !CandyTweak.OLD_TOOLTIP_BOXES.get();
    }

    /**
     * Renders the old tooltip background based on tweak context.
     */
    @Inject(
        method = "renderTooltipBackground",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
        )
    )
    private static void nt_tooltip_background$onTranslateGraphics(GuiGraphics graphics, int x, int y, int width, int height, int z, ResourceLocation sprite, CallbackInfo callback)
    {
        TooltipHelper.render(graphics, x, y, width, height, z);
    }
}
