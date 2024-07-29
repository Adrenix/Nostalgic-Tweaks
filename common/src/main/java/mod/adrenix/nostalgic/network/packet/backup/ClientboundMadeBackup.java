package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundMadeBackup implements ModPacket
{
    /* Fields */

    final boolean successful;

    /* Constructors */

    /**
     * Create a new "server made backup file" packet instance. Depending on whether the backup was successful will
     * change the type of overlay screen seen on the client.
     *
     * @param successful Whether the backup file was created successfully.
     */
    public ClientboundMadeBackup(boolean successful)
    {
        this.successful = successful;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundMadeBackup(FriendlyByteBuf buffer)
    {
        this.successful = buffer.readBoolean();
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.successful);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleMadeBackup(this);
    }
}
