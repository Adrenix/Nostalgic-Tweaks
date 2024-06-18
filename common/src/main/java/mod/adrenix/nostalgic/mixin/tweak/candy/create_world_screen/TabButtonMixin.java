package mod.adrenix.nostalgic.mixin.tweak.candy.create_world_screen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.mixin.util.candy.WidgetMixinHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabButton.class)
public abstract class TabButtonMixin extends AbstractWidget
{
    /* Fake Constructor */

    private TabButtonMixin(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }

    /* Shadow */

    @Shadow
    public abstract boolean isSelected();

    /* Injections */

    /**
     * Disables the rendering of tab button sprites on the world creation screen if rendering the old style tabs.
     */
    @WrapWithCondition(
        method = "renderWidget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !WidgetMixinHelper.isOldStyleTabs();
    }

    /**
     * Disables the rendering of tab button string rendering on the world creation screen if rendering the old style
     * tabs.
     */
    @WrapWithCondition(
        method = "renderWidget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/TabButton;renderString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderString(TabButton button, GuiGraphics graphics, Font font, int color)
    {
        return !WidgetMixinHelper.isOldStyleTabs();
    }

    /**
     * Disables the rendering of the tab button underline on the world creation screen if rendering the old style tabs.
     */
    @WrapWithCondition(
        method = "renderWidget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/TabButton;renderFocusUnderline(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderUnderline(TabButton button, GuiGraphics graphics, Font font, int color)
    {
        return !WidgetMixinHelper.isOldStyleTabs();
    }

    /**
     * Modifies the rendering of the tab button on the world creation screen if rendering the old style tabs.
     */
    @Inject(
        method = "renderWidget",
        at = @At("HEAD")
    )
    private void nt_create_world_screen$onRenderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (WidgetMixinHelper.isOldStyleTabs())
            WidgetMixinHelper.renderOldStyleTabs(this, graphics, this.isSelected());
    }
}
