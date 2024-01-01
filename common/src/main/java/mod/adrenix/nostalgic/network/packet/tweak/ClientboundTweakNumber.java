package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakNumber extends TweakNumberPacket
{
    /* Constructor */

    public ClientboundTweakNumber(TweakNumber<? extends Number> tweak)
    {
        super(tweak, tweak.fromDisk());
    }

    public ClientboundTweakNumber(FriendlyByteBuf buffer)
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
            .ifPresent(tweak -> this.changeOnClient(context, this.poolId, this.getReceivedNumber(tweak)));
    }
}
