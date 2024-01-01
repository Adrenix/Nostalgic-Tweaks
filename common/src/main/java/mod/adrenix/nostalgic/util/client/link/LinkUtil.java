package mod.adrenix.nostalgic.util.client.link;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class LinkUtil
{
    /**
     * Provides logic that handles button presses with URL jumps.
     *
     * @param url The URL the button is referencing.
     * @return A handler method for when a button is pressed.
     */
    public static Runnable onPress(String url)
    {
        return () -> Minecraft.getInstance().setScreen(LinkUtil.confirm(Minecraft.getInstance().screen, url));
    }

    /**
     * Generates a new confirm link screen instance.
     *
     * @param parent The parent screen to return to.
     * @param url    The URL to jump to and display.
     * @return A new confirm link screen instance.
     */
    private static ConfirmLinkScreen confirm(Screen parent, String url)
    {
        return new ConfirmLinkScreen(accepted -> LinkUtil.jump(parent, url, accepted), url, true);
    }

    /**
     * If the link jump is accepted, the URL is opened in the user's default browser. Otherwise, the game returns to the
     * parent screen.
     *
     * @param parent   The parent screen to return to.
     * @param url      The URL to jump to and display.
     * @param accepted Whether the URL should be opened.
     */
    private static void jump(Screen parent, String url, boolean accepted)
    {
        if (accepted)
        {
            try
            {
                Util.getPlatform().openUrl(new URL(url));
            }
            catch (MalformedURLException exception)
            {
                NostalgicTweaks.LOGGER.error("[URL Exception] Could not open URL (%s)\n%s", url, exception);
            }
        }

        Minecraft.getInstance().setScreen(parent);
    }
}
