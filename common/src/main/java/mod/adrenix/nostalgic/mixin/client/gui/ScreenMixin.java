package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.WidgetManager;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
            path.applyFocus(false);

            if (focused != null && focused.isFocused())
                return;

            callback.setReturnValue(true);
        }
    }
}