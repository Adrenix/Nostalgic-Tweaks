package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerboundTweakFlag extends TweakFlagPacket
{
    /* Type */

    public static final Type<ServerboundTweakFlag> TYPE = ModPacket.createType(ServerboundTweakFlag.class);

    /* Constructors */

    public ServerboundTweakFlag(TweakFlag tweak)
    {
        super(tweak, tweak.fromNetwork());
    }

    public ServerboundTweakFlag(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.flag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
