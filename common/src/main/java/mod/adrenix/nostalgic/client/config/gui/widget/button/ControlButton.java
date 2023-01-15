package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Control buttons are buttons that control a configuration value.
 *
 * The purpose of the class is to provide position and dimension details for controller widgets that are used by the
 * config row list {@link ConfigRowList}.
 */

public class ControlButton extends Button
{
    /* Constructor */

    /**
     * Create a new control button that is placed relative to values defined by the config row list.
     * @param title A component title for this control button.
     * @param onPress Instructions that are performed when the button is clicked.
     */
    public ControlButton(Component title, OnPress onPress)
    {
        super
        (
            ConfigRowList.getControlStartX(),
            ConfigRowList.BUTTON_START_Y,
            ConfigRowList.BUTTON_WIDTH,
            ConfigRowList.BUTTON_HEIGHT,
            title,
            onPress,
            DEFAULT_NARRATION
        );
    }
}
