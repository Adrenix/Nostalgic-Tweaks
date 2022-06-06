package mod.adrenix.nostalgic.client.config.reflect;

import mod.adrenix.nostalgic.client.config.annotation.TweakEntry;
import mod.adrenix.nostalgic.util.MixinUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TweakCache<T>
{
    private static final HashMap<String, TweakCache<?>> cache = new HashMap<>();
    private final String key;
    private final GroupType group;
    private StatusType status;
    private T value;
    private int order;
    private TweakEntry.Gui.Position position;

    private static String generateKey(GroupType group, String key) { return group.toString() + "@" + key; }
    static
    {
        Arrays.stream(GroupType.values()).forEach((group) ->
            ConfigReflect.getGroup(group).forEach((key, value) -> {
                if (ConfigReflect.getAnnotation(group, key, TweakEntry.Gui.Ignore.class) == null)
                    TweakCache.cache.put(generateKey(group, key), new TweakCache<>(group, key, value));
            })
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> TweakCache<T> get(GroupType group, String key)
    {
        return (TweakCache<T>) cache.get(generateKey(group, key));
    }

    private TweakCache(GroupType group, String key, T value)
    {
        this.group = group;
        this.key = key;
        this.value = value;
        this.status = StatusType.FAIL;

        TweakEntry.Gui.EntryStatus status = ConfigReflect.getAnnotation(group, key, TweakEntry.Gui.EntryStatus.class);
        if (status != null)
            this.status = status.status();

        TweakEntry.Gui.Placement placement = ConfigReflect.getAnnotation(group, key, TweakEntry.Gui.Placement.class);
        if (placement != null)
        {
            this.position = placement.pos();
            this.order = placement.order();
        }
    }

    public static HashMap<String, TweakCache<?>> all() { return cache; }
    public static int getConflicts()
    {
        AtomicInteger found = new AtomicInteger();
        TweakCache.all().forEach((key, tweak) -> {
            if (tweak.getStatus() == StatusType.FAIL)
                found.getAndIncrement();
        });

        return found.get();
    }

    @Nullable public int getOrder() { return this.order; }
    @Nullable public TweakEntry.Gui.Position getPosition() { return this.position; }

    public T getDefault() { return ConfigReflect.getDefault(this.group, this.key); }
    public T getCurrent() { return this.value; }
    public void setCurrent(T value) { this.value = value; }

    public GroupType getGroup() { return this.group; }
    public StatusType getStatus() { return this.status; }
    public void setStatus(StatusType status) { this.status = status; }

    public void reset() { this.value = getDefault(); }
    public void undo() { this.value = ConfigReflect.getCurrent(this.group, this.key); }
    public void save()
    {
        if (this.isSavable())
        {
            TweakEntry.Run.ReloadChunks chunks = ConfigReflect.getAnnotation(this.group, this.key, TweakEntry.Run.ReloadChunks.class);
            if (chunks != null)
                MixinUtil.Run.reloadChunks = true;
        }

        ConfigReflect.setConfig(this.group, this.key, this.value);
    }

    public boolean isResettable()
    {
        T current = this.getCurrent();
        T def = this.getDefault();

        // This check is required since comparison on the generics appears to check the integers as if they were bytes.
        if (current instanceof Integer && def instanceof Integer)
            return ((Integer) current).compareTo((Integer) def) != 0;
        else if (current instanceof String && def instanceof String)
            return !current.equals(def);
        return this.getCurrent() != this.getDefault();
    }

    public boolean isSavable()
    {
        T current = this.getCurrent();
        T cache = ConfigReflect.getCurrent(this.group, this.key);

        if (current instanceof Integer && cache instanceof Integer)
            return ((Integer) current).compareTo((Integer) cache) != 0;
        return current != cache;
    }

    public String getKey() { return this.key; }
    public String getLangKey() { return this.group.getLangKey() + "." + this.key; }
    public String getTooltipKey() { return this.getLangKey() + ".@Tooltip"; }
    public String getWarningKey() { return this.getLangKey() + ".@Warning"; }
}
