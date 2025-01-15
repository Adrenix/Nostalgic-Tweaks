package mod.adrenix.nostalgic.network.packet.tweak;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

abstract class TweakEnumPacket implements TweakPacket
{
    /* Fields */

    protected final String poolId;
    protected final String enumJson;

    /* Constructors */

    /**
     * Prepare a tweak enum to be sent over the network.
     *
     * @param tweak  A {@link TweakEnum} instance.
     * @param reader A {@link Function} that accepts a {@link TweakEnum} and returns a {@link Enum} to get data from.
     */
    TweakEnumPacket(TweakEnum<?> tweak, Function<TweakEnum<?>, Enum<?>> reader)
    {
        this.poolId = tweak.getJsonPathId();
        this.enumJson = new Gson().toJson(reader.apply(tweak));
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakEnumPacket(FriendlyByteBuf buffer)
    {
        this.poolId = buffer.readUtf();
        this.enumJson = buffer.readUtf();
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeUtf(this.enumJson);
    }

    /**
     * Retrieve the generic type data using the given tweak. This does not perform any checks whether the returned
     * generic class type from the tweak extends an enum.
     *
     * @param tweak A {@link Tweak} instance.
     * @return An {@link Object} that will represent the enum class used by the given tweak.
     */
    @Nullable
    Object getReceivedEnum(Tweak<?> tweak)
    {
        try
        {
            return new Gson().fromJson(this.enumJson, tweak.getGenericType());
        }
        catch (JsonSyntaxException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not parse enum for [tweak={jsonId:%s}]\n%s", this.poolId, exception);
            return null;
        }
    }
}
