package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakItemMap;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakItemMap extends TweakItemMapPacket
{
    /* Constructors */

    public ServerboundTweakItemMap(TweakItemMap<?> tweak)
    {
        super(tweak, TweakItemMap::fromNetwork);
    }

    public ServerboundTweakItemMap(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.packager.getListingMap(this.map));
    }
}
