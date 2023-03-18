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

    private final ContainerType type;
    private final String id;

    @Nullable
    private final TweakContainer parent;

    /* Constructor */

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
     * @param id The id of the container.
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

    public Optional<TweakContainer> getParent()
    {
        return Optional.ofNullable(this.parent);
    }

    public ContainerType getType()
    {
        return this.type;
    }

    public boolean isNull()
    {
        return this.id.isBlank();
    }

    public String getId()
    {
        return this.parent != null ? String.format("%s.%s", this.parent.id, this.id) : this.id;
    }

    @Override
    public String toString()
    {
        if (this.type == ContainerType.CATEGORY)
            return Component.translatable(String.format("gui.%s.autoconfig.category.%s", NostalgicTweaks.MOD_ID, this.id)).getString();

        return Component.translatable(String.format("gui.%s.autoconfig.group.%s", NostalgicTweaks.MOD_ID, this.id)).getString();
    }
}
