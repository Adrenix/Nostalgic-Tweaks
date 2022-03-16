package mod.adrenix.nostalgic.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.gui.screens.ProgressScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public abstract class ProgressScreenForgeMixin
{
    /**
     * Prevents the progress screen from rendering. This issue only appears when using Forge.
     * Controlled by the old loading screens toggle.
     */
    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    protected void onRenderProgress(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldLoadingScreens())
            callback.cancel();
    }
}
