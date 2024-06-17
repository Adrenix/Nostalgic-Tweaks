package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.network.FriendlyByteBuf;

abstract class TweakFlagPacket implements TweakPacket
{
    /* Fields */

    protected final String poolId;
    protected final boolean flag;

    /* Constructors */

    /**
     * Prepare a tweak flag to be sent over the network.
     *
     * @param tweak A {@link TweakFlag} instance.
     * @param flag  The flag to be sent over the network.
     */
    TweakFlagPacket(TweakFlag tweak, boolean flag)
    {
        this.poolId = tweak.getJsonPathId();
        this.flag = flag;
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakFlagPacket(FriendlyByteBuf buffer)
    {
        this.poolId = buffer.readUtf();
        this.flag = buffer.readBoolean();
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeBoolean(this.flag);
    }
}
