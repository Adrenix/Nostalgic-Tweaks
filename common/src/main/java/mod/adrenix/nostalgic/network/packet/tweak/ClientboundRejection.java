package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.client.gui.toast.ToastNotification;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundRejection implements TweakPacket
{
    /* Constructors */

    /**
     * Prepare a LAN update rejection packet to be sent over the network.
     */
    public ClientboundRejection()
    {
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundRejection(FriendlyByteBuf buffer)
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
        if (this.isServerHandling(context))
            return;

        ToastNotification.hostRejectedChanges();
    }
}
