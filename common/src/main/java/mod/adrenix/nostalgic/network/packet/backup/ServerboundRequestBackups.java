package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Create a new "get a list of backups" packet instance. The request will be rejected if the sender of the packet is not
 * a server operator. The server will only acknowledge a request from a server operator.
 */
public record ServerboundRequestBackups() implements ModPacket
{
    /* Type */

    public static final Type<ServerboundRequestBackups> TYPE = ModPacket.createType(ServerboundRequestBackups.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param ignored A {@link FriendlyByteBuf} instance.
     */
    public ServerboundRequestBackups(FriendlyByteBuf ignored)
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
        if (this.isNotFromOperator(context))
            return;

        this.log("Player (%s) requested the mod's backup config files", this.getPlayerName(context));

        PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundBackupObjects());
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
