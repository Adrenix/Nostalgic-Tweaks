package mod.adrenix.nostalgic.common.config.v2.container;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A tweak container will either be a category (which is a collection of groups and tweaks) or a group (which is only a
 * collection of tweaks). The configuration screen will automatically build tweaks and groups in the order they are
 * defined. Container groups can subscribe to other groups and the configuration screen will automatically indent the
 * groups based on the definition tree.
 */

public class TweakContainer
{
    /* Fields */

    /**
     * A container must be either a "CATEGORY" or a "GROUP".
     */
    private final ContainerType type;

    /**
     * If this container is a category, then its id should be the JSON key that will appear in the config file.
     * Otherwise, the id can be a general string if this is a group container since groups will not appear in the config
     * file.
     */
    private final String id;

    /**
     * If this is a group container then it must have a parent which will either be a category container or another
     * group container.
     */
    @Nullable
    private final TweakContainer parent;

    /* Constructors */

    private TweakContainer(String id)
    {
        this.type = ContainerType.CATEGORY;
        this.id = id;
        this.parent = null;
    }

    private TweakContainer(TweakContainer parent, String id)
    {
        this.type = ContainerType.GROUP;
        this.id = id;
        this.parent = parent;
    }

    /* Builders */

    /**
     * Create a new category container.
     * The lang file key will be of the format <code>gui.nostalgic_tweaks.autoconfig.category.{id}</code>.
     * @param id The id of the container. <b>This must match the spelling of the config field name.</b>
     */
    public static TweakContainer category(String id)
    {
        return new TweakContainer(id);
    }

    /**
     * Create a new group container.
     * The lang file key will be of the format <code>gui.nostalgic_tweaks.autoconfig.group.{id}</code>.
     * @param parent The parent container which can be a category or group.
     * @param id The id of the group.
     */
    public static TweakContainer group(TweakContainer parent, String id)
    {
        return new TweakContainer(parent, id);
    }

    /* Methods */

    /**
     * If this container is a group container then a parent will be present. Otherwise, the return value will be
     * <code>null</code>.
     *
     * @return An optional containing a group container or <code>null</code> if this container is a category.
     */
    public Optional<TweakContainer> getParent()
    {
        return Optional.ofNullable(this.parent);
    }

    /**
     * @return The container type will be either "CATEGORY" or "GROUP".
     * @see ContainerType
     */
    public ContainerType getType()
    {
        return this.type;
    }

    /**
     * Some containers may be only placeholders which is designated by a blank id string.
     * @see TweakContainer#id
     * @return Whether this container has a blank id string.
     */
    public boolean isRoot()
    {
        return this.id.isBlank();
    }

    /**
     * Get the root category of this container. If this is a category, then itself is returned.
     * @return A root category container parent.
     */
    public TweakContainer getCategory()
    {
        if (this.parent == null)
            return this;

        TweakContainer scanning = this.parent;

        while (true)
        {
            if (scanning.getParent().isEmpty())
                return scanning;

            scanning = scanning.getParent().get();
        }
    }

    /**
     * The string that is returned will not be a verbatim copy of the original id if this container has a parent.
     * If this container has a parent then the format of this id will be <code>{parentId}.{thisId}</code>. For example,
     * assume this container id was "fire" and it resides in the gameplay category subscribed to another container group
     * called "mechanics". The id return will appear as <code>gameplay.mechanics.fire</code>.
     *
     * @return A unique identifier string for the container.
     */
    public String getCacheKey()
    {
        return this.parent != null ? String.format("%s.%s", this.parent.id, this.id) : this.id;
    }

    /**
     * The string that is returned will be a lowercase value of the container's id. This method should only be used for
     * category containers since this method is used for config reflection.
     *
     * @return The container id in lowercase format.
     */
    public String getConfigKey()
    {
        return this.id.toLowerCase();
    }

    /**
     * @return A translated container name using a lang file.
     */
    @Override
    public String toString()
    {
        if (this.type == ContainerType.CATEGORY)
            return Component.translatable(String.format("gui.%s.autoconfig.category.%s", NostalgicTweaks.MOD_ID, this.id)).getString();

        return Component.translatable(String.format("gui.%s.autoconfig.group.%s", NostalgicTweaks.MOD_ID, this.id)).getString();
    }
}
