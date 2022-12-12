package mod.adrenix.nostalgic.common.config.reflect;

import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Provides helpers for both the client and server tweak caches.
 * The server utilizes this class so do not include client code.
 */

public abstract class TweakCommonCache
{
    /* Common Fields */

    /**
     * All tweak caches have a group and a unique key within that group. These keys match what is saved onto disk.
     * It is important that the keys stay in sync with the client config. This is why keys are established within
     * class loaded code blocks underneath each tweak entry.
     */
    protected final String key;

    /**
     * A tweak's identifier is its key kept within the tweak's sided cache map.
     * This identifier will be used in the client cache map or server cache map.
     */
    protected final String id;

    /**
     * All tweaks have a group associated with them.
     * Some tweaks may be further categorized by a container type.
     */
    protected final TweakGroup group;

    /**
     * All tweaks will have a status associated with them.
     *
     * During construction, all tweaks will be labeled as failures unless defined otherwise in a config definition
     * class. Otherwise, a status will not be changed until the tweak's code is queried during runtime.
     */
    protected TweakStatus status;

    /* Cached Metadata */

    /**
     * Some tweaks may be associated with a custom list. These lists can be used by the server, so metadata is cached
     * here if it is present.
     */
    @Nullable protected final TweakData.List list;

    /* Common Constructor */

    protected TweakCommonCache(TweakGroup group, String key)
    {
        this.group = group;
        this.key = key;
        this.id = generateKey(group, key);

        this.list = this.getMetadata(TweakData.List.class);

        if (this.isMetadataPresent(TweakData.EntryStatus.class))
            this.status = this.getMetadata(TweakData.EntryStatus.class).status();
        else
            this.status = TweakStatus.FAIL;
    }

    /* Common Setters */

    /**
     * Can be set anywhere and updated at anytime.
     * @see TweakStatus
     * @param status The current status of a tweak.
     */
    public void setStatus(TweakStatus status) { this.status = status; }

    /* Common Getters */

    /**
     * The status of a tweak is updated when its code is executed.
     * @see TweakStatus
     * @return Whether a tweak has failed to load, has not attempted to load, or is loaded.
     */
    public TweakStatus getStatus() { return this.status; }

    /**
     * Each tweak will have a group type attached to it.
     * @return The group type associated with this tweak.
     */
    public TweakGroup getGroup() { return this.group; }

    /**
     * Each tweak has a config key.
     *
     * This is not the same as a tweak's identifier which is used in a hashmap.
     * These two fields are detached from each other to prevent same name key conflicts from different groups.
     * @return The config key for this tweak.
     */
    public String getKey() { return this.key; }

    /**
     * Get a tweak's identifier which is its key in a sided cache map.
     * @return The key used to identify the tweak in the client cache map or server cache map.
     */
    public String getId() { return this.id; }

    /* Common Metadata */

    /**
     * @return A tweak's list data if it is present.
     */
    @Nullable public TweakData.List getList() { return this.list; }

    /* Common Methods */

    /**
     * Generates a unique hash map key identifier based on a tweak's group type and config key.
     * @param group The associated group type.
     * @param key A configuration key.
     * @return A unique hash map key identifier.
     */
    public static String generateKey(TweakGroup group, String key) { return group.toString() + "@" + key; }

    /**
     * Get data from an annotation. Ensure the annotation being accessed is <b>not</b> already cached. If this method is
     * being invoked for checking metadata presence, then use {@link TweakCommonCache#isMetadataPresent(Class)} instead.
     *
     * @param annotation The annotation class to check for data.
     * @return Metadata from the annotation class instance if it was present.
     * @param <A> The annotation class type.
     */
    public <A extends Annotation> A getMetadata(Class<A> annotation)
    {
        return CommonReflect.getAnnotation(this, annotation);
    }

    /**
     * Checks if the given annotation class type has an instance present on this tweak.
     * @param annotation The annotation class to check for data.
     * @return Whether metadata from the given annotation class exists for this tweak.
     * @param <A> The annotation class type.
     */
    public <A extends Annotation> boolean isMetadataPresent(Class<A> annotation)
    {
        return this.getMetadata(annotation) != null;
    }

    /**
     * Checks if the given annotation class type is missing for this tweak.
     * @param annotation The annotation class to check for data.
     * @return Whether metadata from the given annotation class didn't exist for this tweak.
     * @param <A> The annotation class type.
     */
    public <A extends Annotation> boolean isMetadataMissing(Class<A> annotation)
    {
        return this.getMetadata(annotation) == null;
    }

    /* Translation Getters */

    public static final String RELATED_APPENDIX = ".@Related";

    public String getLangKey() { return this.group.getLangKey() + "." + this.key; }
    public String getTooltipKey() { return this.getLangKey() + ".@Tooltip"; }
    public String getWarningKey() { return this.getLangKey() + ".@Warning"; }
    public String getOptifineKey() { return this.getLangKey() + ".@Optifine"; }
    public String getSodiumKey() { return this.getLangKey() + ".@Sodium"; }
    public String getRelatedKey() { return this.getLangKey() + RELATED_APPENDIX; }
    public String getTranslation() { return this.getComponentTranslation().getString(); }
    public String getTooltipTranslation() { return Component.translatable(this.getTooltipKey()).getString(); }
    public Component getComponentTranslation() { return Component.translatable(this.getLangKey()); }
}
