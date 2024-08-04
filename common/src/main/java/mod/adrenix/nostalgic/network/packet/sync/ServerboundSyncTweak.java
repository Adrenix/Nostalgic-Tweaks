package mod.adrenix.nostalgic.network.packet.sync;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundSyncTweak implements TweakPacket
{
    /* Fields */

    private final String poolId;

    /* Constructors */

    /**
     * Send a single tweak sync request to the server.
     *
     * @param tweak The {@link Tweak} that needs synced.
     */
    public ServerboundSyncTweak(Tweak<?> tweak)
    {
        this.poolId = tweak.getJsonPathId();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundSyncTweak(FriendlyByteBuf buffer)
    {
        this.poolId = buffer.readUtf();
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isClientHandling(context))
            return;

        final ServerPlayer player = this.getServerPlayer(context);

        this.findOnServer(context, this.poolId).ifPresent(tweak -> tweak.sendToPlayer(player));
    }
}
