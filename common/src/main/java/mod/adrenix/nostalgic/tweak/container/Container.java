package mod.adrenix.nostalgic.tweak.container;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A container will either be a category (which is a collection of groups and tweaks) or a group (which is only a
 * collection of tweaks). The configuration screen will automatically build tweaks and groups in the order they are
 * defined. Container groups can subscribe to other groups, and the configuration screen will automatically indent the
 * groups based on the definition tree.
 */
public class Container
{
    /* Builders */

    /**
     * Create a new category container. The lang file key will be of the format
     * <code>gui.nostalgic_tweaks.container.{jsonId}</code>.
     *
     * @param jsonId The json identifier of the container. <b>This id must match the key used in the config json.</b>
     */
    public static ContainerBuilder category(String jsonId)
    {
        return new ContainerBuilder(jsonId, ContainerType.CATEGORY);
    }

    /**
     * Create a new group container. The lang file key will be of the format
     * <code>gui.nostalgic_tweaks.container.{categoryId}.{id}</code>. For more information about automatic language key
     * information, reference the "see also" section.
     *
     * @param parent The parent container, which can be a category or group.
     * @param id     The id of the group.
     * @see Container#getJsonPathId()
     */
    public static ContainerBuilder group(Container parent, String id)
    {
        return new ContainerBuilder(id, ContainerType.GROUP).parent(parent);
    }

    /* Caching */

    /**
     * This set contains a list of all category containers. This cache is used by the user interface to generate tab
     * widgets at the top of the screen.
     */
    public static final LinkedHashSet<Container> CATEGORIES = new LinkedHashSet<>();

    /**
     * This set contains a list of all group containers. This cache is not used by the user interface. Instead, it is
     * used to ensure that there are not two groups with exact identifiers. If such a situation occurs, then the game
     * will crash since this is an issue produced during mod development.
     */
    public static final LinkedHashSet<Container> GROUPS = new LinkedHashSet<>();

    /* Inspection */

    /**
     * Inspects the container for potential runtime issues.
     *
     * @param container The {@link Container} to inspect.
     */
    private static void inspect(Container container)
    {
        try
        {
            container.getTweaks().forEach(tweak -> {
                if (NostalgicTweaks.isServer() && tweak.isClient())
                    return;

                tweak.getJsonId();
            });
        }
        catch (Throwable throwable)
        {
            String header = String.format("Container (%s) failed inspection: ", container.getJsonPathId());
            String message = header + throwable.getMessage();

            throw new AssertionError(message, throwable.getCause());
        }
    }

    /**
     * Scans all categories and groups for tweaks that have not yet set their config json identifiers in at least one of
     * the sided configs. If a tweak fails inspection, the mod will crash the game with an assertion error since this
     * needs resolved by a developer.
     */
    public static void scanForIssues()
    {
        CATEGORIES.forEach(Container::inspect);
        GROUPS.forEach(Container::inspect);
    }

    /* Fields */

    /**
     * A container must be either a {@code CATEGORY} or a {@code GROUP}.
     */
    private final ContainerType type;

    /**
     * If this container is a category, then its id should be the JSON key that will appear in the config file.
     * Otherwise, the id can be a general string if this is a group container since groups will not appear in the config
     * file.
     */
    private final String jsonId;

    /**
     * Some containers may benefit from custom colors. The colors associated with containers can be any custom color
     * defined by {@link Color} instance.
     */
    @Nullable private final Color color;

    /**
     * Some containers may benefit from custom icons. The icon associated with containers can be any icon, whether it be
     * from a texture sheet location or item renderer.
     */
    @Nullable private final TextureIcon icon;

    /**
     * If this is a group container, then it must have a parent, which will either be a category container or another
     * group container.
     */
    @Nullable private final Container parent;

    /**
     * Some categories may inherit other categories. The inherited category's tweaks will be included when this
     * category's {@link #getTweaks()} method is invoked.
     */
    @Nullable private final Container inheritance;

    /**
     * Some containers may have a description to help explain the purpose of the container. When this is defined, an
     * information row is built and inserted at the top of a container's user interface group. Append a {@code .info} at
     * the end of the json key. For example, {@code gui.nostalgic_tweaks.container.mod.config.info}.
     */
    private final boolean description;

    /**
     * If this container contains tweaks that should be ignored from certain config features, such as bulk-toggling,
     * then this flag should be set to {@code true}.
     */
    private final boolean internal;

    /**
     * This linked hash set stores every tweak that uses this container. Caching occurs when a tweak is in the building
     * phase. Additions made to this set only occur during game startup.
     */
    private final LinkedHashSet<Tweak<?>> tweaks;

    /**
     * Each container will have a category reference. If the container itself is a category, then this field points back
     * to itself.
     */
    private Container category;

    /* Constructor */

    Container(ContainerBuilder builder)
    {
        this.tweaks = new LinkedHashSet<>();

        this.jsonId = builder.jsonId;
        this.type = builder.type;
        this.color = builder.color;
        this.icon = builder.icon;
        this.internal = builder.internal;
        this.description = builder.description;
        this.inheritance = builder.inheritance;

        if (this.type == ContainerType.CATEGORY)
        {
            this.parent = null;
            this.category = this;

            if (this.isDuplicated(CATEGORIES, this))
                throw new AssertionError(String.format("Cannot have duplicate categories (%s)", this.getJsonPathId()));

            if (!this.isRoot())
                CATEGORIES.add(this);
        }
        else
        {
            this.parent = builder.parent;
            this.category = this.getCategory();

            if (this.isDuplicated(GROUPS, this))
                throw new AssertionError(String.format("Cannot have duplicate groups (%s)", this.getJsonPathId()));

            GROUPS.add(this);
        }
    }

    /* Methods */

    /**
     * Add a tweak to this container's tweak cache. This cache is referenced by the mod's configuration user interface
     * during automatic row list generation.
     *
     * @param tweak The tweak instance to add.
     */
    public void addTweak(Tweak<?> tweak)
    {
        this.tweaks.add(tweak);
    }

    /**
     * @return A linked hash set of inherited tweaks and all non-ignored tweaks stored in this container.
     */
    public LinkedHashSet<Tweak<?>> getTweaks()
    {
        LinkedHashSet<Tweak<?>> tweaks = new LinkedHashSet<>();

        if (this.inheritance != null)
            tweaks.addAll(this.inheritance.getTweaks());

        tweaks.addAll(this.tweaks.stream().filter(Tweak::isTop).toList());
        tweaks.addAll(CollectionUtil.filterOut(this.tweaks, Tweak::isIgnored, Tweak::isTop).toList());

        return tweaks;
    }

    /**
     * To get only the tweaks associated with this group, use {@link #getTweaks()}.
     *
     * @return A linked hash set of all inherited tweaks and all non-ignored tweaks stored in this container <b>and</b>
     * its children.
     */
    public LinkedHashSet<Tweak<?>> getDeepTweaks()
    {
        LinkedHashSet<Tweak<?>> tweaks = this.getTweaks();

        if (!this.getChildren().isEmpty())
            this.getChildren().stream().map(Container::getDeepTweaks).forEach(tweaks::addAll);

        return tweaks;
    }

    /**
     * Each container needs to be unique. This ensures there are duplicated containers by checking both the categories
     * and group cache sets for duplicated identifiers.
     *
     * @param checkSet  A linked hash set of containers to examine.
     * @param container The container to check.
     * @return Whether there exists a duplicate container identifier within the given set to check.
     */
    private boolean isDuplicated(LinkedHashSet<Container> checkSet, Container container)
    {
        Optional<Container> duplicate = checkSet.stream()
            .filter(checking -> Objects.equals(checking.getJsonPathId(), container.getJsonPathId()) && !checking.equals(container))
            .findFirst();

        return duplicate.isPresent();
    }

    /**
     * Get the color associated with this container. If no color is assigned to this container, then the color provided
     * will be returned.
     *
     * @param color A default {@link Color} to use if no color was assigned to this color.
     * @return A {@link Color} instance.
     */
    public Color getColor(Color color)
    {
        return Optional.ofNullable(this.color).orElse(color);
    }

    /**
     * Get the color associated with this container. If no color is assigned to this container, then the color provided
     * will be returned.
     *
     * @param color A default color to use if no color was assigned to this color.
     * @return A {@link Color} instance.
     */
    public Color getColor(int color)
    {
        return Optional.ofNullable(this.color).orElse(new Color(color));
    }

    /**
     * Get the color associated with this container. If no color is assigned, then a white color is returned.
     *
     * @return A {@link Color} instance.
     * @see Container#getColor(int)
     */
    public Color getColor()
    {
        return Optional.ofNullable(this.color).orElse(Color.WHITE);
    }

    /**
     * @return Whether a color was assigned to this container.
     */
    public boolean isColorEmpty()
    {
        return Optional.ofNullable(this.color).isEmpty();
    }

    /**
     * Get the icon associated with this container. If no icon is assigned, then an empty icon instance is returned.
     *
     * @return An {@link TextureIcon} instance.
     */
    public TextureIcon getIcon()
    {
        return Optional.ofNullable(this.icon).orElse(TextureIcon.EMPTY);
    }

    /**
     * If this container is a group container, then a parent will be present. Otherwise, the return value will be an
     * empty optional.
     *
     * @return An optional containing a group container or an empty optional if this container is a category.
     */
    public Optional<Container> getParent()
    {
        return Optional.ofNullable(this.parent);
    }

    /**
     * @return Get a description lang key for this container if it exists.
     */
    public Optional<Translation> getDescription()
    {
        if (!this.description)
            return Optional.empty();

        String key = String.format("gui.%s.container.%s.info", NostalgicTweaks.MOD_ID, this.getJsonPathId());

        return Optional.of(new Translation(key));
    }

    /**
     * @return The container type will be either {@code CATEGORY} or {@code GROUP}.
     * @see ContainerType
     */
    public ContainerType getType()
    {
        return this.type;
    }

    /**
     * @return Whether this container is a category type.
     */
    public boolean isCategory()
    {
        return this.type == ContainerType.CATEGORY;
    }

    /**
     * @return Whether this container is a group type.
     */
    public boolean isGroup()
    {
        return this.type == ContainerType.GROUP;
    }

    /**
     * If this container contains tweaks that should be ignored from certain config features, such as bulk-toggling,
     * then this flag will be {@code true}.
     *
     * @return Whether this container is {@code internal}.
     */
    public boolean isInternal()
    {
        return this.internal;
    }

    /**
     * The "root" of the config does not have a config key. Therefore, a blank id string must designate a container that
     * represents the root of the config.
     *
     * @return Whether this container is the root container.
     * @see Container#jsonId
     */
    public boolean isRoot()
    {
        return this.jsonId.isBlank();
    }

    /**
     * The returned set will contain containers starting from the subscribed container until it reaches a parent
     * category. The returned set <b>will</b> have the container invoking this method and will <b>not</b> contain the
     * root container. If the container invoking this method is a category itself, then the returned set will be empty.
     *
     * <p><br>
     * Here is an example of what a possible set could look like:
     * <code>this, this.parent, this.parent.parent, this.parent.parent.category</code>
     *
     * @return An ordered hash set of containers that stops when an entry is a category container.
     * @see #getGroupSetFromCategory()
     */
    public LinkedHashSet<Container> getGroupSetToCategory()
    {
        LinkedHashSet<Container> containers = new LinkedHashSet<>(List.of(this));
        Container scanning = this.parent;

        if (this.parent == null)
            return containers;
        else
            containers.add(this.parent);

        while (scanning.getParent().isPresent())
        {
            if (scanning.isRoot())
                break;

            scanning = scanning.getParent().get();
            containers.add(scanning);
        }

        return containers;
    }

    /**
     * @return Reversed ordered {@link LinkedHashSet} that goes from a category to this container.
     * @see #getGroupSetToCategory()
     */
    public LinkedHashSet<Container> getGroupSetFromCategory()
    {
        List<Container> containers = new ArrayList<>(this.getGroupSetToCategory());
        Collections.reverse(containers);

        return new LinkedHashSet<>(containers);
    }

    /**
     * @return A hash set of all groups that have this container as their parent.
     */
    public LinkedHashSet<Container> getChildren()
    {
        LinkedHashSet<Container> groups = new LinkedHashSet<>();

        Container.GROUPS.stream()
            .filter(group -> group.getParent().stream().anyMatch(this::equals))
            .forEachOrdered(groups::add);

        return groups;
    }

    /**
     * Get the root category of this container. If this is a category, then itself is returned.
     *
     * @return A root category container parent.
     */
    public Container getCategory()
    {
        // If this container is a category, then return the invoking instance
        if (this.parent == null)
            return this;

        // When this method is invoked, a category pointer is cached in the invoking container instance
        if (this.category != null)
            return this.category;
        else
            this.category = CollectionUtil.last(this.getGroupSetToCategory()).orElse(this);

        return this.category;
    }

    /**
     * @return A curated hash set of containers to be used for row indentation.
     */
    private HashSet<Container> getContainersForIndent()
    {
        HashSet<Container> groups = this.getGroupSetFromCategory();

        groups.remove(this.getCategory());

        return groups;
    }

    /**
     * @return Get the indent offset for a group row instance in a row list.
     */
    public int getIndentForGroupRow()
    {
        HashSet<Container> forIndent = this.getContainersForIndent();

        if (forIndent.size() == 1)
            return 0;

        return Math.max(0, forIndent.size() - 1) * 20;
    }

    /**
     * @return Get the indent offset for a tweak row instance in a row list.
     */
    public int getIndentForTweakRow()
    {
        if (this.isRoot())
            return 0;

        return this.getContainersForIndent().size() * 20;
    }

    /**
     * The string that is returned will not be a verbatim copy of this container's id if this container is not a
     * category. If this container is subscribed to a category, then the string returned by this method will be in the
     * form {@code {category.jsonId}.{this.jsonId}}. For example, assume this container's id was {@code mechanics_fire}
     * and this container is subscribed to a group container with an id of {@code mechanics}. Both this container and
     * the group are children of the {@code gameplay} category container. Therefore, the key will be returned as
     * {@code gameplay.mechanics_fire}. The {@code mechanics} group id is completely left off the key since it is not
     * necessary due to how the JSON config is structured.
     *
     * @return A unique config json identifier string for the container.
     */
    public String getJsonPathId()
    {
        return this.parent != null ? String.format("%s.%s", this.getCategory().jsonId, this.jsonId) : this.jsonId;
    }

    /**
     * The string that is returned will be a lowercase value of the container's id. This method should only be used for
     * category containers since this method is used for config reflection.
     *
     * @return The container id in lowercase format.
     */
    public String getJsonId()
    {
        return this.jsonId;
    }

    /**
     * @return A translated container name using a lang file.
     */
    @Override
    public String toString()
    {
        if (this.type == ContainerType.CATEGORY)
        {
            if (this.isRoot())
                return Component.translatable(String.format("gui.%s.container", NostalgicTweaks.MOD_ID)).getString();

            String category = "gui.%s.container.%s";
            return Component.translatable(String.format(category, NostalgicTweaks.MOD_ID, this.jsonId)).getString();
        }

        return Component.translatable(String.format("gui.%s.container.%s.%s", NostalgicTweaks.MOD_ID, this.getCategory().jsonId, this.jsonId))
            .getString();
    }
}
