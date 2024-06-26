package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Let the operator know if backup application was successful.
 *
 * @param success Whether the backup was successfully applied.
 */
public record ClientboundAppliedBackup(boolean success) implements ModPacket
{
    /* Type */

    public static final Type<ClientboundAppliedBackup> TYPE = ModPacket.createType(ClientboundAppliedBackup.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundAppliedBackup(final FriendlyByteBuf buffer)
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

        ExecuteOnClient.handleAppliedBackup(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
