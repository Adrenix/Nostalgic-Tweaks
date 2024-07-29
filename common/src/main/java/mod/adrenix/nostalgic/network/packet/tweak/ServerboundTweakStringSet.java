package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakStringSet extends TweakStringSetPacket
{
    /* Constructors */

    public ServerboundTweakStringSet(TweakStringSet tweak)
    {
        super(tweak, TweakStringSet::fromNetwork);
    }

    public ServerboundTweakStringSet(FriendlyByteBuf buffer)
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
