package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakItemSet;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakItemSet extends TweakItemSetPacket
{
    /* Constructors */

    public ServerboundTweakItemSet(TweakItemSet tweak)
    {
        super(tweak, TweakItemSet::fromNetwork);
    }

    public ServerboundTweakItemSet(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.packager.getListingSet(this.set));
    }
}
