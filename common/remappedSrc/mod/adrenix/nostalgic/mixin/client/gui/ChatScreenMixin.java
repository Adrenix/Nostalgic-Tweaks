package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen
{
    /* Shadows */

    @Shadow protected TextFieldWidget input;

    /* Dummy Constructor */

    private ChatScreenMixin(Text ignored)
    {
        super(ignored);
    }

    /**
     * Moves the input position further to the right to account for the new '>' symbol.
     * Controlled by the old chat input tweak.
     */
    @Inject(method = "init", at = @At("RETURN"))
    private void NT$onInitInput(CallbackInfo callback)
    {
        this.input.setX(ModConfig.Candy.oldChatInput() ? 12 : 4);
        this.input.setWidth(ModConfig.Candy.oldChatInput() ? this.width - 21 : this.width - 4);
        this.input.setEditableColor(0xFFFFFF);
    }

    /**
     * Adds a '>' to the beginning of the chat input box.
     * Controlled by the old chat input tweak.
     */
    @Inject(method = "render", at = @At("RETURN"))
    private void NT$onRender(MatrixStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldChatInput())
            return;
        ChatScreen.drawStringWithShadow(poseStack, MinecraftClient.getInstance().textRenderer, ">", 4, this.height - 12, 0xFFFFFF);
    }
}
