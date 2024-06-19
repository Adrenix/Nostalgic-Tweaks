package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakNumber extends TweakNumberPacket
{
    /* Type */

    public static final Type<ClientboundTweakNumber> TYPE = ModPacket.createType(ClientboundTweakNumber.class);

    /* Constructor */

    public ClientboundTweakNumber(TweakNumber<? extends Number> tweak)
    {
        super(tweak, tweak.fromDisk());
    }

    public ClientboundTweakNumber(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        this.findOnClient(this.poolId)
            .ifPresent(tweak -> this.changeOnClient(context, this.poolId, this.getReceivedNumber(tweak)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
