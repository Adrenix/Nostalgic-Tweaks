package mod.adrenix.nostalgic.client.config.reflect;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.ToastNotification;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.common.config.tweak.ITweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.network.packet.PacketC2SChangeTweak;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This cache is used exclusively by the client. The server should never interface with this class.
 * Therefore, using client code here is safe.
 */

public class TweakClientCache<T>
{
    /**
     * This cache keeps a record of every tweak used by both client and server. If the server wants to interface with
     * a server tweak it needs to reference the server cache.
     *
     * @see TweakServerCache
     */

    private static final HashMap<String, TweakClientCache<?>> cache = new HashMap<>();

    private static String generateKey(GroupType group, String key) { return TweakCommonCache.generateKey(group, key); }
    static
    {
        Arrays.stream(GroupType.values()).forEach((group) ->
            ClientReflect.getGroup(group).forEach((key, value) -> {
                if (CommonReflect.getAnnotation(group, key, TweakSide.Ignore.class) == null)
                    TweakClientCache.cache.put(generateKey(group, key), new TweakClientCache<>(group, key, value));
            })
        );
    }

    public static HashMap<String, TweakClientCache<?>> all() { return cache; }

    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakClientCache<T> get(GroupType group, String key)
    {
        return (TweakClientCache<T>) cache.get(generateKey(group, key));
    }

    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakClientCache<T> get(ITweak tweak)
    {
        if (tweak.getClientCache() == null)
            tweak.setClientCache(get(tweak.getGroup(), tweak.getKey()));
        return (TweakClientCache<T>) tweak.getClientCache();
    }

    /**
     * This field is used to determine a tweak's fuzzy weight when the client is searching for tweaks.
     * Range goes from 0-100 with 100 being an exact match.
     */
    private int weight = 0;

    /**
     * Set the weight that results from a fuzzy search using the Levenshtein distance algorithm.
     * @param weight Levenshtein distance result.
     */
    public void setWeight(int weight) { this.weight = weight; }

    /**
     * Get the cached Levenshtein distance result.
     * @return Levenshtein distance.
     */
    public int getWeight() { return this.weight; }

    /**
     * This static helper assists the fuzzy sorting algorithm.
     * @param firstWeight The first weight to compare.
     * @param secondWeight The second weight to compare.
     * @return A non-zero result so the comparison is guaranteed to appear in a tree map.
     */
    public static int compareWeights(int firstWeight, int secondWeight)
    {
        int compare = Integer.compare(firstWeight, secondWeight);
        return compare != 0 ? compare : compare + 1;
    }

    /**
     * Finds the number of tweaks that have failed to load. This is not always accurate since some
     * tweaks will need to load into the world.
     *
     * Any tweak that didn't run expected code should be assumed as a mod conflict or an incorrectly
     * mapped mixin.
     *
     * @return The amount of tweaks that have not executed any code.
     */
    public static int getConflicts()
    {
        AtomicInteger found = new AtomicInteger();
        TweakClientCache.all().forEach((key, tweak) -> {
            if (tweak.getStatus() == StatusType.FAIL)
                found.getAndIncrement();
        });

        return found.get();
    }

    /**
     * Client tweaks do interface with the server tweak cache. This is needed so the client can stay in sync while
     * connected to a verified N.T supported server.
     *
     * @see TweakServerCache
     */

    public TweakServerCache<T> getServerTweak() { return TweakServerCache.get(this.group, this.key); }
    public T getServerCache() { return this.getServerTweak().getServerCache(); }

    /**
     * If the network has not verified a level with Nostalgic Tweaks installed then all tweaks should be modified
     * client side.
     * @return Whether the tweak should be handled client side or server side.
     */
    private boolean isClientHandled()
    {
        if (!NostalgicTweaks.isNetworkVerified() || NetClientUtil.isSingleplayer() || Minecraft.getInstance().level == null)
            return true;
        return !this.isDynamic() && this.isClientSide();
    }

    /**
     * All tweak caches have a group and a unique key within that group. These keys match what is saved onto disk.
     * It is important that the keys stay in sync with the client config. This is why keys are established within
     * class loaded code blocks underneath each tweak entry.
     *
     * To get a tweak's key in the cache map, use {@link TweakClientCache#getId()}
     *
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     */

    private final String key;
    private final String id;
    private final GroupType group;
    private StatusType status;

    /**
     * This field will not be in sync with the config saved on disk. That is done through reflection.
     * The state of this field is toggled via the configuration menu.
     *
     * @see mod.adrenix.nostalgic.client.config.reflect.ClientReflect
     */
    private T value;

    /**
     * The position of a tweak within the configuration menu can be order by top or bottom.
     * It may be ideal to override the alphabetical ordering of tweaks to keep related tweaks together, regardless of name.
     */

    private int order;
    private TweakClient.Gui.Position position;

    /**
     * Client tweaks are created once and saved in the cache.
     * Use the cache to retrieve tweaks by their group and key identification.
     *
     * @param group Group associated with this tweak (e.g. CandyTweak).
     * @param key The unique key of a tweak. Must match what is saved on disk.
     * @param value The value of a tweak. Can be an Enum, boolean, String, int, etc.
     */
    private TweakClientCache(GroupType group, String key, T value)
    {
        this.group = group;
        this.key = key;
        this.value = value;
        this.status = StatusType.FAIL;
        this.id = generateKey(group, key);

        TweakSide.EntryStatus status = CommonReflect.getAnnotation(this, TweakSide.EntryStatus.class);
        if (status != null)
            this.status = status.status();

        TweakClient.Gui.Placement placement = CommonReflect.getAnnotation(this, TweakClient.Gui.Placement.class);
        if (placement != null)
        {
            this.position = placement.pos();
            this.order = placement.order();
        }
    }

    /**
     * Get a tweak's ID which is its key in the {@link TweakClientCache#cache} map.
     * @return The key used to identify the tweak in the client {@link TweakClientCache#cache}.
     */
    public String getId() { return this.id; }

    /**
     * Gets the default value of a tweak via reflection.
     * @see mod.adrenix.nostalgic.client.config.reflect.ClientReflect
     * @see mod.adrenix.nostalgic.common.config.DefaultConfig
     * @return The default value of a tweak.
     */
    public T getDefault() { return ClientReflect.getDefault(this.group, this.key); }

    /**
     * The return value of this is dependent on whether the tweak is controlled by the server.
     * @return If the tweak is handled by the client, then it returns the client value; otherwise, the server value.
     */
    public T getCurrent() { return this.isClientHandled() ? this.value : this.getServerTweak().getValue(); }

    /**
     * Get the value of this tweak that is saved on disk.
     * @return The tweak value on disk.
     */
    public T getSavedValue() { return ClientReflect.getCurrent(this.group, this.key); }

    /**
     * If the tweak is server controlled, then the client cache is updated. Otherwise, the server cache is updated and
     * won't be saved client side until the server has processed the updated value.
     *
     * If the session is LAN, then an override is necessary so the changes can be saved to the client's config.
     *
     * @param value The new tweak value.
     * @param override Whether to ignore if the tweak is client cached or server cached.
     */
    public void setCurrent(T value, boolean override)
    {
        if (this.isClientHandled() || override)
        {
            this.value = value;

            TweakServerCache<T> cache = this.getServerTweak();
            if (override && cache != null)
                cache.setValue(value);
        }
        else
            this.getServerTweak().setValue(value);
    }

    /**
     * Overload method for setting the current value of a tweak. The default behavior is to examine if the client should
     * handle the tweak. If the session is over LAN, it is necessary to override this behavior when a server tweak changes.
     *
     * @param value The new tweak value.
     */
    public void setCurrent(T value)
    {
        this.setCurrent(value, false);
    }

    /**
     * The group is assigned in the client configuration class.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return The group this tweak is in.
     */
    public GroupType getGroup() { return this.group; }

    /**
     * The status of a tweak is updated when its code is executed.
     * @see mod.adrenix.nostalgic.common.config.reflect.StatusType
     * @return Whether a tweak has failed to load, has not attempted to load, or is loaded.
     */
    public StatusType getStatus()
    {
        return this.isClientHandled() ? this.status : this.getServerTweak().getStatus();
    }

    /**
     * Can be set anywhere and updated at anytime.
     * @see mod.adrenix.nostalgic.common.config.reflect.StatusType
     * @param status The current status of a tweak.
     */
    public void setStatus(StatusType status)
    {
        if (this.isClientHandled())
            this.status = status;
        else
            this.getServerTweak().setStatus(status);
    }

    /**
     * Position is set via annotation in the client configuration class.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return Whether the tweak is ordered at the top or bottom of a category.
     */
    @Nullable public TweakClient.Gui.Position getPosition() { return this.position; }

    /**
     * This ordering is set in the client configuration class. Ordering is done least to greatest.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return Where the tweak should sit relative to neighboring tweaks.
     */
    public int getOrder() { return this.order; }

    /**
     * Restores a tweak back to its default value.
     */
    public void reset()
    {
        if (this.isClientHandled())
            this.value = getDefault();
        else
            this.getServerTweak().setValue(getDefault());
    }

    /**
     * Restores a tweak back to its original cached value.
     * If the tweak is client handled, then that is done via config reflection.
     * If the tweak is server handled, then the server tweak cache is referenced.
     */
    public void undo()
    {
        T value = ClientReflect.getCurrent(this.group, this.key);

        if (this.isClientHandled())
            this.value = value;
        else
            this.getServerTweak().setValue(value);
    }

    /**
     * Saves a tweak's new value to config cache, or sends an update packet to the server if a tweak is not handled by
     * the client. Once the settings screen is closed, all updated tweaks will be saved to disk.
     */
    public void save()
    {
        if (!this.isSavable())
            return;

        if (CommonReflect.getAnnotation(this, TweakClient.Run.ReloadChunks.class) != null)
            ModClientUtil.Run.reloadChunks = true;

        if (CommonReflect.getAnnotation(this, TweakClient.Run.ReloadResources.class) != null)
            ModClientUtil.Run.reloadResources = true;

        boolean isClient = this.isClientSide();
        boolean isDynamic = this.isDynamic() && NetClientUtil.isPlayerOp();
        boolean isServerTweak = isDynamic || !isClient;
        boolean isMultiplayer = NostalgicTweaks.isNetworkVerified() && NetClientUtil.isMultiplayer();

        if (NetClientUtil.isSingleplayer() && isServerTweak)
        {
            this.getServerTweak().setValue(this.value);
            this.getServerTweak().setServerCache(this.value);
        }

        if (isServerTweak && isMultiplayer)
        {
            PacketUtil.sendToServer(new PacketC2SChangeTweak(this.getServerTweak()));
            ToastNotification.addTweakChange();
        }

        T value = isDynamic && isMultiplayer ? this.getServerTweak().getValue() : this.value;

        if (NetClientUtil.isLocalHost() || this.isClientHandled() || isDynamic)
            ClientReflect.setConfig(this.group, this.key, value);
    }

    /**
     * Checks the annotations defined in the client's configuration class if a tweak is client sided.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return If the client side annotation is attached to a tweak.
     */
    public boolean isClientSide()
    {
        return CommonReflect.getAnnotation(this, TweakSide.Server.class) == null;
    }

    /**
     * Checks the annotations defined in the client's configuration class if a tweak is dynamically sided.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return If the dynamic annotation is attached to a tweak.
     */
    public boolean isDynamic()
    {
        return CommonReflect.getAnnotation(this, TweakSide.Dynamic.class) != null;
    }

    /**
     * Checks if a tweak doesn't match the value set in the default configuration class.
     * @see mod.adrenix.nostalgic.common.config.DefaultConfig
     * @return Whether the Reset button should be active.
     */
    public boolean isResettable()
    {
        if (!this.isClientHandled() && !NetClientUtil.isPlayerOp())
            return false;

        T current = this.getCurrent();
        T def = this.getDefault();

        // This check is required since comparison on the generics appears to check the integers as if they were bytes.
        if (current instanceof Integer && def instanceof Integer)
            return ((Integer) current).compareTo((Integer) def) != 0;
        return !current.equals(def);
    }

    /**
     * Checks if a tweak's current value doesn't match the cache.
     * @return If the config can be saved to disk or an update packet should be sent to the server.
     */
    public boolean isSavable()
    {
        T current = this.getCurrent();
        T cache = this.isClientHandled() ? ClientReflect.getCurrent(this.group, this.key) : this.getServerCache();

        if (current instanceof Integer && cache instanceof Integer)
            return ((Integer) current).compareTo((Integer) cache) != 0;
        return !current.equals(cache);
    }

    /**
     * The key is used to identify a tweak within a group.
     * Additionally, the key is also used in the language definition files.
     */

    public String getKey() { return this.key; }
    public String getLangKey() { return this.group.getLangKey() + "." + this.key; }
    public String getTooltipKey() { return this.getLangKey() + ".@Tooltip"; }
    public String getWarningKey() { return this.getLangKey() + ".@Warning"; }
    public String getTranslation() { return Component.translatable(this.getLangKey()).getString(); }
    public String getTooltipTranslation() { return Component.translatable(this.getTooltipKey()).getString(); }
}
