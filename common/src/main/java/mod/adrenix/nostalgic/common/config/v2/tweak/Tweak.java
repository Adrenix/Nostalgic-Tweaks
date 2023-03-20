package mod.adrenix.nostalgic.common.config.v2.tweak;

import com.google.common.base.Suppliers;
import dev.architectury.platform.Platform;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.v2.cache.ConfigCache;
import mod.adrenix.nostalgic.common.config.v2.cache.ConfigReflect;
import mod.adrenix.nostalgic.common.config.v2.v2ModConfig;
import mod.adrenix.nostalgic.common.config.v2.client.ClientConfig;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;
import mod.adrenix.nostalgic.common.config.v2.gui.TweakPlacement;
import mod.adrenix.nostalgic.common.config.v2.gui.TweakSlider;
import mod.adrenix.nostalgic.common.config.v2.server.ServerConfig;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;

import javax.annotation.CheckForNull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

/**
 * All tweak instances are defined by this class. This acts as a bridge between the client and server so both logical
 * sides will make use of this class.
 *
 * @param <T> The value type of this tweak (e.g., boolean, int, long, Class, and etc.)
 */

public class Tweak<T>
{
    /* Utility */

    /**
     * This map contains a list of all tweaks that have called the {@link Tweak#setAndGet(String)} method. The purpose
     * of hash map is for obtaining tweaks with only cache map keys. This should only be used in situations where you
     * are not working with a specific tweak. For example, the packet system must use this cache map to determine what
     * tweak was just sent over the wire. Both the client and server have access to this map, but the server will only
     * have access to server only tweaks.
     */
    private static final LinkedHashMap<String, Tweak<?>> CACHE = new LinkedHashMap<>();

    /**
     * Get a tweak from the built cache using the provided config key.
     * @param cacheId A tweak cache map id.
     * @return A tweak instance without casting data.
     */
    public static Tweak<?> get(String cacheId)
    {
        return Tweak.CACHE.get(cacheId);
    }

    /* Reflection */

    /**
     * This holds the config field pointer for reflection.
     * Caching this value prevents the unnecessary recalculations to find a field in a config class.
     */
    private Field field = null;

    /**
     * Set a config field pointer for reflection.
     * @param field The field to cache.
     */
    public void setConfigField(Field field)
    {
        this.field = field;
    }

    /**
     * A field pointer that has been previously calculated.
     * @return An optional field pointer if it has been already found via reflection.
     */
    public Optional<Field> configField()
    {
        return Optional.ofNullable(field);
    }

    /* Tweak Fields */

    /**
     * The default value applied to a tweak if it has not yet been configured.
     */
    private final T defaultValue;

    /**
     * Whether a tweak is client, server, or dynamic.
     * @see TweakSide
     */
    private final TweakSide side;

    /**
     * The container (category or group) the tweak resides in.
     * Every tweak must be assigned a container.
     */
    private final TweakContainer container;

    /**
     * The value to use when the mod is set into a "disabled" state or when the tweak itself is considered "disabled".
     * Such as a server side tweak being "disabled" when the client is connected to a server without Nostalgic Tweaks
     * installed.
     * <br><br>
     *
     * The {@link Tweak#defaultValue} defined during tweak building will be used when the tweak is in a "disabled"
     * state. To reduce overhead during tweak building, any <code>Boolean</code> tweak with a {@link Tweak#defaultValue}
     * of <code>true</code> is automatically set to <code>false</code> when the tweak is "disabled" unless a disabled
     * value was assigned during the building process. Any boolean tweak with a default value of <code>false</code> will
     * remain <code>false</code> when the tweak is "disabled".
     *
     * Use {@link Builder#whenDisabled(Object)} to change what value should be used when the tweak is "disabled".
     */
    @CheckForNull
    private T disabledValue;

    /**
     * If the tweak is some <code>Number</code> then a custom slider instance can be used.
     * @see TweakSlider
     */
    @CheckForNull
    private TweakSlider slider;

    /**
     * All tweaks start off in the "WAIT" state unless otherwise labeled as "LOADED" during tweak definition by using
     * the builder's {@link Builder#load()} method. When joining a world, all tweaks move from "WAIT" status to either
     * the "LOADED" or "FAIL" status. This is not always accurate since a tweak will not be "LOADED" until code invokes
     * the tweak through the {@link v2ModConfig#get(Tweak)} method.
     *
     * @see TweakStatus
     */
    private TweakStatus status;

    /**
     * All tweaks start with the "AUTO" placement enumeration value and will be ordered alphabetically in the
     * configuration menu's automatic row builder. If this is not preferred, then you can define whether a tweak should
     * be pushed to the "TOP" or "BOTTOM". The order in which tweaks appear will depend on the order in which tweaks
     * were placed in the cache which occurs when {@link Tweak#setAndGet(String)} is called. This method is invoked by
     * both sided configs, but only the {@link ClientConfig} will dictate the order in which tweaks are placed in the
     * cache.
     *
     * @see TweakPlacement
     */
    private TweakPlacement placement;

    /**
     * A flag that indicates that this tweak is new for the current update build.
     * Use the builder's {@link Builder#newForUpdate()} to set this to <code>true</code>.
     */
    private boolean newForUpdate;

    /**
     * A flag that indicates if a tooltip should appear when a mouse hovers over this tweak.
     * All tweaks have this set to <code>true</code>. Use {@link Builder#noTooltip()} to set this to <code>false</code>.
     */
    private boolean noTooltip;

    /**
     * A flag that indicates whether a tweak should go through the mod's network verification check when determining if
     * a tweak should use its configured value when playing on a multiplayer server. This check is only performed on
     * tweaks that run on the server. Use {@link Builder#ignoreNetworkCheck()} to set this to <code>true</code>.
     */
    private boolean noNetworkCheck;

    /**
     * A flag that indicates if a "Warning" tag should appear next to the tweak's name.
     * This can be set to <code>true</code> using {@link Builder#warningTag()}.
     * Lang keys follow the format: <code>gui.nostalgic_tweaks.autoconfig.{category}.{?group}.{tweak}.@Warning</code>.
     */
    private boolean hasWarningTag;

    /**
     * A flag that indicates if an "Optifine" tag should appear next to the tweak's name.
     * This can be set to <code>true</code> using {@link Builder#optifineTag()}.
     * Lang keys follow the format: <code>gui.nostalgic_tweaks.autoconfig.{category}.{?group}.{tweak}.@Optifine</code>.
     */
    private boolean hasOptifineTag;

    /**
     * A flag that indicates if a "Sodium" tag should appear next to the tweak's name.
     * This can be set to <code>true</code> using {@link Builder#sodiumTag()}.
     * Lang keys follow the format: <code>gui.nostalgic_tweaks.autoconfig.{category}.{?group}.{tweak}.@Sodium</code>.
     */
    private boolean hasSodiumTag;

    /**
     * A flag that indicates if a tweak should be considered a colored hex <code>String</code>. The automatic row builder
     * will display a color input box instead of a standard <code>String</code> input box.
     * This can be set to <code>true</code> using {@link Builder#colorTweak()}.
     */
    private boolean hasColorValue;

    /**
     * A flag that indicates if chunks should be reloaded if this tweak's value changes after saving.
     * This can be set to <code>true</code> using {@link Builder#reloadChunks()}.
     */
    private boolean doesChunkReload;

    /**
     * A flag that indicates if game resources should be reloaded if this tweak's value changes after saving.
     * This can be set to <code>true</code> using {@link Builder#reloadResources()}.
     */
    private boolean doesResourceReload;

    /**
     * A flag that indicates if this tweak should be ignored by the automatic row builder and the mod config pipeline.
     * When using {@link v2ModConfig#get(Tweak)}, the current {@link Tweak#value} will always be returned.
     * This can be set to <code>true</code> using {@link Builder#ignore()}.
     */
    private boolean ignore;

    /**
     * A supplier that returns a truthful boolean when a certain alert condition is met. When met, an "Alert" tag will
     * appear next to the tweak's name in the configuration menu row belonging to this tweak.
     * This can be defined using {@link Builder#alert(Supplier)}.
     * Lang keys follow the format: <code>gui.nostalgic_tweaks.autoconfig.{category}.{?group}.{tweak}.@Alert</code>
     */
    private Supplier<Boolean> alertCondition;

    /**
     * A supplier that indicates if a tweak should be considered "enabled". This is useful if an additional check should
     * be made in the mod config when it gets the value of a tweak. If the supplier returns a <code>false</code> boolean
     * value then the tweak will give back its {@link Tweak#disabledValue}.
     */
    private Supplier<Boolean> andIf;

    /**
     * A set of mod ids that will always force this tweak to return its {@link Tweak#disabledValue} if a mod is
     * installed matching an id within this set.
     */
    private Set<String> conflictModIds;

    /**
     * The JSON key associated with this tweak. This should be defined using {@link Tweak#setAndGet(String)} in either
     * the client config JSON structure class or server config JSON structure class.
     *
     * @see ClientConfig
     * @see ServerConfig
     */
    @CheckForNull
    private String configKey = null;

    /**
     * A value that is sent by a server with Nostalgic Tweaks installed. When the client establishes a connection with a
     * server using Nostalgic Tweaks, packets will update this field. When the tweak is built, the initial value stored
     * in this field is the default value.
     */
    private T sentValue;

    /**
     * A value that is used by the configuration menu so that when changes are saved this value is pushed to disk and
     * is copied to {@link Tweak#value}. When the tweak is built, the initial value stored in this field is the default
     * value.
     */
    private T cacheValue;

    /**
     * The current value that is saved on disk. When the tweak is built, the initial value stored in this field is the
     * default value.
     */
    private T value;

    /* Constructor */

    /**
     * Create a new tweak instance.
     * @param defaultValue The default starting value of this tweak.
     * @param side The environment type for the tweak (i.e., client or server).
     * @param container The tweak container this tweak is a part of.
     */
    private Tweak(T defaultValue, TweakSide side, TweakContainer container)
    {
        this.defaultValue = defaultValue;
        this.side = side;
        this.container = container;
    }

    /* Utility */

    /**
     * Logic that is only performed once to check for conflicting mods associated with this tweak.
     * @return Whether a mod conflict was found for this tweak.
     */
    private boolean getConflict()
    {
        if (this.conflictModIds.isEmpty())
            return false;

        for (String id : this.conflictModIds)
        {
            if (Platform.isModLoaded(id))
                return true;
        }

        return false;
    }

    /**
     * Since mods cannot be loaded/unloaded during runtime, this supplier can be memoized.
     * When truthful, indicates that this tweak should not be enabled.
     */
    private final Supplier<Boolean> conflictSupplier = Suppliers.memoize(this::getConflict);

    /**
     * @return Checks if this tweak is conflicting with another mod.
     * @see Tweak#conflictSupplier
     */
    public boolean isConflicted()
    {
        return conflictSupplier.get();
    }

    /* Methods */

    /**
     * Set the config JSON key associated with this tweak and get the tweak's default value as the return.
     * @param configKey A config JSON key.
     * @return The tweak's default value.
     * @throws AssertionError If a tweak with the given config key has already been built.
     * @see Tweak#configKey
     * @see Tweak#defaultValue
     */
    public T setAndGet(String configKey)
    {
        this.configKey = configKey;

        Tweak.CACHE.put(this.getCacheKey(), this);

        return this.defaultValue;
    }

    /**
     * Change the value that is to be saved on disk via reflection.
     *
     * This does not perform any saving operations. Changes to disk must be done with {@link ConfigCache#saveClient()}
     * or {@link ConfigCache#saveServer()}.
     *
     * @param value The new value that reflects what was saved on disk.
     * @throws AssertionError If value class type does not match disk value class type.
     * @see Tweak#value
     */
    public void setValue(Object value)
    {
        Optional<T> cast = ClassUtil.cast(value, this.value.getClass());

        if (cast.isPresent())
        {
            this.value = cast.get();

            if (NostalgicTweaks.isClient())
            {
                boolean isSingleplayer = NetUtil.isSingleplayer() || NetUtil.isLocalHost();
                boolean isMultiplayer = !NetUtil.isSingleplayer() && NetUtil.isConnected();
                boolean isMainMenu = !NetUtil.isConnected();

                boolean isSavable = this.side.equals(TweakSide.CLIENT) || isSingleplayer || isMainMenu;

                if (isMultiplayer && !NostalgicTweaks.isNetworkVerified())
                    isSavable = true;

                if (isSavable)
                    ConfigReflect.setClientField(this, value);
            }
            else
                ConfigReflect.setServerField(this, value);
        }
        else
        {
            String cacheId = this.getCacheKey();
            String diskType = this.value.getClass().toString();
            String sentType = value.getClass().toString();
            String message = "Cannot set value of [tweak={cacheId:%s, classType:%s}] with new classType (%s)";
            String error = String.format(message, cacheId, diskType, sentType);

            throw new AssertionError(error);
        }
    }

    /**
     * Get the value that is saved on disk.
     * This will return the default value if the value for this tweak has not yet been set.
     *
     * @return The value saved on disk.
     * @see Tweak#value
     * @see Tweak#defaultValue
     */
    public T getValue()
    {
        return this.value;
    }

    /**
     * Change the temporary cache value stored in this tweak.
     * This is used by the configuration menu.
     *
     * @param value A new cache value.
     * @see Tweak#cacheValue
     */
    public void setCacheValue(T value)
    {
        this.cacheValue = value;
    }

    /**
     * Get the temporary cache value stored in this tweak.
     * @return The current cached value.
     * @see Tweak#cacheValue
     */
    public T getCacheValue()
    {
        return this.cacheValue;
    }

    /**
     * Change the server value sent from a server with Nostalgic Tweaks installed.
     * This is used by network packets.
     *
     * @throws AssertionError If sent value class type does not match client value class type.
     * @param value The value that was sent by the server over the network.
     * @see Tweak#sentValue
     */
    public void setSentValue(Object value)
    {
        Optional<T> cast = ClassUtil.cast(value, this.value.getClass());

        if (cast.isPresent())
            this.sentValue = cast.get();
        else
            NostalgicTweaks.LOGGER.stacktrace("Network Error:", "Server sent value does not match client value");
    }

    /**
     * Get the value sent from a server with Nostalgic Tweaks installed.
     * @return A value sent from a server with the mod installed.
     * @see Tweak#sentValue
     * @see Tweak#defaultValue
     */
    public T getSentValue()
    {
        return this.sentValue;
    }

    /**
     * Get the {@link Tweak#CACHE} map key associated with this tweak.
     * @throws AssertionError If this tweak's JSON config key has not yet been set.
     * @return A tweak cache map key.
     */
    public String getCacheKey()
    {
        if (this.configKey == null)
            throw new AssertionError("Config key was not properly set before retrieving its value");

        return this.getContainer().getCacheKey() + "." + this.configKey;
    }

    /**
     * Get the config JSON key for this tweak. This should only be used for reflection purposes. If you want to get a
     * tweak from the cache map then use {@link Tweak#getCacheKey()}.
     *
     * @throws AssertionError If this tweak's JSON config key has not yet been set.
     * @return The config JSON key of this tweak.
     */
    public String getConfigKey()
    {
        if (this.configKey == null)
            throw new AssertionError("Config key was not properly set before retrieving its value");

        return this.configKey;
    }

    /**
     * Get the tweak container associated with this tweak.
     * Each tweak must be assigned to a container whether that container be a category or group.
     *
     * @return A tweak container instance (category or group).
     * @see Tweak#container
     */
    public TweakContainer getContainer()
    {
        return this.container;
    }

    /**
     * Get the side this tweak belongs to; either "CLIENT", "SERVER", or "DYNAMIC".
     * @return A tweak side enumeration value.
     * @see Tweak#side
     */
    public TweakSide getSide()
    {
        return this.side;
    }

    /**
     * Get the default value associated with this tweak. This cannot be changed after building the tweak.
     * @return The default value that was assigned during the tweak's building process.
     * @see Tweak#defaultValue
     */
    public T getDefault()
    {
        return this.defaultValue;
    }

    /**
     * Get the "disable" value associated with this tweak. When the mod is globally disabled then each tweak's "disabled"
     * value is used. This is also the value used when the tweak itself is considered "disabled" such as a server side
     * tweak running in a game state where the client is connected to a server without Nostalgic Tweaks installed. If
     * this tweak has its {@link Tweak#ignore} flag set to <code>true</code> then the current value saved on disk will
     * be returned.
     *
     * @return A "disable" value for when the tweak is considered "disabled".
     * @see Tweak#ignore
     * @see Tweak#value
     * @see Tweak#disabledValue
     */
    @SuppressWarnings("unchecked") // Disable value and current values are of the same type so instanceof check on value is safe
    public T getDisabledValue()
    {
        if (this.ignore)
            return this.getValue();

        if (this.disabledValue != null)
            return this.disabledValue;

        if (this.defaultValue instanceof Boolean bool && bool)
        {
            this.disabledValue = (T) (Object) false;
            return this.disabledValue;
        }

        return this.getDefault();
    }

    /**
     * @return Whether this tweak is "New" for this update build.
     * @see Tweak#newForUpdate
     */
    public boolean isNew()
    {
        return this.newForUpdate;
    }

    /**
     * When a tweak is "ignored" it is not used in automatic row generation in the configuration menu and the value
     * stored on disk will always be returned when retrieving the value through the mod config pipeline.
     *
     * @return Whether this tweak is "ignored".
     * @see Tweak#ignore
     */
    public boolean isIgnored()
    {
        return this.ignore;
    }

    /**
     * @return Will be <code>true</code> if there is no tooltip associated with this tweak.
     * @see Tweak#noTooltip
     */
    public boolean isTooltipEmpty()
    {
        return this.noTooltip;
    }

    /**
     * @return Will be <code>true</code> if the <code>String</code> value associated with this tweak is a hex value.
     * @see Tweak#hasColorValue
     */
    public boolean isColoredValue()
    {
        return this.hasColorValue;
    }

    /**
     * Some tweaks, like the old square border tweak, have code queried before the network can be verified. Therefore,
     * it is important that the tweak is skipped in the mod config's network check pipeline.
     *
     * @return If <code>true</code>, this tweak will not go through the mod config's network verification check pipeline.
     * @see Tweak#noNetworkCheck
     */
    public boolean isNetworkCheckIgnored()
    {
        return this.noNetworkCheck;
    }

    /**
     * @return Whether all chunks need reloaded after this tweak is saved with a new value.
     * @see Tweak#doesChunkReload
     */
    public boolean isChunkReloadRequired()
    {
        return this.doesChunkReload;
    }

    /**
     * @return Whether the game's resources need reloaded after this tweak is saved with a new value.
     * @see Tweak#doesResourceReload
     */
    public boolean isResourceReloadRequired()
    {
        return this.doesResourceReload;
    }

    /**
     * @return When <code>true</code>, an "Alert" tag appears next to this tweak's name in the configuration menu row.
     * @see Tweak#alertCondition
     */
    public boolean isAlertTag()
    {
        return this.alertCondition.get();
    }

    /**
     * @return When <code>true</code>, the tweak will be considered "enabled". Otherwise, the tweak will be considered
     * "disabled" and will return its {@link Tweak#disabledValue}.
     *
     * @see Tweak#andIf
     * @see Tweak#disabledValue
     */
    public boolean isExtraConditionMet()
    {
        return this.andIf.get();
    }

    /**
     * @return When <code>true</code>, a "Warning" tag appears next to this tweak's name in the configuration menu row.
     * @see Tweak#hasWarningTag
     */
    public boolean isWarningTag()
    {
        return this.hasWarningTag;
    }

    /**
     * @return When <code>true</code>, an "Optifine" tag appears next to this tweak's name in the configuration menu row.
     * @see Tweak#hasOptifineTag
     */
    public boolean isOptifineTag()
    {
        return this.hasOptifineTag;
    }

    /**
     * @return When <code>true</code>, a "Sodium" tag appears next to this tweak's name in the configuration menu row.
     * @see Tweak#hasSodiumTag
     */
    public boolean isSodiumTag()
    {
        return this.hasSodiumTag;
    }

    /**
     * Change the loading status of this tweak.
     * @param status The new tweak status enumeration value.
     * @see Tweak#status
     */
    public void setStatus(TweakStatus status)
    {
        this.status = status;
    }

    /**
     * @return The current tweak loading status.
     * @see Tweak#status
     */
    public TweakStatus getStatus()
    {
        return this.status;
    }

    /**
     * @return The current tweak placement.
     * @see Tweak#placement
     */
    public TweakPlacement getPlacement()
    {
        return this.placement;
    }

    /**
     * @return This tweak's slider, if it is defined.
     * @see Tweak#slider
     */
    public Optional<TweakSlider> getSlider()
    {
        return Optional.ofNullable(this.slider);
    }

    /* Builder */

    /**
     * Build a new tweak instance.
     * @param defaultValue The tweak's default value.
     * @param side The tweak's side.
     * @param container The tweak's container.
     * @param <V> The tweak's class type.
     * @return A new tweak builder instance.
     */
    public static <V> Builder<V> builder(V defaultValue, TweakSide side, TweakContainer container)
    {
        return new Builder<>(defaultValue, side, container);
    }

    public static class Builder <V>
    {
        /* Builder -> Tweak : Fields */

        private final V defaultValue;
        private final TweakSide side;
        private final TweakContainer container;

        @CheckForNull
        private V disabledValue = null;

        @CheckForNull
        private TweakSlider slider = null;

        private TweakStatus status = TweakStatus.WAIT;
        private TweakPlacement placement = TweakPlacement.AUTO;

        private boolean newForUpdate = false;
        private boolean noTooltip = false;
        private boolean noNetworkCheck = false;
        private boolean hasWarningTag = false;
        private boolean hasOptifineTag = false;
        private boolean hasSodiumTag = false;
        private boolean hasColorValue = false;
        private boolean doesChunkReload = false;
        private boolean doesResourceReload = false;
        private boolean ignore = false;

        private final Set<String> conflictModIds = new HashSet<>();
        private Supplier<Boolean> alertCondition = Suppliers.memoize(() -> false);
        private Supplier<Boolean> andIf = Suppliers.memoize(() -> true);

        /* Constructor & Methods */

        private Builder(V defaultValue, TweakSide side, TweakContainer container)
        {
            this.defaultValue = defaultValue;
            this.side = side;
            this.container = container;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#disabledValue
         */
        public Builder<V> whenDisabled(V value)
        {
            this.disabledValue = value;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#conflictModIds
         */
        public Builder<V> conflictModIds(String... ids)
        {
            this.conflictModIds.addAll(Arrays.asList(ids));
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#alertCondition
         */
        public Builder<V> alert(Supplier<Boolean> condition)
        {
            this.alertCondition = Suppliers.memoize(condition::get);
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#andIf
         */
        public Builder<V> andIf(Supplier<Boolean> condition)
        {
            this.andIf = Suppliers.memoize(condition::get);
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#slider
         * @see TweakSlider
         */
        public Builder<V> slider(TweakSlider slider)
        {
            this.slider = slider;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#ignore
         */
        public Builder<V> ignore()
        {
            this.ignore = true;
            return this;
        }

        /**
         * Set this tweak's status to "LOADED".
         * Reference "see also" tweak field.
         * @see Tweak#status
         */
        public Builder<V> load()
        {
            this.status = TweakStatus.LOADED;
            return this;
        }

        /**
         * Set this tweak's placement at the "TOP" of the container.
         * Reference "see also" tweak field.
         * @see Tweak#placement
         */
        public Builder<V> top()
        {
            this.placement = TweakPlacement.TOP;
            return this;
        }

        /**
         * Set this tweak's placement at the "BOTTOM" of the container.
         * Reference "see also" tweak field.
         * @see Tweak#placement
         */
        public Builder<V> bottom()
        {
            this.placement = TweakPlacement.BOTTOM;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#doesChunkReload
         */
        public Builder<V> reloadChunks()
        {
            this.doesChunkReload = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#doesResourceReload
         */
        public Builder<V> reloadResources()
        {
            this.doesResourceReload = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#hasColorValue
         */
        public Builder<V> colorTweak()
        {
            this.hasColorValue = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#noNetworkCheck
         */
        public Builder<V> ignoreNetworkCheck()
        {
            this.noNetworkCheck = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#newForUpdate
         */
        public Builder<V> newForUpdate()
        {
            this.newForUpdate = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#noTooltip
         */
        public Builder<V> noTooltip()
        {
            this.noTooltip = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#hasWarningTag
         */
        public Builder<V> warningTag()
        {
            this.hasWarningTag = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#hasOptifineTag
         */
        public Builder<V> optifineTag()
        {
            this.hasOptifineTag = true;
            return this;
        }

        /**
         * Reference "see also" tweak field.
         * @see Tweak#hasSodiumTag
         */
        public Builder<V> sodiumTag()
        {
            this.hasSodiumTag = true;
            return this;
        }

        /**
         * Finalize the building process for this tweak.
         * @return A new tweak instance.
         * @see Tweak
         */
        public Tweak<V> build()
        {
            Tweak<V> tweak = new Tweak<>(this.defaultValue, this.side, this.container);

            tweak.value = this.defaultValue;
            tweak.sentValue = this.defaultValue;
            tweak.cacheValue = this.defaultValue;
            tweak.disabledValue = this.disabledValue;
            tweak.conflictModIds = this.conflictModIds;
            tweak.alertCondition = this.alertCondition;
            tweak.andIf = this.andIf;
            tweak.ignore = this.ignore;
            tweak.status = this.status;
            tweak.slider = this.slider;
            tweak.placement = this.placement;
            tweak.newForUpdate = this.newForUpdate;
            tweak.noTooltip = this.noTooltip;
            tweak.noNetworkCheck = this.noNetworkCheck;
            tweak.doesChunkReload = this.doesChunkReload;
            tweak.doesResourceReload = this.doesResourceReload;
            tweak.hasColorValue = this.hasColorValue;
            tweak.hasWarningTag = this.hasWarningTag;
            tweak.hasOptifineTag = this.hasOptifineTag;
            tweak.hasSodiumTag = this.hasSodiumTag;

            return tweak;
        }
    }
}
