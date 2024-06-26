package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerboundTweakStringSet extends TweakStringSetPacket
{
    /* Type */

    public static final Type<ServerboundTweakStringSet> TYPE = ModPacket.createType(ServerboundTweakStringSet.class);

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
