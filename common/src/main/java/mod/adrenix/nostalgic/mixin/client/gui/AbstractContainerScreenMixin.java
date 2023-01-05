package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen
{
    /* Shadows */

    @Shadow protected int titleLabelX;
    @Shadow protected int titleLabelY;

    /* Dummy Constructor */

    protected AbstractContainerScreenMixin(Component component) { super(component); }

    /* Injections */

    /**
     * Changes the rendering of labels depending on the current Minecraft screen.
     * Controlled by various tweaks.
     */
    @Inject(method = "renderLabels", at = @At("HEAD"), cancellable = true)
    private void NT$onRenderLabels(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo callback)
    {
        if (Minecraft.getInstance().screen instanceof AnvilScreen && ModConfig.Candy.oldAnvilScreen())
        {
            this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 0x404040);
            callback.cancel();
        }
    }
}