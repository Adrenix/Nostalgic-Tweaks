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
    @ModifyArg(method = "render", index = 1, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"))
    private int NT$onRenderBoxFill(int vanilla)
    {
        return ModConfig.Candy.oldChatBox() ? -2 : vanilla;
    }

    /**
     * Repositions the chat box text, so it is flush with the far left of the chat box.
     * Controlled by the old chat box tweak.
     */
    @ModifyArg(method = "render", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"))
    private float NT$onDrawMessagePosition(float vanilla)
    {
        return ModConfig.Candy.oldChatBox() ? -2.0F + (float) ModConfig.Candy.getChatOffset() : vanilla;
    }

    /**
     * Prevents the text from fading out with the chat box. Similar to how it appeared in the old days.
     * Controlled by the old chat box tweak.
     */
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"))
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
