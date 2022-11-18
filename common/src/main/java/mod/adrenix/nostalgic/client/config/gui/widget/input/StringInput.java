package mod.adrenix.nostalgic.client.config.gui.widget.input;

import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * A string input instance provides logic for an input box widget. This logic includes automatic value placement, max
 * length limits, and input responders. Because some tweaks have string values that are used by the server, a permission
 * lock interface has been attached to this class.
 */

public class StringInput implements PermissionLock
{
    /* Fields */

    private final EditBox input;
    private final TweakClientCache<String> tweak;

    /* Constructor */

    /**
     * Create a new string input instance.
     * @param tweak A tweak that uses a string as its value.
     */
    public StringInput(TweakClientCache<String> tweak)
    {
        this.tweak = tweak;
        this.input = new EditBox
        (
            Minecraft.getInstance().font,
            ConfigRowList.getControlStartX(),
            0,
            ConfigRowList.BUTTON_WIDTH - 2,
            ConfigRowList.BUTTON_HEIGHT - 2,
            Component.empty()
        );

        this.input.setMaxLength(100);
        this.input.setBordered(true);
        this.input.setVisible(true);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.tweak.getValue());
        this.input.setResponder(this::setInput);
    }

    /* Methods */

    /**
     * Get the internally used edit box widget. The {@link StringInput} class is not a widget that can be interacted
     * with. Use this method when you need to set a property of the input box or need a widget instance to add.
     *
     * @return The edit box widget instance for this widget.
     */
    public AbstractWidget getWidget() { return this.input; }

    /**
     * Set the input for the internal edit box.
     * @param input The string to set within the edit box.
     */
    public void setInput(String input)
    {
        String cached = ClientReflect.getCurrent(this.tweak.getGroup(), this.tweak.getKey());

        if (cached.equals(input))
            this.tweak.setValue(cached);
        else
            this.tweak.setValue(input);
    }
}
