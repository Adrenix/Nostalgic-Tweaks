package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /**
     * Disables the rendering of the selected item name above the hotbar.
     * Controlled by the old selected item name toggle.
     */
    @Inject(method = "renderSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    protected void onRenderSelectedItemName(PoseStack poseStack, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldNoSelectedItemName())
            callback.cancel();
    }

    /**
     * Removes the chat formatting of the selected item above the hotbar.
     * Controlled by the old plain selected item name toggle.
     */
    @Inject(
        method = "renderSelectedItemName",
        locals = LocalCapture.CAPTURE_FAILSOFT,
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"
        )
    )
    protected void onDrawSelectedItemName(PoseStack poseStack, CallbackInfo callback, MutableComponent mutableComponent)
    {
        if (MixinConfig.Candy.oldPlainSelectedItemName())
            mutableComponent.withStyle(ChatFormatting.RESET);
    }
}
