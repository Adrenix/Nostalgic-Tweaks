package mod.adrenix.nostalgic.network.packet.sync;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundSyncTweak(String poolId) implements TweakPacket
{
    /* Type */

    public static final Type<ServerboundSyncTweak> TYPE = ModPacket.createType(ServerboundSyncTweak.class);

    /* Constructors */

    /**
     * Send a single tweak sync request to the server.
     *
     * @param tweak The {@link Tweak} that needs synced.
     */
    public ServerboundSyncTweak(Tweak<?> tweak)
    {
        this(tweak.getJsonPathId());
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundSyncTweak(FriendlyByteBuf buffer)
    {
        this(buffer.readUtf());
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isClientHandling(context))
            return;

        final ServerPlayer player = this.getServerPlayer(context);

        this.findOnServer(context, this.poolId).ifPresent(tweak -> tweak.sendToPlayer(player));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
