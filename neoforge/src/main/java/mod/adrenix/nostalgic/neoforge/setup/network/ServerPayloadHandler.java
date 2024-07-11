package mod.adrenix.nostalgic.neoforge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.LoginReply;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;

public class ServerPayloadHandler
{
    /**
     * Payload handlers need to be singletons.
     */
    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    /**
     * @return The {@link ServerPayloadHandler} singleton instance.
     */
    public static ServerPayloadHandler getInstance()
    {
        return INSTANCE;
    }

    /* Handlers */

    /**
     * Handle receiving the mod network protocol on the server.
     *
     * @param response The {@link ProtocolResponse} instance.
     * @param context  The {@link ConfigurationPayloadContext} instance.
     */
    public void handleProtocol(final ProtocolResponse response, final ConfigurationPayloadContext context)
    {
        final String CLIENT_PROTOCOL = response.version();
        final String SERVER_PROTOCOL = NostalgicTweaks.PROTOCOL;

        if (!CLIENT_PROTOCOL.equals(SERVER_PROTOCOL))
            context.packetHandler().disconnect(LoginReply.getProtocolMismatchReason(CLIENT_PROTOCOL, SERVER_PROTOCOL));

        context.taskCompletedHandler().onTaskCompleted(ProtocolConfigurationTask.TYPE);
    }
}
