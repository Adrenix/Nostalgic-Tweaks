package mod.adrenix.nostalgic.common.config.tweak;

import com.google.gson.Gson;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Prepares a cached server tweak for transmission over the network.
 * Any enumerations not defined in the serializer will need to be handled manually.
 *
 * @see mod.adrenix.nostalgic.common.config.tweak.TweakVersion
 */

public class TweakSerializer
{
    /**
     * This Gson instance is used to convert a JSON string into a readable tweak serializer class.
     * @see TweakSerializer#deserialize(String)
     */
    private static final Gson GSON = new Gson();

    /* Fields */

    private final String key;
    private final TweakGroup group;
    private final TweakStatus status;
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

    /* Transmitted Tweak Lists */

    private ListId listId = null;
    private Set<String> disabledDefaults = null;

    private boolean isListByte = false;
    private boolean isListShort = false;
    private boolean isListInteger = false;
    private boolean isListLong = false;
    private boolean isListFloat = false;
    private boolean isListDouble = false;

    /* Constructor */

    /**
     * Create a new tweak serializer instance.
     * @param tweak A tweak server cache instance.
     */
    public TweakSerializer(TweakServerCache<?> tweak)
    {
        this.key = tweak.getKey();
        this.value = tweak.getValue();
        this.group = tweak.getGroup();
        this.status = tweak.getStatus();

        this.isByte = this.value instanceof Byte;
        this.isShort = this.value instanceof Short;
        this.isInteger = this.value instanceof Integer;
        this.isLong = this.value instanceof Long;
        this.isFloat = this.value instanceof Float;
        this.isDouble = this.value instanceof Double;

        if (this.value instanceof TweakVersion.Hotbar)
            this.hotbar = (TweakVersion.Hotbar) this.value;

        if (tweak.getList() != null)
        {
            this.listId = tweak.getList().id();

            if (this.value instanceof Set<?>)
            {
                this.value = ConfigList.getSetFromId(this.listId).getConfigSet();
                this.disabledDefaults = ConfigList.getSetFromId(this.listId).getDisabledDefaults();
            }
            else if (this.value instanceof Map<?, ?>)
            {
                this.value = ConfigList.getMapFromId(this.listId).getConfigMap();
                this.disabledDefaults = ConfigList.getMapFromId(this.listId).getDisabledDefaults();

                Optional<?> first = ((Map<?, ?>) this.value).values().stream().findFirst();

                if (first.isPresent())
                {
                    Object value = first.get();

                    this.isListByte = value instanceof Byte;
                    this.isListShort = value instanceof Short;
                    this.isListInteger = value instanceof Integer;
                    this.isListLong = value instanceof Long;
                    this.isListFloat = value instanceof Float;
                    this.isListDouble = value instanceof Double;
                }
            }
        }
    }

    /* Methods */

    /**
     * Convert this class into a serialized JSON string.
     * @return A JSON string.
     */
    public String serialize() { return GSON.toJson(this); }

    /**
     * Get the tweak server cache identifier.
     * @return Tweak server cache key.
     */
    public String getKey() { return this.key; }

    /**
     * Get the value associated with this serializer.
     * @return The value kept in the tweak server cache instance.
     * @param <T> The class type associated with the tweak value.
     */
    @SuppressWarnings("unchecked") // This value will be class checked before being applied
    public <T> T getValue() { return (T) this.value; }

    /**
     * Set the value for this serializer.
     * @param value An object value.
     */
    public void setValue(Object value) { this.value = value; }

    /**
     * Get the group type associated with this serializer.
     * @return A group type enumeration value.
     */
    public TweakGroup getGroup() { return this.group; }

    /**
     * Get the status type associated with this serializer.
     * @return A status type enumeration value.
     */
    public TweakStatus getStatus() { return this.status; }

    /**
     * Get the list identifier associated with this serializer.
     * @return A list identifier enumeration value.
     */
    public ListId getListId() { return this.listId; }

    /**
     * Get the disabled default item resource keys associated with this serializer.
     * @return A set of disabled default item resource keys.
     */
    public Set<String> getDisabledDefaults() { return this.disabledDefaults; }

    /**
     * Get the hotbar tweak version associated with this serializer.
     * @return A tweak version hotbar type enumeration value.
     */
    public TweakVersion.Hotbar getHotbar() { return this.hotbar; }

    /**
     * Convert a transmitted map into proper number values.
     * @param map The map to convert.
     * @param converter A function that accepts a double and returns the required number type.
     * @param <R> The number type that will be outputted by the converter function.
     */
    private static <R> void setMapValues(Map<Object, Object> map, Function<Double, R> converter)
    {
        for (Map.Entry<Object, Object> entry : map.entrySet())
            entry.setValue(converter.apply((Double) entry.getValue()));
    }

    /**
     * Deserialize a JSON string into a readable {@link TweakSerializer} class.
     * @param json A JSON string.
     * @return A deserialized {@link TweakSerializer} class.
     */
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

        // Numerical map values get sent over as doubles as well
        if (serializer.getValue() instanceof Map<?, ?>)
        {
            Map<Object, Object> map = serializer.getValue();

            if (serializer.isListByte)
                setMapValues(map, Double::byteValue);
            else if (serializer.isListShort)
                setMapValues(map, Double::shortValue);
            else if (serializer.isListInteger)
                setMapValues(map, Double::intValue);
            else if (serializer.isListLong)
                setMapValues(map, Double::longValue);
            else if (serializer.isListFloat)
                setMapValues(map, Double::floatValue);
            else if (serializer.isListDouble)
                setMapValues(map, Double::doubleValue);
        }

        return serializer;
    }
}
