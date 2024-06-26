package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.item_display;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /**
     * Makes the selected item name background transparent.
     */
    @ModifyArg(
        index = 5,
        method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I"
        )
    )
    private int nt_item_display$modifySelectedItemNameBackground(int color)
    {
        if (!CandyTweak.OLD_NO_SELECTED_ITEM_NAME.get())
            return color;

        return 0;
    }

    /**
     * Disables or removes formatting of the selected item name text.
     */
    @ModifyArg(
        method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I"
        )
    )
    private Component nt_item_display$modifySelectedItemNameComponent(Component text)
    {
        if (CandyTweak.OLD_NO_SELECTED_ITEM_NAME.get())
            return Component.empty();

        if (CandyTweak.OLD_PLAIN_SELECTED_ITEM_NAME.get() && text instanceof MutableComponent mutable)
            return mutable.setStyle(Style.EMPTY);

        return text;
    }
}
