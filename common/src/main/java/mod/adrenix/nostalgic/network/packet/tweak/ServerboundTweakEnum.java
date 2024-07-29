package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakEnum extends TweakEnumPacket
{
    /* Constructors */

    public ServerboundTweakEnum(TweakEnum<?> tweak)
    {
        super(tweak, TweakEnum::fromNetwork);
    }

    public ServerboundTweakEnum(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        this.findOnServer(context, this.poolId)
            .ifPresent(tweak -> this.changeOnServer(context, this.poolId, this.getReceivedEnum(tweak)));
    }
}
