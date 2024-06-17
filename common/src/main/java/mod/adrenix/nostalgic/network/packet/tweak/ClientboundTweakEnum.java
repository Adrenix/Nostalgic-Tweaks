package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakEnum extends TweakEnumPacket
{
    /* Constructors */

    public ClientboundTweakEnum(TweakEnum<?> tweak)
    {
        super(tweak, TweakEnum::fromDisk);
    }

    public ClientboundTweakEnum(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        this.findOnClient(this.poolId)
            .ifPresent(tweak -> this.changeOnClient(context, this.poolId, this.getReceivedEnum(tweak)));
    }
}
