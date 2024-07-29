package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;

/**
 * This packet instructs the server to create a backup file of its current tweak configuration. The server will check if
 * the sender is an operator before performing any I/O operations.
 */
public class ServerboundCreateBackup implements ModPacket
{
    /* Fields */

    private final boolean respond;

    /* Constructors */

    /**
     * Create a new backup packet instance. You can choose whether the server responds with an acknowledgement packet
     * that will open a "success" overlay screen on the client.
     *
     * @param respond Whether to respond with an acknowledgement packet.
     */
    public ServerboundCreateBackup(boolean respond)
    {
        this.respond = respond;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A friendly byte buffer.
     */
    public ServerboundCreateBackup(FriendlyByteBuf buffer)
    {
        this.respond = buffer.readBoolean();
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.respond);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        boolean successful = ConfigBuilder.getHandler().backup();
        NostalgicTweaks.LOGGER.info("Player (%s) created a new config backup file", this.getPlayerName(context));

        if (this.respond)
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundMadeBackup(successful));
    }
}
