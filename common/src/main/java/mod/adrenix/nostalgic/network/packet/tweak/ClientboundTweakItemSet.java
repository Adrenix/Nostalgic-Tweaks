package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakItemSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakItemSet extends TweakItemSetPacket
{
    /* Type */

    public static final Type<ClientboundTweakItemSet> TYPE = ModPacket.createType(ClientboundTweakItemSet.class);

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
    public void receiver(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.packager.getListingSet(this.set));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
