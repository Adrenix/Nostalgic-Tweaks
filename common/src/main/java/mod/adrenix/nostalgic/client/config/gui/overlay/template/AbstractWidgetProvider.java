package mod.adrenix.nostalgic.client.config.gui.overlay.template;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

import java.util.Set;

/**
 * An abstract widget provider is responsible for creating and adding widgets to an overlay. Any overlay that utilizes
 * this class will not need to add widgets as that will be done by a widget provider.
 */

public abstract class AbstractWidgetProvider
{
    /* Widgets */

    public Set<Renderable> children = Set.of();

    /* Methods */

    /**
     * Create and add widgets to an overlay window. Any widget fields will be defined here, added to an overlay's
     * abstract widgets array list, and added to this provider's widget children set list.
     */
    public abstract void generate();

    /**
     * Render all the children of this widget provider.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        for (Renderable widget : this.children)
            widget.render(graphics, mouseX, mouseY, partialTick);
    }
}
