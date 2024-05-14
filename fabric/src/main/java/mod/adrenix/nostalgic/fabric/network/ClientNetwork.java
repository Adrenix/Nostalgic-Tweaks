package mod.adrenix.nostalgic.fabric.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.ModConnection;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

abstract class ClientNetwork
{
    /**
     * Registers the global protocol receiver on the client.
     */
    static void register()
    {
        ClientLoginNetworking.registerGlobalReceiver(ModConnection.PROTOCOL_ID, ClientNetwork::replyWithProtocol);
    }

    /**
     * Handles a Fabric server's request to know what network protocol this version of the mod is using. The player will
     * be disconnected if the protocols do not match even if the server is in server-side-only mode.
     */
    private static CompletableFuture<FriendlyByteBuf> replyWithProtocol(Minecraft minecraft, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buffer, Consumer<GenericFutureListener<? extends Future<? super Void>>> listener)
    {
        return CompletableFuture.completedFuture(PacketByteBufs.create().writeUtf(NostalgicTweaks.PROTOCOL));
    }
}
