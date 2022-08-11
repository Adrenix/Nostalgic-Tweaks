package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.screen.CustomizeScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.Map;

public class ItemButton extends ButtonWidget
{
    public CustomizeScreen screen;
    public Map.Entry<String, Integer> entry;

    public ItemButton(CustomizeScreen screen, Map.Entry<String, Integer> entry, int startX, int startY, int width, int height)
    {
        super(startX, startY, width, height, Text.empty(), (ignored) -> {});
        this.screen = screen;
        this.entry = entry;
        this.active = false;
    }
}