package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerboundTweakEnum extends TweakEnumPacket
{
    /* Type */

    public static final Type<ServerboundTweakEnum> TYPE = ModPacket.createType(ServerboundTweakEnum.class);

    /* Constructors */

    public ServerboundTweakEnum(TweakEnum<?> tweak)
    {
        super(tweak, TweakEnum::fromNetwork);
    }

    public ServerboundTweakEnum(FriendlyByteBuf buffer)
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
            .ifPresent(tweak -> this.changeOnServer(context, this.poolId, this.getReceivedEnum(tweak)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
