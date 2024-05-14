package mod.adrenix.nostalgic.forge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.LoginReply;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD
)
public abstract class NetworkSetup
{
    /**
     * Registers the protocol response payload for networking. This is set as optional so the server can control when to
     * allow players with or without the mod.
     *
     * @param event The {@link RegisterPayloadHandlerEvent} instance.
     */
    @SubscribeEvent
    private static void registerPayload(final RegisterPayloadHandlerEvent event)
    {
        event.registrar(NostalgicTweaks.MOD_ID)
            .optional()
            .configuration(ProtocolRequest.IDENTIFIER, ProtocolRequest::new, handler -> handler.client(ClientPayloadHandler.getInstance()::handleRequest))
            .configuration(ProtocolResponse.IDENTIFIER, ProtocolResponse::new, handler -> handler.server(ServerPayloadHandler.getInstance()::handleProtocol));
    }

    /**
     * Registers the mod network protocol configuration task.
     *
     * @param event The {@link OnGameConfigurationEvent} instance.
     */
    @SubscribeEvent
    private static void registerTask(final OnGameConfigurationEvent event)
    {
        if (NostalgicTweaks.isServer() || NetUtil.isLocalHost())
        {
            boolean canRequest = event.getListener().isConnected(ProtocolRequest.IDENTIFIER);
            boolean canRespond = event.getListener().isConnected(ProtocolResponse.IDENTIFIER);

            if (canRequest && canRespond)
                event.register(new ProtocolConfigurationTask(event.getListener()));
            else if (!ModTweak.SERVER_SIDE_ONLY.get())
                event.getListener().disconnect(LoginReply.getMissingModReason());
        }
    }
}
