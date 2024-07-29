package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakItemSet;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakItemSet extends TweakItemSetPacket
{
    /* Constructors */

    public ClientboundTweakItemSet(TweakItemSet tweak)
    {
        super(tweak, TweakItemSet::fromDisk);
    }

    public ClientboundTweakItemSet(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.packager.getListingSet(this.set));
    }
}
