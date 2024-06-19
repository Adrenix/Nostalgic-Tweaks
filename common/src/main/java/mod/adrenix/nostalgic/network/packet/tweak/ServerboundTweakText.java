package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.TweakText;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerboundTweakText extends TweakTextPacket
{
    /* Type */

    public static final Type<ServerboundTweakText> TYPE = ModPacket.createType(ServerboundTweakText.class);

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
    public void receiver(NetworkManager.PacketContext context)
    {
        this.changeOnServer(context, this.poolId, this.text);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
