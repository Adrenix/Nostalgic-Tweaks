package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundTweakStringSet extends TweakStringSetPacket
{
    /* Type */

    public static final Type<ClientboundTweakStringSet> TYPE = ModPacket.createType(ClientboundTweakStringSet.class);

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
