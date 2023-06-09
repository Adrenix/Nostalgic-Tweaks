package mod.adrenix.nostalgic.fabric.mixin.client;

import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiFabricMixin
{
    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay tweak.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void NT$onRender(GuiGraphics graphics, float f, CallbackInfo callback)
    {
        GuiUtil.renderOverlays(graphics);
    }
}
