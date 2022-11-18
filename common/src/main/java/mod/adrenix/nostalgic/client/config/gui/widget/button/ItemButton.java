package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.screen.SwingScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.Map;

/**
 * This is a simple button class that is used to assist the rendering of item sprites on the swing screen row list.
 * Rendering of these sprites are handled by the speed row list renderer.
 */

public class ItemButton extends Button
{
    /* Fields */

    public SwingScreen screen;
    public Map.Entry<String, Integer> entry;

    /* Constructor */

    /**
     * Create a new item button tracker instance.
     *
     * Since this button is used exclusively by the speed row list, a starting y-position is omitted from the
     * constructor.
     *
     * @param screen A swing screen instance.
     * @param entry A configuration swing speed entry.
     * @param startX The starting x-position of this button.
     */
    public ItemButton(SwingScreen screen, Map.Entry<String, Integer> entry, int startX)
    {
        super(startX, 0, 20, 20, Component.empty(), (ignored) -> {});

        this.screen = screen;
        this.entry = entry;
        this.active = false;
    }
}