package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.client.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Prepare a LAN update rejection packet to be sent over the network.
 */
public record ClientboundRejection() implements TweakPacket
{
    /* Type */

    public static final Type<ClientboundRejection> TYPE = ModPacket.createType(ClientboundRejection.class);

    /* Decoder */

    /**
     * Decode a buffer received over the network.
     *
     * @param ignored A {@link FriendlyByteBuf} instance.
     */
    public ClientboundRejection(FriendlyByteBuf ignored)
    {
        this();
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ToastNotification.hostRejectedChanges();
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
