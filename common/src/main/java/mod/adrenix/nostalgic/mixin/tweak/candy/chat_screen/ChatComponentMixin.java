package mod.adrenix.nostalgic.mixin.tweak.candy.chat_screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin
{
    /**
     * Repositions the chat box so that it is flush with the chat input box.
     */
    @ModifyArg(
        index = 0,
        method = "render",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
        )
    )
    private int nt_chat_screen$setFillMinX(int minX)
    {
        return CandyTweak.OLD_CHAT_BOX.get() ? -2 : minX;
    }

    /**
     * Repositions the chat box text so that it is flush with the far left of the chat input box.
     */
    @ModifyArg(
        index = 2,
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"
        )
    )
    private int nt_chat_screen$setDrawStringX(int x)
    {
        return CandyTweak.OLD_CHAT_BOX.get() ? -2 + CandyTweak.CHAT_OFFSET.get() : x;
    }

    /**
     * Prevents line content text from fading out with the chat box.
     */
    @ModifyArg(
        index = 4,
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"
        )
    )
    private int nt_chat_screen$setLineContentColor(int color)
    {
        return CandyTweak.OLD_CHAT_BOX.get() ? 0xFFFFFF : color;
    }

    /**
     * Prevents the chat queue text from fading out with the chat box.
     */
    @ModifyArg(
        index = 4,
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"
        )
    )
    private int nt_chat_screen$setQueueColor(int color)
    {
        return CandyTweak.OLD_CHAT_BOX.get() ? 0xFFFFFF : color;
    }

    /**
     * Prevents the addition of colored chat signature rectangles next to chat messages.
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/GuiMessage$Line;tag()Lnet/minecraft/client/GuiMessageTag;"
        )
    )
    private GuiMessageTag nt_chat_screen$setMessageTag(GuiMessageTag tag)
    {
        return CandyTweak.DISABLE_SIGNATURE_BOXES.get() ? null : tag;
    }
}
