package mod.adrenix.nostalgic.client.config.gui.widget.list;

import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.Optional;

/**
 * The mod's abstract row list implements its own method of focusing widgets. These overrides prevent the game from
 * disrupting that implementation.
 */

public abstract class AbstractEntry<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList.Entry<E>
{
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        Optional<GuiEventListener> child = this.getChildAt(mouseX, mouseY);

        if (child.isPresent() && this.isDragging() && button == 0)
            return child.get().mouseDragged(mouseX, mouseY, button, dragX, dragY);

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        this.setFocused(null);

        return clicked;
    }
}
