package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakFlag extends TweakFlagPacket
{
    /* Type */

    public static final Type<ClientboundTweakFlag> TYPE = ModPacket.createType(ClientboundTweakFlag.class);

    /* Constructors */

    public ClientboundTweakFlag(TweakFlag tweak)
    {
        super(tweak, tweak.fromDisk());
    }

    public ClientboundTweakFlag(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.flag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
