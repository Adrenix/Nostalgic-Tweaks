package mod.adrenix.nostalgic.common.config.tweak;

import com.google.gson.Gson;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public class TweakSerializer
{
    /* General Tweak Serialization */

    private static final Gson GSON = new Gson();
    private final String key;
    private final GroupType group;
    private final StatusType status;
    private Object value;

    /* Transmitted Tweak Versions */

    private TweakVersion.Hotbar hotbar = null;

    /* Serializer */

    public TweakSerializer(TweakServerCache<?> cache)
    {
        this.key = cache.getKey();
        this.value = cache.getValue();
        this.group = cache.getGroup();
        this.status = cache.getStatus();

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

        if (serializer.getHotbar() != null)
            serializer.setValue(serializer.getHotbar());

        return serializer;
    }
}
