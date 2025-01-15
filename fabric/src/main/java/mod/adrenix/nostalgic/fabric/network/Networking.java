package mod.adrenix.nostalgic.fabric.network;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.network.ModConnection;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;

public abstract class Networking
{
    /**
     * Registers Fabric networking.
     */
    public static void register()
    {
        ServerLoginConnectionEvents.QUERY_START.register(ServerNetwork::sendProtocolRequest);
        ServerLoginNetworking.registerGlobalReceiver(ModConnection.PROTOCOL_ID, ServerNetwork::receiveProtocol);
        EnvExecutor.runInEnv(Env.CLIENT, () -> ClientNetwork::register);
    }
}
