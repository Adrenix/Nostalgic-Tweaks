package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Inform the client whether the server was successful in removing all config backup files.
 *
 * @param success Whether deleting all backup files on the server was successful.
 */
public record ClientboundDeletedAllBackups(boolean success) implements ModPacket
{
    /* Type */

    public static final Type<ClientboundDeletedAllBackups> TYPE = ModPacket.createType(ClientboundDeletedAllBackups.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundDeletedAllBackups(final FriendlyByteBuf buffer)
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

        ExecuteOnClient.handleDeletedAll(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
