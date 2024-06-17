package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundAppliedBackup implements ModPacket
{
    /* Fields */

    final boolean isError;

    /* Constructors */

    /**
     * Let the operator know if backup application was successful.
     *
     * @param success Whether the backup was successfully applied.
     */
    public ClientboundAppliedBackup(boolean success)
    {
        this.isError = !success;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundAppliedBackup(FriendlyByteBuf buffer)
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

        ExecuteOnClient.handleAppliedBackup(this);
    }
}
