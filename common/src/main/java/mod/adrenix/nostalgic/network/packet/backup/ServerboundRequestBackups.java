package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;

/**
 * This packet makes a request to the server for a list of the mod's backup config files. The server will only
 * acknowledge a request from a server operator.
 */
public class ServerboundRequestBackups implements ModPacket
{
    /* Constructors */

    /**
     * Create a new "get a list of backups" packet instance. The request will be rejected if the sender of the packet is
     * not a server operator.
     */
    public ServerboundRequestBackups()
    {
    }

    /**
     * Decode a packet received over the network.
     *
     * @param ignored A {@link FriendlyByteBuf} instance.
     */
    public ServerboundRequestBackups(FriendlyByteBuf ignored)
    {
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        this.log("Player (%s) requested the mod's backup config files", this.getPlayerName(context));

        PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundBackupObjects());
    }
}
