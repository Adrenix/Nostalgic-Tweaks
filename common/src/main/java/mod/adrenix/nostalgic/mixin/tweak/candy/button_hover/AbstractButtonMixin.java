package mod.adrenix.nostalgic.mixin.tweak.candy.button_hover;

import mod.adrenix.nostalgic.mixin.util.candy.WidgetMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget
{
    /* Fake Constructor */

    private AbstractButtonMixin(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }

    /* Injections */

    /**
     * Modifies the button's text color.
     */
    @ModifyArg(
        index = 3,
        method = "renderString",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractButton;renderScrollingString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;II)V"
        )
    )
    private int nt_button_hover$textColor(int color)
    {
        if (!CandyTweak.OLD_BUTTON_TEXT_COLOR.get())
            return color;

        return WidgetMixinHelper.getTextColor(this, this.alpha);
    }
}
