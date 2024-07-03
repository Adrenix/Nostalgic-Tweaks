package mod.adrenix.nostalgic.neoforge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class ClientPayloadHandler implements IPayloadHandler<ProtocolRequest>
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
     * @param context The {@link IPayloadContext} instance.
     */
    @Override
    public void handle(ProtocolRequest request, IPayloadContext context)
    {
        context.reply(new ProtocolResponse(NostalgicTweaks.PROTOCOL));
    }
}
