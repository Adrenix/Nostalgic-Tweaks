package mod.adrenix.nostalgic.mixin.tweak.candy.chat_screen;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen
{
    /* Fake Constructor */

    protected ChatScreenMixin(Component title)
    {
        super(title);
    }

    /* Shadows */

    @Shadow protected EditBox input;

    /* Injections */

    /**
     * Moves the input position further to the right to account for the new ">" symbol.
     */
    @Inject(
        method = "init",
        at = @At("RETURN")
    )
    private void nt_chat_screen$onScreenInit(CallbackInfo callback)
    {
        if (!CandyTweak.OLD_CHAT_INPUT.get())
            return;

        this.input.setX(12);
        this.input.setWidth(this.width - 21);
        this.input.setTextColor(0xFFFFFF);
    }

    /**
     * Adds a ">" symbol to the beginning of the chat input box.
     */
    @Inject(
        method = "render",
        at = @At("RETURN")
    )
    private void nt_chat_screen$onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (!CandyTweak.OLD_CHAT_INPUT.get())
            return;

        graphics.drawString(this.font, ">", 4, this.height - 12, 0xFFFFFF);
    }
}
