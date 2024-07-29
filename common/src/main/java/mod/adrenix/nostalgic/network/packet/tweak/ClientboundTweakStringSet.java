package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakStringSet extends TweakStringSetPacket
{
    /* Constructors */

    public ClientboundTweakStringSet(TweakStringSet tweak)
    {
        super(tweak, TweakStringSet::fromDisk);
    }

    public ClientboundTweakStringSet(FriendlyByteBuf buffer)
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
