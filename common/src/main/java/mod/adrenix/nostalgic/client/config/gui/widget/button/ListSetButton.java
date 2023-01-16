package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListSetScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.list.ListSet;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.util.common.ComponentBackport;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;

import java.util.Set;

/**
 * This control button redirects the current configuration screen to a list set screen instance that is associated with
 * a tweak cache entry.
 *
 * Since this control can be used to control lists used by the server, the permission lock interface has been attached.
 */

public class ListSetButton extends ControlButton implements PermissionLock
{
    /* On Press */

    /**
     * Goes to a list set screen that is associated with the provided tweak. Since lists are utilized by both the client
     * and server, using the common cache is necessary.
     *
     * @param tweak A tweak common cache instance.
     */
    private static void jumpToScreen(TweakCommonCache tweak)
    {
        ListSet listSet = ConfigList.getSetFromTweak(tweak);

        if (listSet == null)
            return;

        Minecraft.getInstance().setScreen(new ListSetScreen(tweak.getComponentTranslation(), listSet));
    }

    /* Constructor */

    /**
     * Create a new list set button controller. When this button is clicked, it will jump to a new list set screen.
     * @param tweak A tweak client cache that holds a set with item resource keys that point to a specific value.
     */
    public ListSetButton(TweakClientCache<Set<String>> tweak)
    {
        super(ComponentBackport.translatable(LangUtil.Gui.BUTTON_EDIT_LIST), (button) -> jumpToScreen(tweak));
    }
}
