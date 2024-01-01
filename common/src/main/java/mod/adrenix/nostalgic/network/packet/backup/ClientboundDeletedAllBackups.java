package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundDeletedAllBackups implements ModPacket
{
    /* Fields */

    final boolean isError;

    /* Constructor */

    /**
     * Inform the client whether the server was successful in removing all config backup files.
     *
     * @param success Whether deleting all backup files on the server was successful.
     */
    public ClientboundDeletedAllBackups(boolean success)
    {
        this.isError = !success;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundDeletedAllBackups(FriendlyByteBuf buffer)
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

        ExecuteOnClient.handleDeletedAll(this);
    }
}
