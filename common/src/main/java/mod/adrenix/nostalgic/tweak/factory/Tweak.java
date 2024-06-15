package mod.adrenix.nostalgic.tweak.factory;

import com.google.common.base.Suppliers;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.config.cache.CacheHolder;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.config.cache.ConfigReflect;
import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.TweakIssue;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.lang.DecodeLang;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

/**
 * This class provides the tweak structure that all tweaks will share. Each tweak will store different types of
 * information and handle that storage differently. The implementation information for value storage and handling is
 * defined by {@link TweakMeta}.
 */
public abstract class Tweak<T> implements TweakMeta<T>
{
    /* Fields */

    protected final TweakBuilder<?> builder;
    private final Supplier<Boolean> conflict;
    private final CacheHolder<TweakStatus> statusHolder;
    private CacheMode cacheMode;
    private String jsonId = null;

    /* Constructor */

    Tweak(TweakBuilder<?> builder)
    {
        this.builder = builder;
        this.cacheMode = CacheMode.LOCAL;
        this.statusHolder = CacheHolder.from(builder.status, this::getCacheMode);
        this.conflict = Suppliers.memoize(() -> builder.conflictMods.stream().anyMatch(ModTracker::isInstalled));

        builder.container.addTweak(this);
    }

    /* Abstraction */

    /**
     * This method is used in the class's {@link #toString()}. The default output from the {@code toString} method is
     * {@code [tweak={jsonId:%s, type:%s}]} where {@code type:%s} is replaced by the value returned by this method.
     *
     * @return A simple class name for the variable type the tweak is handling.
     */
    protected abstract String getTypeName();

    /* Methods */

    /**
     * The JSON identifier associated with this tweak. This should be invoked in either the client config JSON structure
     * class or the server config JSON structure class.
     *
     * @param jsonId The config key that points to this tweak.
     * @see mod.adrenix.nostalgic.config.ClientConfig
     * @see mod.adrenix.nostalgic.config.ServerConfig
     */
    private void setJsonId(String jsonId)
    {
        this.jsonId = jsonId;
    }

    /**
     * Get the config JSON identifier for this tweak. This key is set using {@link #setJsonId(String)} in the client or
     * server config structure class.
     *
     * <p><br>
     * This should only be used for reflection. If you want to get a tweak from the cache map, then use the
     * {@link TweakPool}.
     *
     * @return The json key that is used in the config file.
     */
    public String getJsonId()
    {
        if (this.jsonId == null)
            throw new AssertionError(String.format("Tweak (%s) has not set a config json identifier", this.getTypeName()));

        return this.jsonId;
    }

    /**
     * The format of a tweak's path json identifier will be the tweak's category json identifier followed by the tweak's
     * json identifier. For example, {@code gameplay.oldLadderGap}. The keys in the {@link TweakPool} map follow this
     * format.
     *
     * @return A full config json identifier.
     * @throws AssertionError If the tweak's json identifier config key has not yet been set.
     */
    public String getJsonPathId()
    {
        if (this.getContainer().isRoot())
            return this.getJsonId();

        return this.getCategory().getJsonId() + "." + this.getJsonId();
    }

    @Override
    public T register(String jsonId)
    {
        this.setJsonId(jsonId);
        TweakPool.TWEAK_MAP.putIfAbsent(this.getJsonPathId(), this);

        return this.getDefault();
    }

    @Override
    public T get()
    {
        if (!this.isExtraConditionMet())
            return this.getDisabled();

        return TweakPipeline.get(this);
    }

    /**
     * Get the side this tweak belongs to; either {@code CLIENT}, {@code SERVER}, or {@code DYNAMIC}.
     *
     * @return A tweak side enumeration value.
     */
    public TweakEnv getEnv()
    {
        return this.builder.env;
    }

    /**
     * @return Whether this tweak only applies changes to the client.
     */
    public boolean isClient()
    {
        return this.getEnv() == TweakEnv.CLIENT;
    }

    /**
     * @return Whether this tweak only applies changes to the server.
     */
    public boolean isServer()
    {
        return this.getEnv() == TweakEnv.SERVER;
    }

    /**
     * When this is {@code true}, this tweak can be controlled by a server with Nostalgic Tweaks installed. If the
     * server has disabled this tweak, or if the client is not connected to a server with the mod installed, then the
     * client's value will be used.
     *
     * @return Whether this tweak applies changes to either the client or server.
     */
    public boolean isDynamic()
    {
        return this.getEnv() == TweakEnv.DYNAMIC;
    }

    /**
     * @return Whether this tweak is server-side or dynamic.
     */
    public boolean isMultiplayerLike()
    {
        return this.isServer() || this.isDynamic();
    }

    /**
     * Get the container associated with this tweak. Each tweak must be assigned to a container, whether that container
     * be a category or group.
     *
     * @return A tweak container instance (category or group).
     */
    public Container getContainer()
    {
        return this.builder.container;
    }

    /**
     * Get the category container associated with this tweak. Each tweak will be assigned to a category.
     *
     * @return A category instance.
     */
    public Container getCategory()
    {
        return this.builder.container.getCategory();
    }

    @Override
    public CacheMode getCacheMode()
    {
        return this.cacheMode;
    }

    @Override
    public void setCacheMode(CacheMode cacheMode)
    {
        this.cacheMode = cacheMode;
    }

    @Override
    public boolean isCurrentCacheSavable()
    {
        return switch (this.cacheMode)
        {
            case LOCAL -> this.isLocalSavable();
            case NETWORK -> this.isNetworkSavable();
        };
    }

    @Override
    public boolean isCacheUndoable()
    {
        return switch (this.cacheMode)
        {
            case LOCAL -> this.isLocalSavable();
            case NETWORK -> this.isNetworkSavable();
        };
    }

    /**
     * Send this tweak's network value to the server, if possible. If sending to the server was not possible, and the
     * player is connected to a server with the mod installed, then the network cache will be restored to what was
     * originally sent by the server.
     *
     * @throws RuntimeException If the server tried to invoke this method.
     */
    protected void sendIfPossible()
    {
        if (NostalgicTweaks.isServer())
            throw new RuntimeException("Server tried to access client-only tweak method");

        if (NostalgicTweaks.isNetworkVerified() && this.isMultiplayerLike())
        {
            if (NetUtil.isPlayerOp() && NetUtil.isMultiplayer())
            {
                ToastNotification.changeOnServer();
                this.sendToServer();
            }
            else
                this.setReceived(this.fromServer());
        }
    }

    /**
     * The runtime utility reload flags in the runtime updated appropriately if this tweak requires those updates.
     *
     * @throws RuntimeException If the server tried to invoke this method.
     */
    protected void updateReloadFlags()
    {
        if (NostalgicTweaks.isServer())
            throw new RuntimeException("Server tried to access client-only tweak method");

        if (this.isChunkReloadRequired())
            AfterConfigSave.setChunksToReload();

        if (this.isResourceReloadRequired())
            AfterConfigSave.setResourcesToReload();
    }

    @Override
    public void applyCurrentCache()
    {
        if (NostalgicTweaks.isServer())
            throw new RuntimeException("Server tried to access client-only tweak method");

        this.updateReloadFlags();

        switch (this.cacheMode)
        {
            case LOCAL -> this.setDisk(this.fromLocal());
            case NETWORK -> this.sendIfPossible();
        }

        if (NetUtil.isLocalHost())
            this.sendToAll();
    }

    @Override
    public void applyCacheAndSend()
    {
        if (NostalgicTweaks.isServer())
            throw new RuntimeException("Server tried to access client-only tweak method");

        this.updateReloadFlags();
        this.setDisk(this.fromLocal());
        this.sendIfPossible();

        if (NetUtil.isLocalHost())
            this.sendToAll();
    }

    @Override
    public void applyReflection(T value)
    {
        if (NostalgicTweaks.isClient())
            ConfigReflect.setClientField(this, value);
        else
            ConfigReflect.setServerField(this, value);
    }

    /**
     * @return When {code true}, a "New" tag appears next to this tweak's name in the configuration menu row.
     */
    public boolean isNew()
    {
        return this.builder.newForUpdate;
    }

    /**
     * Functional shortcut that is the opposite of {@link #isNew()}.
     *
     * @return Whether this tweak was added in a previous update.
     */
    public boolean isOld()
    {
        return !this.isNew();
    }

    /**
     * This indicates whether a tweak should go through the mod's network verification check when determining if a tweak
     * should use its configured value when playing on a multiplayer server. This check is only performed on tweaks that
     * run on the server. For example, some tweaks, like the old square border tweak, have code queried before the
     * network can be verified. Therefore, it is important that the tweak be skipped in the mod config's network
     * verification check pipeline.
     *
     * @return If {@code true}, this tweak will not go through the mod config's network verification check pipeline.
     */
    public boolean isNetworkCheckIgnored()
    {
        return this.builder.noNetworkCheck;
    }

    /**
     * @return Whether the game context has a network available for tweaks.
     */
    public boolean isNetworkAvailable()
    {
        return this.isMultiplayerLike() && NostalgicTweaks.isNetworkVerified() && NetUtil.isMultiplayer() && !NetUtil.isLocalHost();
    }

    /**
     * @return Whether the game context does not a network available for tweaks.
     */
    public boolean isNetworkUnavailable()
    {
        return !this.isNetworkAvailable();
    }

    @Override
    public boolean isNetworkSavable()
    {
        return this.isNetworkAvailable() && this.isCacheSavable(this.fromServer(), this.fromNetwork());
    }

    /**
     * Checks if a multiplayer-like tweak cannot be changed by the client's configuration menu. This prevents issues
     * when trying to change tweaks that shouldn't be changed because the player lacks operator permissions on a server
     * running Nostalgic Tweaks. Even if the client bypasses this, the server will reject a tweak change request if the
     * server player doesn't have operator permissions.
     *
     * @return Whether the multiplayer-like tweak is locked out from being changed by the client.
     */
    public boolean isNetworkLocked()
    {
        if (NostalgicTweaks.isServer() || this.isClient() || this.isLocalMode())
            return false;

        if (NetUtil.isSingleplayer() || NetUtil.isLocalHost() || !NostalgicTweaks.isNetworkVerified())
            return false;

        return !NetUtil.isPlayerOp();
    }

    /**
     * The inverse method of {@link #isNetworkLocked()}.
     *
     * @return Whether a multiplayer-like tweak can be changed by the client's configuration menu.
     * @see #isNetworkUnlocked()
     */
    public boolean isNetworkUnlocked()
    {
        return !this.isNetworkLocked();
    }

    /**
     * Lang keys follow the format: {@code gui.nostalgic_tweaks.config.{category}.{tweak}.warning}.
     *
     * @return When {@code true}, a "Warning" tag in the configuration menu row.
     */
    public boolean isWarningTag()
    {
        return this.builder.hasWarningTag;
    }

    /**
     * A {@link TweakAlert} instance that holds a supplier that returns a truthful boolean when a certain alert
     * condition is met.  When met, an "Alert" tag will appear next to the tweak's name in the configuration menu row
     * belonging to this tweak.
     *
     * @return When {@code true}, an "Alert" tag in the configuration menu row.
     */
    public boolean isAlertTag()
    {
        return this.builder.alert.getCondition().get();
    }

    /**
     * Indicates if the server tweak does not work in server-side-only mode. Lang keys follow the format:
     * {@code gui.nostalgic_tweaks.config.{category}.{tweak}.no_sso}.
     *
     * @return When {@code true}, a "No SSO" tag appears in the configuration menu row.
     */
    public boolean isNotSSO()
    {
        return this.builder.noSSO;
    }

    /**
     * @return Whether all chunks need reloaded after this tweak is saved with a new value.
     */
    public boolean isChunkReloadRequired()
    {
        return this.builder.doesChunkReload;
    }

    /**
     * @return Whether the game's resources need reloaded after this tweak is saved with a new value.
     */
    public boolean isResourceReloadRequired()
    {
        return this.builder.doesResourceReload;
    }

    /**
     * @return Whether this tweak should be at the top of its container.
     */
    public boolean isTop()
    {
        return this.builder.top;
    }

    /**
     * When a tweak is "ignored" it is not used in automatic row generation in the configuration menu, and the value
     * stored on disk will always be returned when retrieving the value through the network verification check
     * pipeline.
     *
     * @return Whether this tweak is ignored.
     * @see #isNotIgnored()
     */
    public boolean isIgnored()
    {
        return this.builder.ignoreIf.getAsBoolean();
    }

    /**
     * Functional shortcut for checking if this tweak is ignored.
     *
     * @return Whether this tweak is not ignored.
     * @see #isIgnored()
     */
    public boolean isNotIgnored()
    {
        return !this.isIgnored();
    }

    /**
     * If this tweak's container (or any of its parents) is flagged as {@code internal}, then this tweak should be
     * ignored from certain config features, such as bulk-toggling.
     *
     * @return Whether this container is {@code internal}.
     * @see #isNotInternal()
     */
    public boolean isInternal()
    {
        return this.getCategory().isInternal() || this.getContainer()
            .getGroupSetToCategory()
            .stream()
            .anyMatch(Container::isInternal);
    }

    /**
     * Functional shortcut for checking if this tweak is internal.
     *
     * @return Whether this tweak is not internal.
     * @see #isInternal()
     */
    public boolean isNotInternal()
    {
        return !this.isInternal();
    }

    /**
     * Since mods can't be loaded/unloaded during runtime, the conflict supplier is memoized. When the supplier is
     * {@code true}, it indicates that this tweak shouldn't be enabled.
     *
     * @return Checks if this tweak is conflicting with another mod.
     */
    public boolean isModConflict()
    {
        return this.conflict.get();
    }

    /**
     * Change the loading status of this tweak.
     *
     * @param cacheMode The cache mode to store the status in.
     * @param status    The new {@link TweakStatus} enumeration value.
     * @see #setEnvStatus(TweakStatus)
     */
    public void setStatus(CacheMode cacheMode, TweakStatus status)
    {
        switch (cacheMode)
        {
            case LOCAL -> this.statusHolder.setLocal(status);
            case NETWORK -> this.statusHolder.setNetwork(status);
        }
    }

    /**
     * Change the local cache status of this tweak.
     *
     * @param status The new {@link TweakStatus} enumeration value.
     */
    public void setEnvStatus(TweakStatus status)
    {
        this.statusHolder.setLocal(status);
    }

    /**
     * If the server is invoking this method, then the local status is always returned; otherwise, the status based on
     * the current cache mode is returned.
     *
     * @return The current {@link TweakStatus} enumeration value.
     * @see #getStatus(CacheMode)
     * @see #getEnvStatus()
     */
    public TweakStatus getStatus()
    {
        if (NostalgicTweaks.isServer())
            return this.statusHolder.getLocal();

        return this.statusHolder.get();
    }

    /**
     * Get the status of this tweak that is based on the given cache mode.
     *
     * @param cacheMode The {@link CacheMode} to collect data from.
     * @return A {@link TweakStatus} based on the given cache mode.
     * @see #getStatus()
     * @see #getEnvStatus()
     */
    public TweakStatus getStatus(CacheMode cacheMode)
    {
        return switch (cacheMode)
        {
            case LOCAL -> this.statusHolder.getLocal();
            case NETWORK -> this.statusHolder.getNetwork();
        };
    }

    /**
     * Gets the local status. Each environment internal tweak status will always be what is stored locally. The network
     * cache changes based on what is sent by the server with the mod installed.
     *
     * @return A locally cached {@link TweakStatus}.
     * @see #getStatus(CacheMode)
     * @see #getStatus()
     */
    public TweakStatus getEnvStatus()
    {
        return this.statusHolder.getLocal();
    }

    /**
     * @return Whether this tweak status is set to {@code LOADED}.
     */
    public boolean isLoaded()
    {
        return this.getStatus() == TweakStatus.LOADED;
    }

    /**
     * @return Whether this tweak status is set to {@code FAIL} or if there is a mod conflict.
     */
    public boolean isConflictOrFail()
    {
        return this.getStatus() == TweakStatus.FAIL || this.isModConflict();
    }

    /**
     * @return When {@code true}, the tweak will be considered enabled. Otherwise, the tweak will be considered
     * "disabled" and will return its disabled value.
     */
    public boolean isExtraConditionMet()
    {
        return this.builder.andIf.getAsBoolean();
    }

    /**
     * Gives a set of mod {@link TweakIssue} enumerations that will appear below a tweak's description within automatic
     * row generation.
     *
     * @return An unmodifiable set of {@link TweakIssue} enumerations.
     */
    public Set<TweakIssue> getModIssues()
    {
        return Collections.unmodifiableSet(this.builder.modIssues);
    }

    /**
     * The config file groups <b color=#4CC143>tweaks</b> by <b color=#0094FF>category</b>. The format for tweaks in the
     * lang file follows:<p>
     *
     * <code color=#C19E43>gui.nostalgic_tweaks.config.<b color=#0094FF>{$1}</b>.<b color=#4CC143>{$2}</b></code>
     * <p>
     * Where <code><b color=#0094FF>{$1}</b></code> is the tweak's category identifier and
     * <code><b color=#4CC143>{$2}</b></code> is the tweak's identifier.
     * <p>
     * Group containers are only used by the user interface to help with organizing tweaks.
     * <p>
     * The config file doesn't acknowledge groups within categories, so it is not possible for two tweaks with the same
     * identifier to be within the same category.
     *
     * @return Get the raw lang file key for this tweak.
     */
    public String getLangKey()
    {
        String modId = NostalgicTweaks.MOD_ID;
        String categoryJsonId = this.getCategory().getJsonId();
        String jsonId = this.jsonId;

        if (this.getContainer().isRoot())
            return String.format("gui.%s.config.%s", modId, jsonId);

        return String.format("gui.%s.config.%s.%s", modId, categoryJsonId, jsonId);
    }

    /**
     * @return Get the tweak's translated name in component form.
     */
    public Component getTranslation()
    {
        return Component.translatable(this.getLangKey());
    }

    /**
     * Compares two tweak name strings lexicographically, ignoring case differences.
     *
     * @param tweak The {@link Tweak} to compare names with.
     * @return A negative integer, zero, or a positive integer as the specified tweak's translated name is greater than,
     * equal to, or less than this tweak's translated name, ignoring case considerations.
     */
    public int compareTranslationName(Tweak<?> tweak)
    {
        return this.getTranslation().getString().compareToIgnoreCase(tweak.getTranslation().getString());
    }

    /**
     * To assign a description to a tweak, append the tweak's lang key with {@code .info}.
     *
     * @return Get the description translatable component for this tweak.
     */
    public Component getDescription()
    {
        return DecodeLang.findAndReplace(Component.translatable(this.getLangKey() + ".info"));
    }

    /**
     * To assign a conflict message to a tweak, append the tweak's lang key with {@code .conflict}.
     *
     * @return Get the conflict description translatable component for this tweak.
     */
    public Component getConflictMessage()
    {
        return DecodeLang.findAndReplace(Component.translatable(this.getLangKey() + ".conflict"));
    }

    /**
     * To assign a custom warning label to a tweak, append the tweak's lang key with {@code .warn}.
     *
     * @return Get the translation warning message for this tweak.
     */
    public Component getWarningMessage()
    {
        return DecodeLang.findAndReplace(Component.translatable(this.getLangKey() + ".warn"));
    }

    /**
     * By default, if a {@link TweakAlert} instance was never assigned to this tweak, then an "empty" tweak alert
     * instance is used. The {@link TweakAlert#NONE} definition <i>always</i> returns {@code false} and will display an
     * "error" message if that instance's translation is retrieved.
     *
     * @return Get a translatable component for the alert instance associated with this tweak.
     */
    public Component getAlertMessage()
    {
        return DecodeLang.findAndReplace(this.builder.alert.getMessage());
    }

    /**
     * To assign a custom No SSO label to a tweak, append the tweak's lang key with {@code .no_sso}, or the default
     * tooltip message will appear.
     *
     * @return Get the translation No SSO message for this tweak.
     */
    public Component getNoSSOMessage()
    {
        String langKey = this.getLangKey() + ".no_sso";
        Component message = DecodeLang.findAndReplace(Component.translatable(langKey));

        if (message.getString().equals(langKey))
            return Lang.Tag.NO_SSO_TOOLTIP.get();

        return message;
    }

    @Override
    public String toString()
    {
        String format = "[tweak={jsonId:%s, type:%s}]";
        String jsonId = this.getJsonPathId();
        String type = this.getTypeName();

        return String.format(format, jsonId, type);
    }
}
