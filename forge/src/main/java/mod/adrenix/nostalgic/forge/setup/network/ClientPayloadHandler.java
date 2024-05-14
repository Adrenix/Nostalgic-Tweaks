package mod.adrenix.nostalgic.forge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;

public class ClientPayloadHandler
{
    /**
     * Payload handlers need to be singletons.
     */
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    /**
     * @return The {@link ClientPayloadHandler} singleton instance.
     */
    public static ClientPayloadHandler getInstance()
    {
        return INSTANCE;
    }

    /* Handlers */

    /**
     * Handle receiving a network protocol request from the server.
     *
     * @param request The {@link ProtocolRequest} instance.
     * @param context The {@link ConfigurationPayloadContext} instance.
     */
    public void handleRequest(final ProtocolRequest request, final ConfigurationPayloadContext context)
    {
        context.replyHandler().send(new ProtocolResponse(NostalgicTweaks.PROTOCOL));
    }
}
