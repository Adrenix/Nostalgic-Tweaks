package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericMessageScreen.class)
public abstract class GenericMessageScreenMixin extends Screen
{
    /* Fake Constructor */

    private GenericMessageScreenMixin(Component title)
    {
        super(title);
    }

    /* Shadows */

    @Shadow @Nullable private FocusableTextWidget textWidget;

    /* Injections */

    /**
     * Prevents the initialization of the generic message screen's text widget.
     */
    @Inject(
        method = "init",
        at = @At("RETURN")
    )
    private void nt_gui_background$modifyGenericMessageText(CallbackInfo callback)
    {
        if (!CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get() || this.textWidget == null)
            return;

        this.removeWidget(this.textWidget);

        this.textWidget = null;
    }

    /**
     * Renders the generic message screen's message in the center of the screen.
     */
    @Inject(
        method = "renderBackground",
        at = @At("RETURN")
    )
    private void nt_gui_background$renderCenteredGenericMessage(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            graphics.drawCenteredString(this.font, this.title, this.width / 2, 70, 0xFFFFFF);
    }
}
