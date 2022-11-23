package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.network.chat.Component;

/**
 * This control button toggles a boolean state that is connected to a client tweak cache entry.
 * Since this control can be used to toggle server tweaks, the permission lock interface has been attached.
 */

public class BooleanButton extends ControlButton implements PermissionLock
{
    /* Fields */

    private final TweakClientCache<Boolean> tweak;

    /* Constructor */

    /**
     * Creates a new boolean button controller for a tweak.
     * @param tweak A tweak client cache entry.
     * @param onPress Instructions to perform when clicked.
     */
    public BooleanButton(TweakClientCache<Boolean> tweak, OnPress onPress)
    {
        super(Component.empty(), onPress);
        this.tweak = tweak;
    }

    /* Methods */

    /**
     * This message will either be "Yes" or "No".
     * @return The message component associated with this controller.
     */
    @Override
    public Component getMessage()
    {
        return Component.translatable(this.tweak.getValue() ? LangUtil.Gui.BUTTON_YES : LangUtil.Gui.BUTTON_NO);
    }
}
