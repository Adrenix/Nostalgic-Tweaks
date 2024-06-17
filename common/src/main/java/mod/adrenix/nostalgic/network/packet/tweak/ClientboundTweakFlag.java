package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakFlag extends TweakFlagPacket
{
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
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.flag);
    }
}
