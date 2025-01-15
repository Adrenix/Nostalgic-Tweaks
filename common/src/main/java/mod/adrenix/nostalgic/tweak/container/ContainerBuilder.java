package mod.adrenix.nostalgic.tweak.container;

import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ContainerBuilder
{
    /* Fields */

    final String jsonId;
    final ContainerType type;

    boolean internal;
    boolean description;
    Container inheritance;
    Container parent;
    Color color;
    TextureIcon icon;

    /* Constructor */

    ContainerBuilder(String jsonId, ContainerType type)
    {
        this.jsonId = jsonId;
        this.type = type;
    }

    /* Methods */

    /**
     * Set the parent container for this group container.
     *
     * @param container Another group or category container instance.
     */
    public ContainerBuilder parent(Container container)
    {
        this.parent = container;

        return this;
    }

    /**
     * Indicate that this container will have a description. It will be displayed at the top of the container's group in
     * the user interface. Append a {@code .info} at the end of the container's {@code jsonId} key in the lang file. For
     * example, {@code gui.nostalgic_tweaks.container.mod.config.info}.
     */
    public ContainerBuilder description()
    {
        this.description = true;

        return this;
    }

    /**
     * If this container contains tweaks that should be ignored from certain config features, such as bulk-toggling,
     * then this flag should be set to {@code true}.
     */
    public ContainerBuilder internal()
    {
        return this;
    }

    /**
     * Inherit the tweaks from the given category. This is useful in situations where a config category does not need
     * its own section in the user interface. For example, the mod category inherits the root category.
     *
     * @param category A category tweak container.
     * @throws AssertionError If the given tweak container is not a category or the inheritor is not a category.
     */
    public ContainerBuilder inherit(Container category)
    {
        if (this.type != ContainerType.CATEGORY)
            throw new AssertionError("Only 'category' containers can inherit other categories");

        if (category.getType() != ContainerType.CATEGORY)
            throw new AssertionError("Container inheritance must be from a 'category' container");

        this.inheritance = category;

        return this;
    }

    /**
     * Change the theme color of this container.
     *
     * @param color A {@link Color} instance.
     */
    public ContainerBuilder color(Color color)
    {
        this.color = color.lock();

        return this;
    }

    /**
     * Change the theme color of this container.
     *
     * @param color An RGB integer (ex: 0xFFABC2).
     */
    public ContainerBuilder color(int color)
    {
        this.color = new Color(color).lock();

        return this;
    }

    /**
     * Change the icon for this container.
     *
     * @param icon An {@link TextureIcon} instance.
     */
    public ContainerBuilder icon(TextureIcon icon)
    {
        this.icon = icon;

        return this;
    }

    /**
     * Change the icon for this container.
     *
     * @param item An item instance to retrieve an icon from.
     */
    public ContainerBuilder icon(Item item)
    {
        this.icon = TextureIcon.fromItem(item);

        return this;
    }

    /**
     * Change the icon for this container.
     *
     * @param block A block instance to retrieve an icon from.
     */
    public ContainerBuilder icon(Block block)
    {
        this.icon = TextureIcon.fromBlock(block);

        return this;
    }

    /**
     * Change the icon for this container.
     *
     * @param location A {@link TextureLocation} instance.
     */
    public ContainerBuilder icon(TextureLocation location)
    {
        this.icon = TextureIcon.fromTexture(location);

        return this;
    }

    /**
     * Finalize the building process for this container.
     *
     * @return A new {@link Container} instance.
     */
    public Container build()
    {
        return new Container(this);
    }
}
