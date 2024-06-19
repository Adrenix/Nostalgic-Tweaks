package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakItemMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakItemMap extends TweakItemMapPacket
{
    /* Type */

    public static final Type<ClientboundTweakItemMap> TYPE = ModPacket.createType(ClientboundTweakItemMap.class);

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
