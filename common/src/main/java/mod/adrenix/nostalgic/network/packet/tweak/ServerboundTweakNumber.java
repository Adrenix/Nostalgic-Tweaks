package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakNumber extends TweakNumberPacket
{
    /* Constructors */

    public ServerboundTweakNumber(TweakNumber<? extends Number> tweak)
    {
        super(tweak, tweak.fromNetwork());
    }

    public ServerboundTweakNumber(FriendlyByteBuf buffer)
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
            .ifPresent(tweak -> this.changeOnServer(context, this.poolId, this.getReceivedNumber(tweak)));
    }
}
