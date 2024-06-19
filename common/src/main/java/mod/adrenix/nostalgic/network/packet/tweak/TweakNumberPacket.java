package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.network.FriendlyByteBuf;

abstract class TweakNumberPacket implements TweakPacket
{
    /* Fields */

    protected final String poolId;
    protected final double doubleValue;

    /* Constructors */

    /**
     * Prepare a tweak number to be sent over the network.
     *
     * @param tweak  A {@link TweakNumber} instance.
     * @param number The number to be sent over the network.
     */
    TweakNumberPacket(TweakNumber<? extends Number> tweak, Number number)
    {
        this.poolId = tweak.getJsonPathId();
        this.doubleValue = number.doubleValue();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakNumberPacket(FriendlyByteBuf buffer)
    {
        this.poolId = buffer.readUtf();
        this.doubleValue = buffer.readDouble();
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeDouble(this.doubleValue);
    }

    /**
     * Retrieve the correct number implementation (int, float, double, etc.) from the given tweak. This does not perform
     * any checks whether the returned generic class type from the tweak extends the number class.
     *
     * @param tweak A {@link Tweak} instance.
     * @return All number-like tweaks sent over the network are expanded to doubles and are parsed back to the correct
     * number type based on the number used by the given tweak.
     */
    Number getReceivedNumber(Tweak<?> tweak)
    {
        return MathUtil.getNumberFromType(tweak.getGenericType(), this.doubleValue);
    }
}
