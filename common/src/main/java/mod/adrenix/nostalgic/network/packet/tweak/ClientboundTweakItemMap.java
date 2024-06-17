package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakItemMap;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakItemMap extends TweakItemMapPacket
{
    /* Constructors */

    public ClientboundTweakItemMap(TweakItemMap<?> tweak)
    {
        super(tweak, TweakItemMap::fromDisk);
    }

    public ClientboundTweakItemMap(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.packager.getListingMap(this.map));
    }
}
