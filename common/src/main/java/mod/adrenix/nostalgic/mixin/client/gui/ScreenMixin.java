package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.mixin.duck.WidgetManager;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public abstract class ScreenMixin implements WidgetManager
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
}