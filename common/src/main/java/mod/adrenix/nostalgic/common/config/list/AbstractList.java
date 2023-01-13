package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.tweak.Tweak;
import mod.adrenix.nostalgic.util.client.NetUtil;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class provides common structure between list maps and list sets.
 * The abstract list screen and config row builders will make use of this abstraction.
 */

public abstract class AbstractList
{
    /* Fields */

    private final Tweak tweak;
    private final ListId listId;
    private final ListInclude listInclude;
    private final Set<String> disabledDefaults;
    private final Set<String> serverDisabledDefaults;

    /* Constructor */

    /**
     * Create a new abstract list instance.
     * @param tweak The tweak associated with this list.
     * @param listId The list identifier for this list.
     * @param listInclude A list include enumeration that dictates what items are allowed.
     * @param disabledDefaults A list of disabled default values that was set by the user.
     */
    public AbstractList(Tweak tweak, ListId listId, ListInclude listInclude, Set<String> disabledDefaults)
    {
        if (!tweak.isLoaded())
            tweak.setEnabled();

        this.tweak = tweak;
        this.listId = listId;
        this.listInclude = listInclude;
        this.disabledDefaults = disabledDefaults;
        this.serverDisabledDefaults = new LinkedHashSet<>(disabledDefaults);
    }

    /* Getters */

    /**
     * Each list or set must be associated with a tweak. Even if a list will not be associated with an automatically
     * generated list button, a tweak instance must still be tied to a list.
     *
     * @return This will be a tweak enumeration value that implements {@link Tweak}.
     */
    public Tweak getTweak() { return this.tweak; }

    /**
     * Each config map or set will have a list screen identifier. This is needed since some list screens will change
     * their behavior or display depending on the list identifier provided.
     *
     * @return A list identifier enumeration value.
     */
    public ListId getId() { return this.listId; }

    /**
     * Each config map or set may have a restricted filter. A list inclusion enumeration value will change what items
     * appear within the "All Items" category. Further list filtering will be disabled if the include value is not set
     * to {@link ListFilter#NONE}. The config validator will enforce any required filters as necessary.
     *
     * @return A list filter enumeration value.
     */
    public ListInclude getInclude() { return this.listInclude; }

    /**
     * The disabled defaults set contains a list of item resource keys that should be ignored when the mod queries the
     * list's default entries.
     *
     * @return A set of disabled item resource keys.
     */
    public Set<String> getDisabledDefaults()
    {
        return this.isServerNeeded() ? this.serverDisabledDefaults : this.disabledDefaults;
    }

    /* Methods */

    /**
     * If this method is run on the server, then the config list kept on disk should be returned. Otherwise, if the
     * client is connected to a Nostalgic Tweaks server, then the server-synced list should be returned.
     *
     * @return Whether the server list should be returned.
     */
    public boolean isServerNeeded()
    {
        if (NostalgicTweaks.isServer() || NetUtil.isLocalHost())
            return false;

        return NostalgicTweaks.isNetworkVerified() && NetUtil.isMultiplayer() && this.tweak.getServerCache() != null;
    }

    /**
     * Checks if the given item resource key is contained within the disabled default set.
     * @param resourceKey The item resource key to check.
     * @return Whether the given item resource key default is disabled.
     */
    public boolean isDefaultDisabled(String resourceKey) { return this.getDisabledDefaults().contains(resourceKey); }
}
