package mod.adrenix.nostalgic.common.config.tweak;

import com.google.gson.Gson;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

/**
 * Prepares a cached server tweak for transmission over the network.
 * Any enumerations not defined in the serializer will need to be handled manually.
 *
 * @see mod.adrenix.nostalgic.common.config.tweak.TweakVersion
 */

public class TweakSerializer
{
    /* General Tweak Serialization */

    private static final Gson GSON = new Gson();
    private final String key;
    private final GroupType group;
    private final StatusType status;
    private Object value;

    /* Transmitted Numbers */

    private final boolean isByte;
    private final boolean isShort;
    private final boolean isInteger;
    private final boolean isLong;
    private final boolean isFloat;
    private final boolean isDouble;

    /* Transmitted Tweak Versions */

    private TweakVersion.Hotbar hotbar = null;

    /* Serializer */

    public TweakSerializer(TweakServerCache<?> cache)
    {
        this.key = cache.getKey();
        this.value = cache.getValue();
        this.group = cache.getGroup();
        this.status = cache.getStatus();

        this.isByte = this.value instanceof Byte;
        this.isShort = this.value instanceof Short;
        this.isInteger = this.value instanceof Integer;
        this.isLong = this.value instanceof Long;
        this.isFloat = this.value instanceof Float;
        this.isDouble = this.value instanceof Double;

        if (this.value instanceof TweakVersion.Hotbar)
            this.hotbar = (TweakVersion.Hotbar) this.value;
    }

    public String serialize() { return GSON.toJson(this); }
    public String getKey() { return this.key; }

    @SuppressWarnings("unchecked") // This value will be class checked before being applied
    public <T> T getValue() { return (T) this.value; }
    public void setValue(Object value) { this.value = value; }
    public GroupType getGroup() { return this.group; }
    public StatusType getStatus() { return this.status; }
    public TweakVersion.Hotbar getHotbar() { return this.hotbar; }

    public static TweakSerializer deserialize(String json)
    {
        TweakSerializer serializer = GSON.fromJson(json, TweakSerializer.class);

        // Numerical values gets sent over as doubles
        if (serializer.isByte)
            serializer.setValue(((Double) serializer.getValue()).byteValue());
        else if (serializer.isShort)
            serializer.setValue(((Double) serializer.getValue()).shortValue());
        else if (serializer.isInteger)
            serializer.setValue(((Double) serializer.getValue()).intValue());
        else if (serializer.isLong)
            serializer.setValue(((Double) serializer.getValue()).longValue());
        else if (serializer.isFloat)
            serializer.setValue(((Double) serializer.getValue()).floatValue());
        else if (serializer.isDouble)
            serializer.setValue(serializer.getValue());
        else if (serializer.getHotbar() != null)
            serializer.setValue(serializer.getHotbar());

        return serializer;
    }
}
