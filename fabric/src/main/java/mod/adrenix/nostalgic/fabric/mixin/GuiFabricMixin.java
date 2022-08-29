package mod.adrenix.nostalgic.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiFabricMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;

    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay tweak.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void NT$onRender(PoseStack matrix, float f, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldVersionOverlay() && !this.minecraft.options.renderDebug)
            this.minecraft.font.drawShadow(matrix, ModConfig.Candy.getOverlayText(), 2.0F, 2.0F, 0xFFFFFF);
    }
}
