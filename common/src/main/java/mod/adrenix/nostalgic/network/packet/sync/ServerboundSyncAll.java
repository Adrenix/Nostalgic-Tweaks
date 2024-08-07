package mod.adrenix.nostalgic.network.packet.sync;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Send a request to the server to send its current tweak values so the client can be in sync with the server.
 */
public record ServerboundSyncAll() implements ModPacket
{
    /* Type */

    public static final Type<ServerboundSyncAll> TYPE = ModPacket.createType(ServerboundSyncAll.class);

    /* Decoder */

    /**
     * Decode a buffer received over the network.
     *
     * @param ignored A {@link FriendlyByteBuf} instance.
     */
    public ServerboundSyncAll(final FriendlyByteBuf ignored)
    {
        this();
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isClientHandling(context))
            return;

        final ServerPlayer player = this.getServerPlayer(context);

        TweakPool.filter(Tweak::isMultiplayerLike).forEach(tweak -> tweak.sendToPlayer(player));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
