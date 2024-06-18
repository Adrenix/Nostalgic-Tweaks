package mod.adrenix.nostalgic.forge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.LoginReply;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD
)
public abstract class NetworkSetup
{
    /**
     * Registers the protocol response payload for networking. This is set as optional so the server can control when to
     * allow players with or without the mod.
     *
     * @param event The {@link RegisterPayloadHandlersEvent} instance.
     */
    @SubscribeEvent
    private static void registerPayload(final RegisterPayloadHandlersEvent event)
    {
        event.registrar(NostalgicTweaks.MOD_ID)
            .optional()
            .configurationToClient(ProtocolRequest.TYPE, ProtocolRequest.CODEC, ClientPayloadHandler.getInstance())
            .configurationToServer(ProtocolResponse.TYPE, ProtocolResponse.CODEC, ServerPayloadHandler.getInstance());
    }

    /**
     * Registers the mod network protocol configuration task.
     *
     * @param event The {@link RegisterConfigurationTasksEvent} instance.
     */
    @SubscribeEvent
    private static void registerTask(final RegisterConfigurationTasksEvent event)
    {
        if (NostalgicTweaks.isServer() || NetUtil.isLocalHost())
        {
            boolean canRequest = event.getListener().hasChannel(ProtocolRequest.IDENTIFIER);
            boolean canRespond = event.getListener().hasChannel(ProtocolResponse.IDENTIFIER);

            if (canRequest && canRespond)
                event.register(new ProtocolConfigurationTask(event.getListener()));
            else if (!ModTweak.SERVER_SIDE_ONLY.get())
                event.getListener().disconnect(LoginReply.getMissingModReason());
        }
    }
}
