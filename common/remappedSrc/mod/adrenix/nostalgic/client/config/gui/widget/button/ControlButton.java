package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ControlButton extends ButtonWidget
{
    public ControlButton(Text title, PressAction onPress)
    {
        super
        (
            ConfigRowList.getControlStartX(),
            ConfigRowList.BUTTON_START_Y,
            ConfigRowList.BUTTON_WIDTH,
            ConfigRowList.BUTTON_HEIGHT,
            title,
            onPress
        );
    }
}
