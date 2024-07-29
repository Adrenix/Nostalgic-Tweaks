package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakText;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundTweakText extends TweakTextPacket
{
    /* Constructors */

    public ClientboundTweakText(TweakText tweak)
    {
        super(tweak, tweak.fromDisk());
    }

    public ClientboundTweakText(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnClient(context, this.poolId, this.text);
    }
}
