package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakMobMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakMobMap extends TweakMobMapPacket
{
    /* Type */

    public static final Type<ClientboundTweakMobMap> TYPE = ModPacket.createType(ClientboundTweakMobMap.class);

    /* Constructors */

    public ClientboundTweakMobMap(TweakMobMap<?> tweak)
    {
        super(tweak, TweakMobMap::fromDisk);
    }

    public ClientboundTweakMobMap(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.packager.getListingMap(this.map));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
