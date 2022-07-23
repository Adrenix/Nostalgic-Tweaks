package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ControlButton extends Button
{
    public ControlButton(Component title, OnPress onPress)
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
