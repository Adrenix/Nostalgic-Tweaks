package mod.adrenix.nostalgic.client.config.gui.widget.list;

import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public abstract class AbstractEntry<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList.Entry<E>
{
    /**
     * The mod's abstract row list implements its own method of focusing widgets. This override prevents the game from
     * disrupting that implementation.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.setFocused(null);
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
