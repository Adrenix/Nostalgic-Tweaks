package mod.adrenix.nostalgic.mixin.tweak.candy.create_world_screen;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.helper.candy.screen.WidgetHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TabNavigationBar.class)
public abstract class TabNavigationBarMixin
{
    /**
     * Disables the rendering of the header fill on the world creation screen if rendering the old style tabs.
     */
    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderFill(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int color)
    {
        return !WidgetHelper.isOldStyleTabs();
    }

    /**
     * Disables the rendering of the header on the world creation screen if rendering the old style tabs.
     */
    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderHeader(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
    {
        return !WidgetHelper.isOldStyleTabs();
    }
}
