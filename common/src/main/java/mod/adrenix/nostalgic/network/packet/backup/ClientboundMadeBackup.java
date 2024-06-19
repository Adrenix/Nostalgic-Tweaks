package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Create a new "server made backup file" packet instance. Depending on whether the backup was successful will change
 * the type of overlay screen seen on the client.
 *
 * @param success Whether the backup file was created successfully.
 */
public record ClientboundMadeBackup(boolean success) implements ModPacket
{
    /* Type */

    public static final Type<ClientboundMadeBackup> TYPE = ModPacket.createType(ClientboundMadeBackup.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundMadeBackup(final FriendlyByteBuf buffer)
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

        ExecuteOnClient.handleMadeBackup(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
