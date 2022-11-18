package mod.adrenix.nostalgic.util.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is only used by the client. The server should never interface with this.
 * Therefore, it is safe to use vanilla client code here.
 */

public abstract class LinkUtil
{
    /* Static Fields */

    public static final String DISCORD = "https://discord.gg/jWdfVh3";
    public static final String KO_FI = "https://ko-fi.com/adrenix";
    public static final String GOLDEN_DAYS = "https://github.com/PoeticRainbow/golden-days/releases";

    /* Static Methods */

    /**
     * Provides logic that handles button presses with URL jumps.
     * @param url The URL the button is referencing.
     * @return A handler method for when a button is pressed.
     */
    public static Button.OnPress onPress(String url)
    {
        return button -> Minecraft.getInstance().setScreen(LinkUtil.confirm(Minecraft.getInstance().screen, url));
    }

    /* Private Utility */

    /**
     * Generates a new confirm link screen instance.
     * @param parent The parent screen to return to.
     * @param url The URL to jump to and display.
     * @return A new confirm link screen instance.
     */
    private static ConfirmLinkScreen confirm(Screen parent, String url)
    {
        return new ConfirmLinkScreen(accepted -> LinkUtil.jump(parent, url, accepted), url, true);
    }

    /**
     * If the link jump is accepted, the URL is opened in the user's default browser.
     * Otherwise, the game returns to the parent screen.
     * @param parent The parent screen to return to.
     * @param url The URL to jump to and display.
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
                exception.printStackTrace();
            }
        }

        Minecraft.getInstance().setScreen(parent);
    }
}
