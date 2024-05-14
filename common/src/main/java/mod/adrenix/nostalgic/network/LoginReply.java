package mod.adrenix.nostalgic.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class LoginReply
{
    /**
     * Get a protocol mismatch message.
     *
     * @param CLIENT_PROTOCOL The client's protocol version number.
     * @param SERVER_PROTOCOL The server's protocol version number.
     * @return The {@link Component} reason to reply with the client and server network protocol do not match.
     */
    public static Component getProtocolMismatchReason(final String CLIENT_PROTOCOL, final String SERVER_PROTOCOL)
    {
        final MutableComponent MOD_NAME = new Color(0xFFFF00).apply(NostalgicTweaks.MOD_NAME);
        final MutableComponent MOD_LINK = new Color(0x11BDED).apply(LinkLocation.DOWNLOAD);
        final MutableComponent CLIENT_NUMBER = new Color(0xF87C73).apply(CLIENT_PROTOCOL);
        final MutableComponent SERVER_NUMBER = new Color(0xF8BE73).apply(SERVER_PROTOCOL);
        final MutableComponent SERVER_VERSION = new Color(0xFAEEAA).apply(NostalgicTweaks.getFullVersion());

        String message = """
                 Your %s protocol (%s) did not match the server protocol (%s).
                 Network protocols must match; however, mod versions don't need to match.

                 This server is running %s (%s).
                 %s
            """;

        return Component.translatable(message, MOD_NAME, CLIENT_NUMBER, SERVER_NUMBER, MOD_NAME, SERVER_VERSION, MOD_LINK);
    }

    /**
     * Get the missing mod message.
     *
     * @return The {@link Component} reason to reply with when the client is missing the mod.
     */
    public static Component getMissingModReason()
    {
        final MutableComponent SERVER_VERSION = Color.SUMMER_YELLOW.apply(NostalgicTweaks.getFullVersion());
        final MutableComponent MOD_NAME = Color.YELLOW.apply(NostalgicTweaks.MOD_NAME);
        final MutableComponent MOD_LINK = Color.LIGHT_BLUE.apply(LinkLocation.DOWNLOAD);
        final MutableComponent SSO_MODE = Color.PURPLE_PLUM.apply("Server-Side-Only");
        final MutableComponent CONFIG_MANAGE = Color.ATOMIC_TANGERINE.apply("Config Management");
        String message = "You need %s (%s) to join this server. Or ask the server admin to enable %s mode in %s.\n%s";

        return Component.translatable(message, MOD_NAME, SERVER_VERSION, SSO_MODE, CONFIG_MANAGE, MOD_LINK);
    }
}
