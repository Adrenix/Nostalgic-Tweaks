package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin extends AbstractContainerWidget
{
    /* Fake Constructor */

    private AbstractSelectionListMixin(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }

    /* Injections */

    /**
     * Darkens the shader color for the old dirt background in row lists.
     */
    @Inject(
        method = "renderListBackground",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"
        )
    )
    private void nt_gui_background$onEnableBlendForBackground(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
    }

    /**
     * Changes the row list background to the current dirt texture.
     */
    @ModifyArg(
        method = "renderListBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private ResourceLocation nt_gui_background$modifyListBackgroundTexture(ResourceLocation texture)
    {
        return CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get() ? TextureLocation.DIRT_BACKGROUND : texture;
    }

    /**
     * Restores the shader color after rendering the old dirt background.
     */
    @Inject(
        method = "renderListBackground",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"
        )
    )
    private void nt_gui_background$onDisableBlendForBackground(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Prevents the separators from rendering if the row list is using the old dirt background.
     */
    @WrapWithCondition(
        method = "renderListSeparators",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private boolean nt_gui_background$shouldRenderListSeparators(GuiGraphics graphics, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
    {
        return !CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get();
    }

    /**
     * Renders the old row list separator shadows if the row list is using the old dirt background.
     */
    @Inject(
        method = "renderListSeparators",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"
        )
    )
    private void nt_gui_background$renderOldListShadows(GuiGraphics graphics, CallbackInfo callback)
    {
        if (!CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            return;

        graphics.fillGradient(RenderType.guiOverlay(), this.getX(), this.getY(), this.getRight(), this.getY() + 4, 0xFF000000, 0, 0);
        graphics.fillGradient(RenderType.guiOverlay(), this.getX(), this.getBottom() - 4, this.getRight(), this.getBottom(), 0, 0xFF000000, 0);
    }
}
