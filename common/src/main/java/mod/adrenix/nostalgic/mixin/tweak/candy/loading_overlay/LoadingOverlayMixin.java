package mod.adrenix.nostalgic.mixin.tweak.candy.loading_overlay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.candy.LoadingOverlayHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.Overlay;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin
{
    /* Shadows */

    @Shadow private long fadeOutStart;
    @Shadow @Final public static ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION;

    /* Injections */

    /**
     * Prevents the title screen from rendering until the fade out is finished.
     */
    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
        )
    )
    private boolean nt_loading_overlay$shouldRenderTitleScreen(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        float fadeOut = this.fadeOutStart > -1L ? (float) (Util.getMillis() - this.fadeOutStart) / 1000.0F : -1.0F;

        return CandyTweak.OLD_LOADING_OVERLAY.get() == Overlay.MODERN || fadeOut >= 2.0F;
    }

    /**
     * Renders the old Mojang logo before the progress bar is rendered. This injection happens after the modern logo is
     * rendered. While rendering the logo twice isn't efficient, it does prevent mod conflicts and other issues from
     * occurring from an injection override.
     */
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ReloadInstance;getActualProgress()F"
        )
    )
    private void nt_loading_overlay$onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (CandyTweak.OLD_LOADING_OVERLAY.get() == Overlay.MODERN)
            return;

        LoadingOverlayHelper.render(graphics, MOJANG_STUDIOS_LOGO_LOCATION);
    }

    /**
     * This keeps the progress rendered until the fade out is done and the overlay is ready to close.
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(
            ordinal = 1,
            value = "CONSTANT",
            args = "floatValue=1.0F"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/server/packs/resources/ReloadInstance;getActualProgress()F"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;drawProgressBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"
            )
        )
    )
    private float nt_loading_overlay$setAlphaForProgress(float alpha)
    {
        return CandyTweak.OLD_LOADING_OVERLAY.get() != Overlay.MODERN ? 3.0F : alpha;
    }

    /**
     * Prevents the progress bar from rendering, if needed.
     */
    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;drawProgressBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"
        )
    )
    private boolean nt_loading_overlay$shouldRenderProgressBar(LoadingOverlay overlay, GuiGraphics graphics, int minX, int minY, int maxX, int maxY, float partialTick)
    {
        return !CandyTweak.REMOVE_LOADING_BAR.get();
    }

    /**
     * Changes the bottom-y position of the progress bar.
     */
    @ModifyVariable(
        method = "drawProgressBar",
        argsOnly = true,
        ordinal = 1,
        at = @At("HEAD")
    )
    private int nt_loading_overlay$setProgressBarMinY(int minY)
    {
        return CandyTweak.OLD_LOADING_OVERLAY.get() != Overlay.MODERN ? LoadingOverlayHelper.getProgressBarOffset() - 5 : minY;
    }

    /**
     * Changes the top-y position of the progress bar.
     */
    @ModifyVariable(
        method = "drawProgressBar",
        argsOnly = true,
        ordinal = 3,
        at = @At("HEAD")
    )
    private int nt_loading_overlay$setProgressBarMaxY(int maxY)
    {
        return CandyTweak.OLD_LOADING_OVERLAY.get() != Overlay.MODERN ? LoadingOverlayHelper.getProgressBarOffset() + 5 : maxY;
    }

    /**
     * Changes the outline color of the progress bar.
     */
    @ModifyExpressionValue(
        method = "drawProgressBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/ARGB;color(IIII)I"
        )
    )
    private int nt_loading_overlay$setOutlineProgressBarColor(int color)
    {
        return ModTweak.ENABLED.get() ? LoadingOverlayHelper.getOutlineProgressBarColor() : color;
    }

    /**
     * Changes the inside color of the progress bar.
     */
    @ModifyArg(
        method = "drawProgressBar",
        index = 4,
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
        )
    )
    private int nt_loading_overlay$setInsideProgressBarColor(int color)
    {
        return ModTweak.ENABLED.get() ? LoadingOverlayHelper.getInsideProgressBarColor() : color;
    }
}
