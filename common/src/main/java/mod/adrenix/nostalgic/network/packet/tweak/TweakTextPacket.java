package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.TweakText;
import net.minecraft.network.FriendlyByteBuf;

abstract class TweakTextPacket implements TweakPacket
{
    /* Fields */

    protected final String poolId;
    protected final String text;

    /* Constructors */

    /**
     * Prepare a text tweak to be sent over the network.
     *
     * @param tweak A {@link TweakText} instance.
     * @param text  The text to be sent over the network.
     */
    TweakTextPacket(TweakText tweak, String text)
    {
        this.poolId = tweak.getJsonPathId();
        this.text = text;
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakTextPacket(FriendlyByteBuf buffer)
    {
        this.poolId = buffer.readUtf();
        this.text = buffer.readUtf();
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeUtf(this.text);
    }
}
