package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin
{
    /**
     * Repositions the chat box, so it is flush with the chat input box.
     * Controlled by the old chat box tweak.
     */
    @ModifyArg(method = "render", index = 0, at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/client/gui/GuiGraphics.fill(IIIII)V"))
    private int NT$onRenderBoxFill(int vanilla)
    {
        return ModConfig.Candy.oldChatBox() ? -2 : vanilla;
    }

    /**
     * Repositions the chat box text, so it is flush with the far left of the chat box.
     * Controlled by the old chat box tweak.
     */
    @ModifyArg(method = "render", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
    private int NT$onDrawMessagePosition(int vanilla)
    {
        return ModConfig.Candy.oldChatBox() ? -2 + ModConfig.Candy.getChatOffset() : vanilla;
    }

    /**
     * Prevents the text from fading out with the chat box. Similar to how it appeared in the old days.
     * Controlled by the old chat box tweak.
     */
    @ModifyArg(method = "render", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
    private int NT$onDrawMessageColor(int vanilla)
    {
        return ModConfig.Candy.oldChatBox() ? 0xFFFFFF : vanilla;
    }

    /**
     * Prevents the addition of colored chat signature rectangles next to chat messages.
     * Controlled by the disable chat signature boxes tweak.
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage$Line;tag()Lnet/minecraft/client/GuiMessageTag;"))
    private GuiMessageTag NT$onDrawMessageTag(GuiMessage.Line line)
    {
        return ModConfig.Candy.disableSignatureBoxes() ? null : line.tag();
    }
}
