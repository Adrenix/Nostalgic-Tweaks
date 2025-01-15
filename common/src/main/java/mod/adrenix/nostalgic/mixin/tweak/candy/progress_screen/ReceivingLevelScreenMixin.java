package mod.adrenix.nostalgic.mixin.tweak.candy.progress_screen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.progress.ProgressRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReceivingLevelScreen.class)
public abstract class ReceivingLevelScreenMixin extends Screen
{
    /* Fake Constructor */

    private ReceivingLevelScreenMixin(Component title)
    {
        super(title);
    }

    /* Injections */

    /**
     * Prevents the generic receiving level text and replaces it with the old receiving level header and simulation
     * message.
     */
    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
        )
    )
    private boolean nt_progress_screen$shouldRenderReceivingLevelText(GuiGraphics graphics, Font font, Component text, int x, int y, int color)
    {
        if (!CandyTweak.OLD_PROGRESS_SCREEN.get())
            return true;

        ProgressRenderer.drawHeaderText(graphics, Lang.Level.LOADING.get(), this.width);
        ProgressRenderer.drawStageText(graphics, Lang.Level.SIMULATE.get(), this.width);

        return false;
    }

    /**
     * Overrides the vanilla background with a dirt background if the old dirt background tweak is enabled.
     */
    @Inject(
        method = "renderBackground",
        at = @At("RETURN")
    )
    private void nt_progress_screen$onFinishBackgroundRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            GuiUtil.renderDirtBackground(graphics);
    }
}
