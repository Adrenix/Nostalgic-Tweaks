package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakItemSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerboundTweakItemSet extends TweakItemSetPacket
{
    /* Type */

    public static final Type<ServerboundTweakItemSet> TYPE = ModPacket.createType(ServerboundTweakItemSet.class);

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
    public void receiver(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.packager.getListingSet(this.set));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
