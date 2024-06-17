package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundBackupDeleted implements ModPacket
{
    /* Fields */

    final boolean isError;

    /* Constructors */

    /**
     * Inform the client that the requested backup file to delete was acknowledged.
     *
     * @param success Whether deleting the file was successful on the server.
     */
    public ClientboundBackupDeleted(boolean success)
    {
        this.isError = !success;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundBackupDeleted(FriendlyByteBuf buffer)
    {
        this.isError = buffer.readBoolean();
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.isError);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleBackupDeleted(this);
    }
}
