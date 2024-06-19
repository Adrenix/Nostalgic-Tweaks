package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Inform the client that the requested backup file to delete was acknowledged.
 *
 * @param success Whether deleting the file was successful on the server.
 */
public record ClientboundBackupDeleted(boolean success) implements ModPacket
{
    /* Type */

    public static final Type<ClientboundBackupDeleted> TYPE = ModPacket.createType(ClientboundBackupDeleted.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundBackupDeleted(final FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean());
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.success);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleBackupDeleted(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
