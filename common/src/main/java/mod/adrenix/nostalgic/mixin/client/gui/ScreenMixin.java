package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.WidgetManager;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements WidgetManager
{
    /* Shadows */

    @Shadow
    protected abstract void removeWidget(GuiEventListener listener);

    @Shadow
    protected abstract GuiEventListener addRenderableWidget(GuiEventListener widget);

    /* Widget Manager Implementation */

    @Override
    public <T extends GuiEventListener & Renderable> void NT$addRenderableWidget(T widget)
    {
        this.addRenderableWidget(widget);
    }

    @Override
    public void NT$removeWidget(GuiEventListener listener)
    {
        this.removeWidget(listener);
    }

    /* Injections */

    /**
     * Removes focus on the currently focused widget when the escape key is pressed. Controlled by the remove focus on
     * escape tweak.
     */
    @Inject(
        method = "keyPressed",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callback)
    {
        ComponentPath path = this.getCurrentFocusPath();

        if (ModConfig.Candy.removeFocusOnEscape() && keyCode == GLFW.GLFW_KEY_ESCAPE && path != null)
        {
            GuiEventListener focused = this.getFocused();

            if (focused != null && focused.isFocused())
            {
                if (focused instanceof AbstractWidget widget && (!widget.active || !widget.visible))
                    return;

                path.applyFocus(false);
                return;
            }

            path.applyFocus(false);
            callback.setReturnValue(true);
        }
    }

    /**
     * Changes the fill gradient background color. Controlled by various GUI background tweaks.
     */
    @Redirect(
        method = "renderBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"
        )
    )
    private void NT$onRenderBackground(GuiGraphics graphics, int x, int y, int w, int h, int colorFrom, int colorTo)
    {
        if (ModConfig.Candy.customGuiBackground())
        {
            int top = ColorUtil.toHexInt(ModConfig.Candy.customTopGradient());
            int bottom = ColorUtil.toHexInt(ModConfig.Candy.customBottomGradient());

            graphics.fillGradient(x, y, w, h, top, bottom);
        }
        else if (!ModConfig.Candy.oldGuiBackground().equals(TweakType.GuiBackground.SOLID_BLACK))
        {
            switch (ModConfig.Candy.oldGuiBackground())
            {
                case SOLID_BLUE -> graphics.fillGradient(x, y, w, h, 0xA0303060, 0xA0303060);
                case GRADIENT_BLUE -> graphics.fillGradient(x, y, w, h, 0x60050500, 0xA0303060);
            }
        }
        else
            graphics.fillGradient(x, y, w, h, colorFrom, colorTo);
    }
}