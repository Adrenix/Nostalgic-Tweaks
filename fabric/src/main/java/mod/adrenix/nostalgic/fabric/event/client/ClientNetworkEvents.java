package mod.adrenix.nostalgic.fabric.event.client;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import mod.adrenix.nostalgic.fabric.NostalgicCommonFabric;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Fabric networking related event instructions and registration.
 * Registration is invoked by the client event handler.
 */

public abstract class ClientNetworkEvents
{
    /**
     * Registers networking related Fabric events.
     */
    public static void register()
    {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientEventHelper.disconnect());
        ClientLoginNetworking.registerGlobalReceiver(NostalgicCommonFabric.VERIFY_PROTOCOL, ClientNetworkEvents::verify);
    }

    /**
     * Handles the mod protocol request from a server with Nostalgic Tweaks installed.
     * The connection will be cancelled if the network protocol versions do not match.
     */
    private static CompletableFuture<FriendlyByteBuf> verify
    (
        Minecraft minecraft,
        ClientHandshakePacketListenerImpl handler,
        FriendlyByteBuf buffer,
        Consumer<GenericFutureListener<? extends Future<? super Void>>> listener
    )
    {
        return CompletableFuture.completedFuture(PacketByteBufs.create().writeUtf(NostalgicTweaks.PROTOCOL));
    }
}
