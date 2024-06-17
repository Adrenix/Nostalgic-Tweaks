package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakFlag extends TweakFlagPacket
{
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
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.flag);
    }
}
