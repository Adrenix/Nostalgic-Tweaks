package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerboundTweakNumber extends TweakNumberPacket
{
    /* Type */

    public static final Type<ServerboundTweakNumber> TYPE = ModPacket.createType(ServerboundTweakNumber.class);

    /* Constructors */

    public ServerboundTweakNumber(TweakNumber<? extends Number> tweak)
    {
        super(tweak, tweak.fromNetwork());
    }

    public ServerboundTweakNumber(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        this.findOnServer(context, this.poolId)
            .ifPresent(tweak -> this.changeOnServer(context, this.poolId, this.getReceivedNumber(tweak)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
