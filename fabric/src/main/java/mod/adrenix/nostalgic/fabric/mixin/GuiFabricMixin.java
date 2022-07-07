package mod.adrenix.nostalgic.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.gui.Gui;
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
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void NT$onRender(PoseStack poseStack, float f, CallbackInfo callback)
    {
        ModClientUtil.Gui.renderOverlays(poseStack);
    }
}
