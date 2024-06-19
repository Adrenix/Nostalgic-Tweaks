package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakEnum extends TweakEnumPacket
{
    /* Type */

    public static final Type<ClientboundTweakEnum> TYPE = ModPacket.createType(ClientboundTweakEnum.class);

    /* Constructors */

    public ClientboundTweakEnum(TweakEnum<?> tweak)
    {
        super(tweak, TweakEnum::fromDisk);
    }

    public ClientboundTweakEnum(FriendlyByteBuf buffer)
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
            .ifPresent(tweak -> this.changeOnClient(context, this.poolId, this.getReceivedEnum(tweak)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
