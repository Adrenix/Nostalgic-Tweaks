package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Create a new backup packet instance. You can choose whether the server responds with an acknowledgement packet that
 * will open a "success" overlay screen on the client. The server will check if the sender is an operator before
 * performing any I/O operations.
 *
 * @param respond Whether to respond with an acknowledgement packet.
 */
public record ServerboundCreateBackup(boolean respond) implements ModPacket
{
    /* Type */

    public static final Type<ServerboundCreateBackup> TYPE = ModPacket.createType(ServerboundCreateBackup.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A friendly byte buffer.
     */
    public ServerboundCreateBackup(final FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean());
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.respond);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        boolean success = ConfigBuilder.getHandler().backup();

        NostalgicTweaks.LOGGER.info("Player (%s) created a new config backup file", this.getPlayerName(context));

        if (this.respond)
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundMadeBackup(success));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
