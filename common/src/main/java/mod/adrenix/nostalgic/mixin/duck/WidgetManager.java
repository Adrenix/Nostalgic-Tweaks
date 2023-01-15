package mod.adrenix.nostalgic.mixin.duck;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * Adds the ability to manage widgets from screens.
 */

public interface WidgetManager
{
    <T extends GuiEventListener & Renderable>
    void NT$addRenderableWidget(T widget);
    void NT$removeWidget(GuiEventListener listener);
}
