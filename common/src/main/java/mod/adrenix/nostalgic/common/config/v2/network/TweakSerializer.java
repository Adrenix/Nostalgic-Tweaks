package mod.adrenix.nostalgic.common.config.v2.network;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.v2.tweak.Tweak;

import java.lang.reflect.Type;

/**
 * Prepares a cached server tweak for transmission over the network.
 */

public class TweakSerializer<T>
{
    /* Fields */

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();

    @Expose private final String cacheKey;
    @Expose private final T sendingValue;
    @Expose private final TweakStatus tweakStatus;

    private final Type tweakType;

    /* Constructor */

    public TweakSerializer(Tweak<T> tweak)
    {
        this.cacheKey = tweak.getCacheKey();
        this.sendingValue = tweak.getValue();
        this.tweakStatus = tweak.getStatus();

        this.tweakType = TypeToken.getParameterized(TweakSerializer.class, tweak.getValue().getClass()).getType();
    }

    /* Methods */

    /**
     * Convert this class into a serialized JSON string.
     * @return A JSON string.
     */
    public String serialize()
    {
        return GSON_BUILDER.create().toJson(this, this.tweakType);
    }

    /**
     * @return The tweak's cache map key.
     */
    public String getCacheKey()
    {
        return this.cacheKey;
    }

    /**
     * @return The tweak value being sent by the server.
     */
    public Object getSendingValue()
    {
        return this.sendingValue;
    }

    /**
     * Get the current status of the tweak being sent by the server.
     * @return A tweak status type enumeration value.
     */
    public TweakStatus getStatus()
    {
        return this.tweakStatus;
    }

    /**
     * Deserialize a JSON string into a readable {@link TweakSerializer} class.
     * @param json A JSON string.
     * @return A deserialized {@link TweakSerializer} class.
     */
    public static TweakSerializer<?> deserialize(String json)
    {
        TweakSerializer<?> sent = GSON_BUILDER.create().fromJson(json, TweakSerializer.class);
        Type valueType = TypeToken.getParameterized(TweakSerializer.class, Tweak.get(sent.getCacheKey()).getValue().getClass()).getType();

        return GSON_BUILDER.create().fromJson(json, valueType);
    }
}
