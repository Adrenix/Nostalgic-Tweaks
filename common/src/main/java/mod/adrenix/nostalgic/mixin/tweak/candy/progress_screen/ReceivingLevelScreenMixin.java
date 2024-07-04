package mod.adrenix.nostalgic.mixin.tweak.candy.progress_screen;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.progress.ProgressRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
