package mod.adrenix.nostalgic.client.config.reflect;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.TweakReload;
import mod.adrenix.nostalgic.client.config.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.tweak.Tweak;
import mod.adrenix.nostalgic.network.packet.PacketC2SChangeTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.CheckReturnValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This cache is used exclusively by the client. The server should not interface with this class, the server may
 * however access the client hashmap cache.
 *
 * Therefore, using vanilla client code here is safe.
 */

public class TweakClientCache<T> extends TweakCommonCache
{
    /**
     * This cache keeps a record of every tweak used by both client and server. If the server wants to interface with
     * a server tweak, then it needs to reference the server cache.
     *
     * @see TweakServerCache
     */

    private static final HashMap<String, TweakClientCache<?>> CACHE = new HashMap<>();

    static
    {
        Arrays.stream(TweakGroup.values()).forEach
        (
            (group) ->
                ClientReflect.getGroup(group).forEach((key, value) ->
                    TweakClientCache.CACHE.put(generateKey(group, key), new TweakClientCache<>(group, key, value)))
        );
    }

    /**
     * Get a hash map of all tweaks.
     * @return A map of tweak keys to their client cached value.
     */
    public static HashMap<String, TweakClientCache<?>> all() { return CACHE; }

    /**
     * Get a tweak. This should <b>only</b> be used if a tweak enumeration is not available.
     * For best performance, use {@link TweakClientCache#get(Tweak)} since it retrieves cached hashmap pointers.
     * @param group The group a tweak is associated with.
     * @param key The key used to identify the tweak.
     * @return The current tweak value kept in the cache.
     * @param <T> The type associated with the tweak.
     * @throws AssertionError Will throw if the tweak is not available in the cache.
     */
    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakClientCache<T> get(TweakGroup group, String key) throws AssertionError
    {
        TweakClientCache<T> instance = (TweakClientCache<T>) CACHE.get(generateKey(group, key));

        if (instance == null)
            throw new AssertionError(String.format("Tweak [group=%s, key=%s] was not found in client-cache", group, key));

        return instance;
    }

    /**
     * An overload method for {@link TweakClientCache#get(TweakGroup, String)}. This should be the primary way of
     * retrieving cached tweak values. When each tweak loads, a pointer is cached in the tweak's enumeration instance.
     * This method will use that pointer instead of looping through the hashmap to get a tweak's value.
     *
     * This will <b>throw</b> an {@link AssertionError} if the tweak is not in the cache.
     * @param tweak The tweak to fetch from cache.
     * @return The current value kept in the cache.
     * @param <T> The type associated with the tweak.
     */
    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakClientCache<T> get(Tweak tweak)
    {
        if (tweak.getClientCache() == null)
            tweak.setClientCache(get(tweak.getGroup(), tweak.getKey()));

        return (TweakClientCache<T>) tweak.getClientCache();
    }

    /* Server Cache Retrieval */

    /**
     * Client tweaks do interface with the server tweak cache. This is needed so the client can stay in sync while
     * connected to a verified N.T supported server.
     *
     * @see TweakServerCache
     */
    public TweakServerCache<T> getServerTweak()
    {
        if (this.tweak != null)
            return TweakServerCache.get(this.tweak);

        return TweakServerCache.get(this.group, this.key);
    }

    /**
     * A shortcut for {@link TweakClientCache#getServerTweak()} that gets the current server cache.
     * @return The value saved in the client's server cache.
     */
    public T getServerCache() { return this.getServerTweak().getServerCache(); }

    /* Fuzzy Searching */

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

    /* Static Methods */

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

        TweakClientCache.all().forEach((key, tweak) ->
        {
            if (tweak.getStatus() == TweakStatus.FAIL)
                found.getAndIncrement();
        });

        return found.get();
    }

    /* Fields */

    /*
       These fields are caches for commonly accessed annotation metadata. Any annotations that is queried often or every
       tick needs a field cache. Since this metadata never changes during runtime, it is safe to cache the known values
       rather than constantly using reflection to retrieve metadata.
     */

    private final boolean isAnnotatedNew;
    private final boolean isAnnotatedClient;
    private final boolean isAnnotatedServer;
    private final boolean isAnnotatedDynamic;
    private final boolean isAnnotatedNotAutomated;
    private final boolean isAnnotatedReloadChunks;
    private final boolean isAnnotatedReloadResources;

    private final TweakGui.Placement placement;
    private final TweakGui.Category category;
    private final TweakGui.Subcategory subcategory;
    private final TweakGui.Embed embed;

    /**
     * This field will track the tweak enumeration associated with this cache. Once defined, this will speed up value
     * retrieval for server side tweaks significantly.
     *
     * There is no need for this field to be common since the server will only be interfacing with its own cache. The
     * server will never reach over to the client cache map.
     */
    private Tweak tweak;

    /**
     * This field will not be in sync with the config saved on disk. That is done through reflection.
     * The state of this field is toggled via the configuration menu.
     *
     * @see mod.adrenix.nostalgic.client.config.reflect.ClientReflect
     */
    private T value;

    /*
       The position of a tweak within the configuration menu can be order by top or bottom.
       It may be ideal to override the alphabetical ordering of tweaks to keep related tweaks together, regardless of name.
     */

    private int order;
    private TweakGui.Position position;

    /* Constructor */

    /**
     * Client tweaks are created once and saved in the cache.
     * Use the cache to retrieve tweaks by their group and key identification.
     *
     * @param group Group associated with this tweak (e.g. CandyTweak).
     * @param key The unique key of a tweak. Must match what is saved on disk.
     * @param value The value of a tweak. Can be an Enum, boolean, String, int, etc.
     */
    private TweakClientCache(TweakGroup group, String key, T value)
    {
        super(group, key);

        this.tweak = null;
        this.value = value;

        this.isAnnotatedNew = this.isMetadataPresent(TweakGui.New.class);
        this.isAnnotatedClient = this.isMetadataMissing(TweakData.Server.class);
        this.isAnnotatedServer = this.isMetadataPresent(TweakData.Server.class);
        this.isAnnotatedDynamic = this.isMetadataPresent(TweakData.Dynamic.class);
        this.isAnnotatedNotAutomated = this.isMetadataPresent(TweakGui.NotAutomated.class);
        this.isAnnotatedReloadChunks = this.isMetadataPresent(TweakReload.Chunks.class);
        this.isAnnotatedReloadResources = this.isMetadataPresent(TweakReload.Resources.class);

        this.placement = this.getMetadata(TweakGui.Placement.class);
        this.category = this.getMetadata(TweakGui.Category.class);
        this.subcategory = this.getMetadata(TweakGui.Subcategory.class);
        this.embed = this.getMetadata(TweakGui.Embed.class);

        if (this.placement != null)
        {
            this.position = this.placement.pos();
            this.order = this.placement.order();
        }
    }

    /* Methods */

    /**
     * If the network has not verified a level with Nostalgic Tweaks installed then all tweaks should be modified
     * client side.
     * @return Whether the tweak should be handled client side or server side.
     */
    private boolean isClientHandled()
    {
        if (!NostalgicTweaks.isNetworkVerified() || NetUtil.isSingleplayer() || Minecraft.getInstance().level == null)
            return true;

        return !this.isDynamic() && this.isClient();
    }

    /**
     * Define the tweak enumeration associated with this cache.
     * @param tweak A tweak instance.
     */
    public void setTweak(Tweak tweak) { this.tweak = tweak; }

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
    public T getValue() { return this.isClientHandled() ? this.value : this.getServerTweak().getValue(); }

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
    public void setValue(T value, boolean override)
    {
        if (this.isClientHandled() || override)
        {
            this.value = value;

            TweakServerCache<T> serverTweak = this.getServerTweak();

            if (override && serverTweak != null)
                serverTweak.setValue(value);
        }
        else
            this.getServerTweak().setValue(value);
    }

    /**
     * Overload method for setting the current value of a tweak. Use this method when the client needs to change the
     * value of a tweak.
     *
     * @param value The new tweak value.
     */
    public void setValue(T value) { this.setValue(value, false); }

    /**
     * The status of a tweak is updated when its code is executed.
     * @see TweakStatus
     * @return Whether a tweak has failed to load, has not attempted to load, or is loaded.
     */
    public TweakStatus getStatus() { return this.isClientHandled() ? this.status : this.getServerTweak().getStatus(); }

    /**
     * Can be set anywhere and updated at anytime.
     * @see TweakStatus
     * @param status The current status of a tweak.
     */
    public void setStatus(TweakStatus status)
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
    @CheckReturnValue
    public TweakGui.Position getPosition() { return this.position; }

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
            this.getServerTweak().setValue(this.getServerCache());
    }

    /**
     * Saves a tweak's new value to config cache, or sends an update packet to the server if a tweak is not handled by
     * the client. Once the settings screen is closed, all updated tweaks will be saved to disk. Tweaks with list data
     * do not need saved here since their saving instructions is handled by the abstract list screen.
     */
    public void save()
    {
        if (this.getList() != null || !this.isSavable())
            return;

        if (this.isAnnotatedReloadChunks)
            RunUtil.reloadChunks = true;

        if (this.isAnnotatedReloadResources)
            RunUtil.reloadResources = true;

        boolean isClient = this.isClient();
        boolean isDynamic = this.isDynamic() && NetUtil.isPlayerOp();
        boolean isServerTweak = isDynamic || !isClient;
        boolean isMultiplayer = NostalgicTweaks.isNetworkVerified() && NetUtil.isMultiplayer();

        if (NetUtil.isSingleplayer() && isServerTweak)
        {
            this.getServerTweak().setValue(this.value);
            this.getServerTweak().setServerCache(this.value);
        }

        if (isServerTweak && isMultiplayer)
        {
            PacketUtil.sendToServer(new PacketC2SChangeTweak(this.getServerTweak()));
            ToastNotification.sentChanges();
        }

        T value = isDynamic && isMultiplayer ? this.getServerTweak().getValue() : this.value;

        if (NetUtil.isLocalHost() || this.isClientHandled() || isDynamic)
            ClientReflect.setConfig(this.group, this.key, value);
    }

    /**
     * Checks the annotations defined in the client's configuration class if a tweak is new for this update.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return If the client side annotation is attached to a tweak.
     */
    public boolean isNew() { return this.isAnnotatedNew; }

    /**
     * Checks the annotations defined in the client's configuration class if a tweak is client sided.
     * This <b>does</b> check if a tweak is dynamic. Restrict using {@link TweakClientCache#isDynamic()}.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return If the client side annotation is attached to a tweak.
     */
    public boolean isClient() { return this.isAnnotatedClient; }

    /**
     * Checks the annotations defined in the client's configuration class if a tweak is server sided.
     * This does <b>not</b> check if a tweak is dynamic, use {@link TweakClientCache#isDynamic()}.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return If the server annotation is attached to a tweak.
     */
    public boolean isServer() { return this.isAnnotatedServer; }

    /**
     * Checks the annotations defined in the client's configuration class if a tweak is dynamically sided.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     * @return If the dynamic annotation is attached to a tweak.
     */
    public boolean isDynamic() { return this.isAnnotatedDynamic; }

    /**
     * Cache utility that checks if a tweak can be changed by the client. Useful to prevent issues of changing tweaks
     * that shouldn't be changed because the player lacks permission to a connected N.T server.
     * @return Whether the tweak is locked out from the client.
     */
    public boolean isLocked()
    {
        if (this.isClient() && !this.isDynamic())
            return false;

        return NostalgicTweaks.isNetworkVerified() && !NetUtil.isPlayerOp();
    }

    /**
     * Checks if a tweak doesn't match the value set in the default configuration class.
     * @see mod.adrenix.nostalgic.common.config.DefaultConfig
     * @return Whether the Reset button should be active.
     */
    public boolean isResettable()
    {
        if (!this.isClientHandled() && !NetUtil.isPlayerOp())
            return false;

        T current = this.getValue();
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
        T current = this.getValue();
        T cache = this.isClientHandled() ? ClientReflect.getCurrent(this.group, this.key) : this.getServerCache();

        if (current instanceof Integer && cache instanceof Integer)
            return ((Integer) current).compareTo((Integer) cache) != 0;

        return !current.equals(cache);
    }

    /**
     * @return If a tweak is not automated, then its configuration row must be manually created.
     */
    public boolean isNotAutomated() { return this.isAnnotatedNotAutomated; }

    /**
     * @return A tweak's container placement data if it is present.
     */
    @CheckReturnValue public TweakGui.Placement getPlacement() { return this.placement; }

    /**
     * @return A tweak's category if it is present.
     */
    @CheckReturnValue public TweakGui.Category getCategory() { return this.category; }

    /**
     * @return A tweak's subcategory if it is present.
     */
    @CheckReturnValue public TweakGui.Subcategory getSubcategory() { return this.subcategory; }

    /**
     * @return A tweak's embed if it is present.
     */
    @CheckReturnValue public TweakGui.Embed getEmbed() { return this.embed; }

    /**
     * Get the translation of this tweak's container. If this tweak does not reside in a container then an empty string
     * is returned.
     *
     * @return The translation of this tweak's container, or an empty string if there is not a container.
     */
    public String getContainerTranslation()
    {
        if (this.category != null)
            return Component.translatable(this.category.container().getLangKey()).getString();
        else if (this.subcategory != null)
            return Component.translatable(this.subcategory.container().getLangKey()).getString();
        else if (this.embed != null)
            return Component.translatable(this.embed.container().getLangKey()).getString();

        return "";
    }
}
