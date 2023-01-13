package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.list.ListMap;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Map;

/**
 * This control button redirects the current configuration screen to a list map screen instance that is associated with
 * a tweak cache entry.
 *
 * Since this control can be used to control lists used by the server, the permission lock interface has been attached.
 */

public class ListMapButton<V> extends ControlButton implements PermissionLock
{
    /* On Press */

    /**
     * Goes to a list map screen that is associated with the provided tweak. Since lists are utilized by both the client
     * and server, using the common cache is necessary.
     *
     * @param tweak A tweak common cache instance.
     */
    private static void jumpToScreen(TweakCommonCache tweak)
    {
        ListMap<?> listMap = ConfigList.getMapFromTweak(tweak);

        if (listMap == null)
            return;

        Minecraft.getInstance().setScreen(new ListMapScreen<>(tweak.getComponentTranslation(), listMap));
    }

    /* Constructor */

    /**
     * Create a new list map button controller. When this button is clicked, it will jump to a new list map screen.
     * @param tweak A tweak client cache that holds a map with item resource keys that point to a specific value.
     */
    public ListMapButton(TweakClientCache<Map<String, V>> tweak)
    {
        super(Component.translatable(LangUtil.Gui.BUTTON_EDIT_LIST), (button) -> jumpToScreen(tweak));
    }
}
