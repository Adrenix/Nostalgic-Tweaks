package mod.adrenix.nostalgic.forge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.LoginReply;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class ServerPayloadHandler implements IPayloadHandler<ProtocolResponse>
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
     * @param context  The {@link IPayloadContext} instance.
     */
    @Override
    public void handle(ProtocolResponse response, IPayloadContext context)
    {
        final String CLIENT_PROTOCOL = response.version();
        final String SERVER_PROTOCOL = NostalgicTweaks.PROTOCOL;

        if (!CLIENT_PROTOCOL.equals(SERVER_PROTOCOL))
            context.disconnect(LoginReply.getProtocolMismatchReason(CLIENT_PROTOCOL, SERVER_PROTOCOL));

        context.finishCurrentTask(ProtocolConfigurationTask.TYPE);
    }
}
