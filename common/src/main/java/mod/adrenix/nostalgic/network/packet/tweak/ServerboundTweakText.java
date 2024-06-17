package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.tweak.factory.TweakText;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundTweakText extends TweakTextPacket
{
    /* Constructors */

    public ServerboundTweakText(TweakText tweak)
    {
        super(tweak, tweak.fromNetwork());
    }

    public ServerboundTweakText(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    /* Methods */

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.text);
    }
}
