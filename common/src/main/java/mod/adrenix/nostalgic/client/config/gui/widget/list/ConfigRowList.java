package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakEmbed;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryList;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.gui.widget.SearchCrumbs;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.input.StringInput;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.client.config.gui.widget.button.*;
import mod.adrenix.nostalgic.client.config.gui.widget.button.CycleButton;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.ConfigSlider;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.mixin.widen.AbstractWidgetAccessor;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class implements a modified row list abstraction. This abstraction is based on vanilla's container object
 * selection list. Instances of this configuration row list only appear in {@link ConfigScreen}.
 */

public class ConfigRowList extends AbstractRowList<ConfigRowList.Row>
{
    /* Widget Rendering Values */

    public static final int BUTTON_START_Y = 0;
    public static final int BUTTON_HEIGHT = 20;
    public static final int BUTTON_WIDTH = 120;
    public static final int ROW_WIDGET_GAP = 2;
    public static final int TEXT_FROM_END = 8;
    public static final int TEXT_START = 8;
    public static final int CAT_TEXT_START = 28;
    public static final int SUB_TEXT_START = 48;
    public static final int EMB_TEXT_START = 68;

    /* Static Row List Tracking */

    // Holds the current row that is rendering.
    @Nullable
    private static ConfigRowList.Row rendering = null;

    // Holds the current tweak that the mouse is over in the search tab.
    @Nullable
    public static String overTweakId = null;

    // Holds a tweak identification string for tweak search jumping.
    @Nullable
    public static String jumpToTweakId = null;

    // Holds a group identification string for crumb search jumping.
    @Nullable
    public static Object jumpToContainerId = null;

    /* Widget Start Positions */

    /**
     * Retrieve the starting x-position for text rendering on rows.
     * @return The standard x-position or an x-position based on current row that is rendering.
     */
    public static int getStartX()
    {
        if (ConfigRowList.rendering == null)
            return TEXT_START;
        else
            return ConfigRowList.rendering.getIndent();
    }

    /**
     * Retrieve the starting x-position for row controllers (buttons that change tweak values).
     * @return A value of 0 if no screen is rendering; otherwise, the current screen width.
     */
    public static int getControlStartX()
    {
        return Minecraft.getInstance().screen == null ? 0 : Minecraft.getInstance().screen.width;
    }

    /* Static Getter */

    /**
     * Get the config row list instance that is associated with the active config screen.
     * @throws AssertionError When the current screen is not a config screen.
     * @return A config row list instance.
     */
    public static ConfigRowList getInstance()
    {
        if (Minecraft.getInstance().screen instanceof ConfigScreen screen)
            return screen.getWidgets().getConfigRowList();
        else
            throw new AssertionError(String.format("[%s] Cannot get row list because there is no config screen", NostalgicTweaks.MOD_NAME));
    }

    /* Config Row Instance Fields */

    /**
     * A config row list will only be use by a config screen.
     */
    public final ConfigScreen screen;

    /**
     * This field is used to keep track of the current row selected by either tab jumping or by clicking a search crumb
     * that are visible in the search group tab results.
     */
    public boolean setSelection = false;

    /* Constructor */

    /**
     * Create a new config row list instance.
     * @param screen The parent config screen instance.
     * @param width The width of the row list (should be the parent screen width).
     * @param height The height of the row list (should be the parent screen height).
     * @param startY The starting position of where individual row entries are visible.
     * @param endY The ending position of where individual row entries are no longer visible.
     * @param rowHeight The maximum height of individual row entries.
     */
    public ConfigRowList(ConfigScreen screen, int width, int height, int startY, int endY, int rowHeight)
    {
        super(width, height, startY, endY, rowHeight);

        this.screen = screen;
    }

    /* Tab Key Support */

    /**
     * Set the current tab focus on a specific widget.
     * @param widget The widget to focus on.
     */
    public void setFocusOn(AbstractWidget widget)
    {
        this.resetFocus();
        ((AbstractWidgetAccessor) widget).NT$setFocus(true);
        this.setLastSelection(widget);
    }

    /**
     * Remove any focus that may be present on row widgets.
     * This is automatically handled when using the {@link ConfigRowList#setFocusOn(AbstractWidget)} method.
     */
    public void resetFocus()
    {
        for (Row row : this.children())
        {
            for (AbstractWidget widget : row.children)
                ((AbstractWidgetAccessor) widget).NT$setFocus(false);
        }

        this.setLastSelection(null);
        setSelection = false;
    }

    /* Overrides & Utility */

    /**
     * Config row mouse click handling.
     * @param mouseX Current mouse x-position.
     * @param mouseY Current mouse y-position.
     * @param button The mouse button clicked.
     * @return Whether this handled a mouse click.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        if (this.screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && clicked)
            this.screen.getWidgets().getSearchInput().setFocus(false);

        return clicked;
    }

    /**
     * Utility method that determines if a widget is selectable with the tab key.
     * @param row A configuration row.
     * @param widget A widget within a row.
     * @return Whether the given widget is an invalid tab target.
     */
    private boolean isInvalidWidget(Row row, AbstractWidget widget)
    {
        boolean isGroup = widget instanceof ContainerButton;
        boolean isReset = widget.equals(row.reset);
        boolean isController = widget.equals(row.controller);
        boolean isInactive = (isGroup || isReset || isController) && !widget.isActive();

        return isInactive || (!isGroup && !isReset && !isController);
    }

    /**
     * Key press handling.
     * @param keyCode A key code.
     * @param scanCode A scan code.
     * @param modifiers Modifiers.
     * @return Whether the key press was handled.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (KeyUtil.isEsc(keyCode) && this.unsetFocus())
            return true;

        if (KeyUtil.isTab(keyCode) && this.setFocus(this::isInvalidWidget))
            return true;

        return this.getFocusKeyPress(keyCode, scanCode, modifiers);
    }

    /*

      Config Row Providers

      The following classes and methods are used to generate rows within the config row list. This can be done manually
      or automatically based on the given tweak.

     */

    /**
     * Generate a configuration row based on the given group type and value.
     * @param group The group associated with the key and value (e.g., eye candy or animations).
     * @param key A tweak key that identifies the configuration row.
     * @param value The value that will be controlled by this row.
     * @return A configuration row instance that handles the given value.
     * @param <E> A type that is associated with a provided enumeration value.
     */
    public <E extends Enum<E>> Row getRow(TweakGroup group, String key, Object value)
    {
        if (value instanceof Boolean)
            return new BooleanRow(group, key, (Boolean) value).generate();
        else if (value instanceof Integer)
            return new IntSliderRow(group, key, (Integer) value).generate();
        else if (value instanceof Enum)
            return new EnumRow<E>(group, key, value).generate();
        else if (value instanceof String)
        {
            if (CommonReflect.getAnnotation(group, key, TweakData.Color.class) != null)
                return new ColorRow(group, key, (String) value).generate();

            return new StringRow(group, key, (String) value).generate();
        }
        else
            return new InvalidRow(group, key, value).generate();
    }

    /**
     * Manually add a configuration row.
     * @param row The manually constructed row instance.
     */
    public void addRow(ConfigRowList.Row row) { this.addEntry(row); }

    /**
     * Alternative method for automatically adding rows based on the provided value.
     * @param group The group associated with the key and value (e.g., eye candy or animations).
     * @param key A tweak key that identifies the configuration row.
     * @param value The value that will be controlled by this row.
     */
    public void addRow(TweakGroup group, String key, Object value) { this.addEntry(this.getRow(group, key, value)); }

    /*

      Row Templates

      The following classes are used to generate configuration rows based on specific Java object. Any unhandled types
      will be defaulted to an invalid row instance.

     */

    /**
     * Template class for value-specific rows.
     * @param <T> The type of the controlled value that will be handled.
     */
    public abstract static class AbstractRow<T>
    {
        /* Fields */

        protected final TweakClientCache<T> tweak;
        protected final TweakGroup group;
        protected final String key;
        protected final T value;

        /* Constructor */

        /**
         * Generate a new abstract row.
         * @param group The group type associated with this row.
         * @param key The tweak cache key associated with this row.
         * @param value The tweak value associated with this row.
         */
        protected AbstractRow(TweakGroup group, String key, T value)
        {
            this.tweak = TweakClientCache.get(group, key);
            this.group = group;
            this.key = key;
            this.value = value;
        }

        /**
         * Create a new config row list row instance.
         * @param controller The main widget controller for this row.
         * @return A new config row list row instance.
         */
        protected ConfigRowList.Row create(AbstractWidget controller)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            TweakGui.NoTooltip noTooltip = this.tweak.getMetadata(TweakGui.NoTooltip.class);

            widgets.add(controller);
            widgets.add(new ResetButton(this.tweak, controller));
            widgets.add(new StatusButton(this.tweak, controller));
            widgets.add(new TweakTag(this.tweak, controller, noTooltip == null));

            if (noTooltip == null)
                widgets.add(new TooltipButton(this.tweak, controller));

            if (controller instanceof ColorInput color)
                widgets.add(color.getWidget());

            return new ConfigRowList.Row(widgets, controller, this.tweak);
        }

        /**
         * Create a new config row list row instance based on the provided array list of widgets and the row's tweak
         * cache.
         *
         * @return A new config row list row instance.
         */
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(new ArrayList<>(), this.tweak); }
    }

    /**
     * This row is used when the provided value does not have a specific handler.
     */
    public static class InvalidRow extends AbstractRow<Object>
    {
        /**
         * Creates an empty row within a row list.
         * This is used by automatic generation and has no manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public InvalidRow(TweakGroup group, String key, Object value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(new ArrayList<>(), this.tweak); }
    }

    /**
     * This row type is used when the provided value is a boolean type.
     */
    public static class BooleanRow extends AbstractRow<Boolean>
    {
        /**
         * Create a row within a row list that has a boolean widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public BooleanRow(TweakGroup group, String key, boolean value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate()
        {
            return this.create(new BooleanButton(this.tweak, (button) -> this.tweak.setValue(!this.tweak.getValue())));
        }
    }

    /**
     * This row type is used when the provided value is an integer type.
     */
    public static class IntSliderRow extends AbstractRow<Integer>
    {
        /**
         * Create a row within a row list that has an integer slider widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public IntSliderRow(TweakGroup group, String key, int value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return this.create(new ConfigSlider(this.tweak)); }
    }

    /**
     * This row type is used when the provided value is a generic string type.
     */
    public static class StringRow extends AbstractRow<String>
    {
        /**
         * Create a row within a row list that has a text input box widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public StringRow(TweakGroup group, String key, String value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return this.create(new StringInput(this.tweak).getWidget()); }
    }

    /**
     * This row type is used when the provided string value has hex color metadata.
     */
    public static class ColorRow extends AbstractRow<String>
    {
        /**
         * Create a row within a row list that has a text input box with color specific input logic as a widget
         * controller. This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public ColorRow(TweakGroup group, String key, String value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return this.create(new ColorInput(this.tweak)); }
    }

    /**
     * This row type is used when the provided value can be various values within an enumeration.
     * @param <E> The value type of the enumeration.
     */
    public static class EnumRow<E extends Enum<E>> extends AbstractRow<E>
    {
        /**
         * Create a row within a row list that has an enumeration cycle button as a widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        @SuppressWarnings("unchecked")
        public EnumRow(TweakGroup group, String key, Object value) { super(group, key, (E) value); }

        @Override
        public ConfigRowList.Row generate()
        {
            return this.create(new CycleButton<>(this.tweak, this.tweak.getValue().getDeclaringClass(), (button) -> ((CycleButton<?>) button).toggle()));
        }
    }

    /*

      Manual Custom Row Builders

      The following classes and methods are used to manually construct rows within the config row list. This is useful
      when the page has a predefined layout.

     */

    /**
     * Generate a row that handles a key mapping.
     * @param mapping The key mapping this row will handle.
     */
    public record BindingRow(KeyMapping mapping)
    {
        /**
         * Create a new config row list row instance that has a key binding button as a widget controller.
         * @return A config row list row instance.
         */
        public ConfigRowList.Row generate()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            KeyBindButton controller = new KeyBindButton(this.mapping);

            widgets.add(controller);
            widgets.add(new ResetButton(null, controller));

            return new ConfigRowList.Row(widgets, controller, null);
        }
    }

    /**
     * Generate a row that will have a single centered widget controller.
     * @param controller The widget controller.
     */
    public record SingleCenteredRow(AbstractWidget controller)
    {
        /**
         * Create a new config row list row instance with only the provided widget as the controller.
         * @return A config row list instance with only the provided widget centered.
         */
        public ConfigRowList.Row generate()
        {
            Screen screen = Minecraft.getInstance().screen;
            assert screen != null;

            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller.x = (screen.width / 2) - (this.controller.getWidth() / 2);
            this.controller.y = 0;

            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, null);
        }
    }

    /**
     * Generate a row that will be aligned to the given indent value.
     * @param controller The widget controller.
     * @param indent The indent value that will be aligned from the left.
     */
    public record SingleLeftRow(AbstractWidget controller, int indent)
    {
        /**
         * Create a new config row list row instance with only the provided widget as the controller.
         * @return A config row list instance with only the provided widget aligned to the left.
         */
        public ConfigRowList.Row generate()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller.x = indent;
            this.controller.y = 0;

            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, null);
        }
    }

    /**
     * Manually create a configuration row with a predefined list of widgets.
     * @param widgets The widgets that belong to this row.
     */
    public record ManualRow(List<AbstractWidget> widgets)
    {
        /**
         * Create a new config row list row instance with the widgets provided.
         * @return A config row list instance with widgets that are in order as provided by the list.
         */
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(widgets, null); }
    }

    /*

      Group Container Rows

      The following classes and methods are used to handle categories, subcategories, and embedded subcategories.
      These configuration rows appear as arrows that will expand or collapse subscribed configuration rows.

     */

    /**
     * An enumeration that defines three categorization types.
     */
    public enum ContainerType
    {
        CATEGORY,    // The main parent (subscribes to a main group like Eye Candy)
        SUBCATEGORY, // A child of the parent (subscribes to a category like Combat Gameplay)
        EMBEDDED     // A subcategory of a subcategory (like 'Buttons' to 'Title Screen Candy')
    }

    /**
     * A container row is a type of row that holds subscribed rows. Subscriptions are determined based on metadata
     * defined in the client config.
     *
     * These rows can also be embedded within other container rows. This is done by specific a container type.
     * Containers are expanded in the following order: Category > Subcategory > Embedded
     */
    public static class ContainerRow
    {
        /* Fields */

        private ArrayList<ConfigRowList.Row> cache;
        private ContainerButton controller;

        private final Enum<?> id;
        private final Component title;
        private final Supplier<ArrayList<ConfigRowList.Row>> childrenSupply;
        private final ContainerType containerType;
        private final ConfigRowList list;

        private boolean expanded = false;

        /* Constructors */

        /**
         * Create a new container row with the given container type.
         * @param title A translatable component for the row's display title.
         * @param childrenSupply A supplier that provides an array list of config row list row instances.
         * @param id An enumeration identifier for this container row instance.
         * @param containerType The type of container this row instance is associated with.
         */
        public ContainerRow
        (
            Component title,
            Supplier<ArrayList<ConfigRowList.Row>> childrenSupply,
            Enum<?> id,
            ContainerType containerType
        )
        {
            this.id = id;
            this.title = title;
            this.childrenSupply = childrenSupply;
            this.containerType = containerType;
            this.list = getInstance();
        }

        /**
         * Create a new container row instance with a category container type.
         * @param title A translatable component for the row's display title.
         * @param childrenSupply A supplier that provides an array list of config row list row instances.
         * @param id An enumeration identifier for this container row instance.
         */
        public ContainerRow(Component title, Supplier<ArrayList<ConfigRowList.Row>> childrenSupply, Enum<?> id)
        {
            this(title, childrenSupply, id, ContainerType.CATEGORY);
        }

        /* Methods */

        /**
         * Get an indentation value based on the provided container type.
         * @param containerType The container to get an indent value from.
         * @return An integer that defines where text should start from the left side.
         */
        public static int getIndent(ContainerType containerType)
        {
            return switch (containerType)
            {
                case CATEGORY -> TEXT_START;
                case SUBCATEGORY -> CAT_TEXT_START;
                case EMBEDDED -> SUB_TEXT_START;
            };
        }

        /**
         * @return Check if the current row is expanded.
         */
        public boolean isExpanded() { return this.expanded; }

        /**
         * Create the metadata for this category row.
         * This data defines flags needed for proper tree rendering.
         */
        private void setGroupMetadata()
        {
            // If the parent group contains only subcategories then we don't want pipe bars in the last subcategory
            for (ConfigRowList.Row categories : this.list.children())
            {
                for (AbstractWidget widget : categories.children)
                {
                    // Check if parent group
                    if (widget instanceof ContainerButton group && this.controller.equals(group))
                    {
                        // Ensure children only consist of subcategories
                        ContainerButton subcategory = null;
                        boolean isSubOnly = true;

                        for (ConfigRowList.Row subcategories : this.cache)
                        {
                            for (AbstractWidget subWidget : subcategories.children)
                            {
                                if (subWidget instanceof ContainerButton subGroup)
                                    subcategory = subGroup;
                                else
                                {
                                    isSubOnly = false;
                                    break;
                                }
                            }

                            if (!isSubOnly)
                                break;
                        }

                        // If parent group only has subcategories, then tell the last subgroup to not display pipe bars
                        if (isSubOnly && subcategory != null)
                            subcategory.setLastSubcategory(true);

                        // If category group has tweaks at the end, a category bar is needed for embedded tree rendering
                        if ((!isSubOnly || group.isParentTreeNeeded()) && subcategory != null)
                            subcategory.setParentTreeNeeded(true);

                        // If category group has additional children then show grandparent pipe bars for embedded rows
                        if (group.getId() instanceof TweakEmbed)
                        {
                            if (categories.group != null && !categories.group.isLastSubcategory())
                                group.setGrandparentTreeNeeded(true);
                        }
                    }
                }
            }
        }

        /**
         * @return Determine the index of where this category row resides within the children array of the main config
         * row list.
         */
        private int getHeaderIndex()
        {
            int header = -1;
            for (int i = 0; i < this.list.children().size(); i++)
            {
                if (header != -1)
                    break;

                for (AbstractWidget widget : this.list.children().get(i).children)
                {
                    if (widget instanceof ContainerButton && widget.equals(this.controller))
                    {
                        header = i;
                        break;
                    }
                }
            }

            return header == -1 ? 0 : ++header;
        }

        /**
         * Expand this category row so that its subscribed rows become visible.
         */
        public void expand()
        {
            this.expanded = true;
            this.cache = this.childrenSupply.get();
            this.setGroupMetadata();

            int header = this.getHeaderIndex();
            for (ConfigRowList.Row row : this.cache)
            {
                this.list.children().add(header, row);

                row.setIndent(getIndent(this.containerType) + 20);
                row.setGroup(this.controller);
                header++;
            }

            if (this.cache.size() > 0)
            {
                this.cache.get(0).setFirst(true);
                this.cache.get(this.cache.size() - 1).setLast(true);
            }
        }

        /**
         * Collapse this category row so that its subscribed rows become invisible.
         */
        public void collapse()
        {
            if (this.cache == null)
                return;

            this.expanded = false;

            for (ConfigRowList.Row cache : this.cache)
            {
                for (ConfigRowList.Row child : this.list.children())
                {
                    if (child.equals(cache))
                    {
                        // Collapse any subcategories within the category
                        for (AbstractWidget widget : child.children)
                        {
                            if (widget instanceof ContainerButton group)
                                group.collapse();
                        }

                        this.list.removeEntry(child);
                        break;
                    }
                }
            }

            if (this.list.getScrollAmount() > 0.0D)
                this.list.setScrollAmount(this.list.getScrollAmount());
        }

        /**
         * @return Creates a new row instance so that it can be added to the config row list.
         */
        public ConfigRowList.Row generate()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller = new ContainerButton(this, this.id, this.title, this.containerType);
            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, this.controller, null);
        }
    }

    /**
     * This is the class that defines the children of the configuration row list. These entries will be instances of the
     * row classes defined above.
     *
     * Rendering of all the rows listed above is also defined by this class.
     */
    public static class Row extends ContainerObjectSelectionList.Entry<Row>
    {
        /* Nullable Fields */

        @Nullable private ContainerButton group;
        @Nullable public final TweakClientCache<?> tweak;
        @Nullable public final AbstractWidget controller;
        @Nullable public ResetButton reset = null;

        /* Fields */

        private boolean first = false;
        private boolean last = false;
        private float fadeIn = 0F;
        private int indent = TEXT_START;

        public final List<AbstractWidget> children;

        /* Constructors */

        /**
         * Create a new config row list row instance.
         * @param list A list of widgets that will become children of this row.
         * @param controller The widget that should be considered the controller of this row.
         *                   A controller widget is a widget that changes the state of a tweak or configuration value.
         *                   This can be left as null if this row does not have a controller.
         *
         * @param tweak The tweak that this row is associated with. This can be left as null if this row does not manage
         *              a tweak from the client cache.
         */
        public Row(List<AbstractWidget> list, @Nullable AbstractWidget controller, @Nullable TweakClientCache<?> tweak)
        {
            this.children = list;
            this.controller = controller;
            this.tweak = tweak;

            // Assign reset button to row if applicable
            for (AbstractWidget widget : list)
            {
                if (widget instanceof ResetButton button)
                    this.reset = button;
            }

            if (this.controller instanceof ContainerButton containerButton)
                this.group = containerButton;
            else
                this.group = null;
        }

        /**
         * Create a new config row list row instance that does have a controller widget.
         * @param list A list of widgets that will become children of this row.
         * @param tweak The tweak that this row is associated with. This can be left as null if this row does not manage
         *              a tweak from the client cache.
         */
        public Row(List<AbstractWidget> list, @Nullable TweakClientCache<?> tweak) { this(list, null, tweak); }

        /* Setters & Getters */

        /**
         * @return Whether this row is the first entry within a container.
         */
        public boolean isFirst() { return this.first; }

        /**
         * Set the flag that states if this row is the first entry within a container.
         * @param state A state for the flag.
         */
        public void setFirst(boolean state) { this.first = state; }

        /**
         * @return Whether this row is the last entry within a container.
         */
        public boolean isLast() { return this.last; }

        /**
         * Set the flag that states if this row is the last entry within a container.
         * @param state A state for the flag.
         */
        public void setLast(boolean state) { this.last = state; }

        /**
         * Set an indent value for this row.
         * @param indent A value that is used for text rendering from the left side of the screen.
         */
        public void setIndent(int indent) { this.indent = indent; }

        /**
         * @return Get the indentation value for this row.
         */
        public int getIndent() { return this.indent; }

        /**
         * Set a new group button.
         * @param group A group button instance.
         */
        public void setGroup(@Nullable ContainerButton group) { this.group = group; }

        /* Overrides & Rendering */

        /**
         * @return Check if this row manages a key binding.
         */
        private boolean isBindingRow()
        {
            for (AbstractWidget widget : this.children)
                if (widget instanceof KeyBindButton)
                    return true;

            return false;
        }

        /**
         * This is used for tweaks that is controlled by the server. Since this handled client-side it is important that
         * the server checks if the user attempting to change a tweak is an operator.
         *
         * @return Whether this row should be rendered as locked if the player is not an operator.
         */
        private boolean isRowLocked()
        {
            if (this.tweak == null)
                return false;

            boolean isClient = this.tweak.isClient();
            boolean isDynamic = this.tweak.isDynamic();

            if ((isClient && !isDynamic) || !NostalgicTweaks.isNetworkVerified())
                return false;

            if (this.tweak.isDynamic() && NostalgicTweaks.isNetworkVerified() && !NetUtil.isPlayerOp())
                return true;

            for (AbstractWidget widget : this.children)
            {
                if (widget instanceof PermissionLock && Minecraft.getInstance().player != null)
                    return !NetUtil.isPlayerOp(Minecraft.getInstance().player);
            }

            return false;
        }

        /**
         * Renders a semi-transparent rectangle behind rows with controllers that manage a configuration entry.
         * @param poseStack The current pose stack.
         * @param screen The current screen.
         * @param top Where the top of the rectangle should start rendering.
         * @param height The height of the rectangle.
         */
        private void renderOnHover(PoseStack poseStack, Screen screen, int top, int height)
        {
            boolean isHoverOn = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_ROW_HIGHLIGHT).getValue();
            if (Overlay.isOpened() || !isHoverOn || (this.tweak == null && !this.isBindingRow()))
                return;

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            Matrix4f matrix = poseStack.last().pose();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            float z = 0.0F;
            boolean isFaded = (Boolean) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE).getValue();
            int[] rgba = TextUtil.toHexRGBA((String) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR).getValue());
            int r = rgba[0];
            int g = rgba[1];
            int b = rgba[2];
            int a = rgba[3];
            int alpha = Mth.clamp(isFaded ? (int) (this.fadeIn * a) : a, 0, 255);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(matrix, (float) 0, (float) (top + height), z).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, (float) screen.width, (float) (top + height), z).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, (float) screen.width, (float) (top - 1), z).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, (float) 0, (float) (top - 1), z).color(r, g, b, alpha).endVertex();
            tesselator.end();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        /**
         * Render a tree that connects containers to their subscribed row entries.
         * @param poseStack The current pose stack.
         * @param top A starting y-position for the top of the tree.
         * @param height The ending y-position for the bottom of the tree.
         */
        private void renderTree(PoseStack poseStack, int top, int height)
        {
            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.CATEGORY_TREE_COLOR);
            TweakClientCache<Boolean> tree = TweakClientCache.get(GuiTweak.DISPLAY_CATEGORY_TREE);

            boolean isTreeEnabled = tree.getValue();
            boolean isIndented = this.indent != TEXT_START;

            if (!isIndented || !isTreeEnabled)
                return;

            boolean isSubIndented = this.group == null || !this.group.isLastSubcategory();
            boolean isVertical = this.indent == SUB_TEXT_START || this.indent == EMB_TEXT_START;
            boolean isRowEmpty = this.children.size() == 0;
            boolean isTextRow = false;

            for (AbstractWidget widget : this.children)
            {
                if (widget instanceof TextGroup.TextRow)
                {
                    isTextRow = true;
                    break;
                }
            }

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            Matrix4f matrix = poseStack.last().pose();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int rgba = TextUtil.toHexInt(color.getValue());

            float leftX = this.indent - 16.0F;
            float rightX = leftX + 10.0F;

            float topY = (float) top + (height / 2.0F) - 2.0F;
            float bottomY = topY + 2.0F;

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            /*
               Container Subscriber Connections

               The following fill calls connect this container's subscriptions. If this container is embedded or a
               subcategory, then additional rendering is required to create a full tree.
             */

            // Horizontal Bar ( L Subscriber Title )

            if (!isTextRow && !isRowEmpty)
                RenderUtil.fill(buffer, matrix, leftX + 2.0F, rightX, topY, bottomY, rgba);

            // Vertical Bar ( | )

            rightX = leftX + 2.0F;
            topY = this.isFirst() ? top - 6.0F : top - 1.0F;
            bottomY = this.isLast() ? (float) top + (height / 2.0F) : (float) (top + height) + 3.0F;

            RenderUtil.fill(buffer, matrix, leftX, rightX, topY, bottomY, rgba);

            /*
               Subcategory Connections

               The following fill calls connect this container's parent subscriptions. If this container is embedded
               then additional rendering is required to create a full tree.
             */

            if (isVertical && isSubIndented)
            {
                // Subcategory Vertical Bar ( |  L Subscriber Title )

                leftX = this.indent - 36.0F;
                rightX = leftX + 2.0F;
                bottomY = (float) (top + height) + 3.0F;

                RenderUtil.fill(buffer, matrix, leftX, rightX, topY + (this.isFirst() ? 5.0F : 0.0F), bottomY, rgba);

                if (this.indent == EMB_TEXT_START && this.group.isGrandparentTreeNeeded())
                    RenderUtil.fill(buffer, matrix, leftX - 20.0F, rightX - 20.0F, topY + (this.isFirst() ? 5.0F : 0.0F), bottomY, rgba);
            }

            /*
               Embedded Connections

               The following fill calls connect this container's parent subscriptions. If this container has a
               grandparent, then additional rendering is required to create a full tree.
             */

            if (this.indent == EMB_TEXT_START && this.group.isParentTreeNeeded() && this.group.isLastSubcategory())
            {
                // Embedded Vertical Bar ( |  L Subscriber Title )

                topY += this.isFirst() ? 5.0F : 0.0F;
                leftX = this.indent - 56.0F;
                rightX = leftX + 2.0F;
                bottomY = (float) (top + height) + 3.0F;

                RenderUtil.fill(buffer, matrix, leftX, rightX, topY, bottomY, rgba);
            }
            else if (this.indent == EMB_TEXT_START && this.group.isGrandparentTreeNeeded())
            {
                // Secondary Embedded Vertical Bar ( |  |  L Subscriber Title )

                topY += this.isFirst() ? 5.0F : 0.0F;
                leftX -= 40.0F;
                rightX -= 40.0F;
                bottomY = (float) (top + height) + 3.0F;

                RenderUtil.fill(buffer, matrix, leftX, rightX, topY, bottomY, rgba);
            }

            tesselator.end();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        /**
         * Checks if a widget within a row is exceeding the width of the current screen.
         * @param screen A screen to get width data from.
         * @param widget The widget to check.
         * @return Whether a widget is completely visible.
         */
        private static boolean isRowClipped(Screen screen, AbstractWidget widget)
        {
            return widget.x + widget.getWidth() >= screen.width - TEXT_FROM_END;
        }

        /**
         * The main row renderer.
         * @param poseStack The current pose stack.
         * @param index Unused parameter.
         * @param top The top of this row.
         * @param left The starting x-position of this row.
         * @param width The width of this row.
         * @param height The height of this row.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         * @param isMouseOver A flag that states if the mouse is over this row.
         * @param partialTick A change in frame time.
         */
        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            Font font = Minecraft.getInstance().font;
            ConfigScreen screen = (ConfigScreen) Minecraft.getInstance().screen;

            if (screen == null)
                return;

            // Update renderer tracker
            ConfigRowList.rendering = this;

            // Multiplayer row lockout
            boolean isRowLocked = this.isRowLocked();

            // Row highlights
            boolean isFading = (Boolean) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE).getValue();
            this.fadeIn = Mth.clamp(this.fadeIn, 0F, 1F);

            if (this.isMouseOver(mouseX, mouseY))
                this.fadeIn += isFading ? 0.05F : 1.0F;
            else
                this.fadeIn -= isFading ? 0.05F : 1.0F;
            if (this.fadeIn > 0F)
                this.renderOnHover(poseStack, screen, top, height);

            // Tree indent highlights
            this.renderTree(poseStack, top, height);

            // Update indentation and get focus colors on widgets
            boolean isFocused = false;
            int startX = this.indent;

            for (AbstractWidget widget : this.children)
            {
                if (widget.x == TEXT_START && this.indent != TEXT_START)
                    widget.x = this.indent;

                if (widget.isFocused())
                    isFocused = true;
            }

            // Ensure reset buttons don't overlap scrollbar (different languages will change the width of this button)
            int diffX = 0;
            for (AbstractWidget widget : this.children)
            {
                if (widget instanceof ResetButton && isRowClipped(screen, widget))
                {
                    int prevX = widget.x;
                    while (isRowClipped(screen, widget))
                        widget.x--;

                    diffX = prevX - widget.x;
                    break;
                }
            }

            for (AbstractWidget widget : this.children)
                widget.x -= diffX;

            // Ensure translation does not overlap controllers
            TweakTag tagger = null;

            if (this.controller != null)
            {
                for (AbstractWidget widget : this.children)
                {
                    if (widget instanceof TweakTag tag)
                    {
                        boolean isUnchecked = tag.x == 0 || tag.getWidth() == 0;
                        boolean isOverlap = tag.x + tag.getWidth() >= this.controller.x - 6;
                        tagger = tag;

                        if (isUnchecked || isOverlap)
                        {
                            tag.setRender(false);
                            tag.render(poseStack, mouseX, mouseY, partialTick);

                            while (tag.x + tag.getWidth() >= this.controller.x - 6)
                            {
                                tag.setTitle(TextUtil.ellipsis(tag.getTitle()));
                                tag.render(poseStack, mouseX, mouseY, partialTick);

                                if (tag.getTitle() == null || tag.getTitle().length() < 3)
                                    break;
                            }

                            tag.setRender(true);
                            break;
                        }
                    }
                }
            }

            // Update tooltip bubble if text has ellipsis
            for (AbstractWidget widget : this.children)
            {
                if (widget instanceof TooltipButton button && tagger != null)
                {
                    button.setTitle(tagger.getTitle());
                    break;
                }
            }

            // Render rows
            for (AbstractWidget widget : this.children)
            {
                // Update widget focus if a change was sent via the category list overlay
                if (CategoryList.OVERLAY.getSelected() == this)
                {
                    ConfigRowList list = screen.getWidgets().getConfigRowList();

                    if (list.setSelection)
                    {
                        if (list.getLastSelection() != null)
                            ((AbstractWidgetAccessor) list.getLastSelection()).NT$setFocus(false);

                        list.setSelection = false;
                        list.setLastSelection(widget);
                        ((AbstractWidgetAccessor) widget).NT$setFocus(true);
                    }
                }

                // Apply row title color formatting
                Component title = Component.empty();

                if (this.tweak != null)
                {
                    Component translation = Component.translatable(tagger == null ? this.tweak.getLangKey() : tagger.getTitle());
                    title = this.tweak.isSavable() ? translation.copy().withStyle(ChatFormatting.ITALIC) : translation.copy().withStyle(ChatFormatting.RESET);

                    if (isFocused)
                        title = title.copy().withStyle(ChatFormatting.GOLD);
                }
                else if (widget instanceof KeyBindButton)
                {
                    Component translation = Component.translatable(((KeyBindButton) widget).getMapping().getName());
                    title = KeyUtil.isMappingConflict(((KeyBindButton) widget).getMapping()) ? translation.copy().withStyle(ChatFormatting.RED) : translation.copy().withStyle(ChatFormatting.RESET);
                }
                else if (widget instanceof ContainerButton containerButton)
                    containerButton.setHighlight(CategoryList.OVERLAY.getSelected() == this);

                // Render row title
                boolean isSearching = screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH;
                int dy = isSearching ? 11 : 0;
                int startY = top + 6 + dy;

                boolean isHovered = isSearching && MathUtil.isWithinBox(mouseX, mouseY, startX, startY, font.width(title), font.lineHeight);
                title = isHovered ? title.copy().withStyle(ChatFormatting.UNDERLINE) : title;

                Screen.drawString(poseStack, font, title, startX, startY, 0xFFFFFF);

                if (this.tweak != null && isHovered)
                    ConfigRowList.overTweakId = this.tweak.getId();
                else if (this.tweak != null && ConfigRowList.overTweakId != null && ConfigRowList.overTweakId.equals(this.tweak.getId()))
                    ConfigRowList.overTweakId = null;

                // Realign widgets
                widget.y = top;
                int cacheX = widget.x;
                int cacheY = widget.y;

                if (widget instanceof EditBox)
                {
                    widget.x -= 1;
                    widget.y += 1;
                }

                // Realign text rows if searching
                if (screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH)
                {
                    if (widget instanceof SearchCrumbs crumb)
                    {
                        widget.x = crumb.startX;
                        widget.y += 2;
                    }
                    else
                        widget.y += 11;

                    cacheY = widget.y + (widget instanceof EditBox ? -1 : 0);
                }

                // Render final widget
                widget.active = !Overlay.isOpened();

                // Apply row locking for multiplayer
                if (widget instanceof PermissionLock && Minecraft.getInstance().player != null)
                    widget.active = !isRowLocked;
                else if (isRowLocked && widget instanceof ResetButton)
                    widget.active = false;

                widget.render(poseStack, mouseX, mouseY, partialTick);

                // Reset widget positions with caches
                if (widget instanceof EditBox)
                {
                    widget.x = cacheX;
                    widget.y = cacheY;
                }

                // If ellipsis, then give tooltip of full tweak name
                boolean isEllipsis = tagger != null && tagger.getTitle().contains("...");
                boolean isOverText = (mouseX >= startX && mouseX <= startX + font.width(title)) && (mouseY >= top + 6 && mouseY <= top + 6 + 8);

                if (isEllipsis && isOverText && this.tweak != null)
                {
                    screen.renderLast.add(() ->
                        screen.renderComponentTooltip(poseStack, TextUtil.Wrap.tooltip(Component.translatable(this.tweak.getTranslation()), 35), mouseX, mouseY))
                    ;
                }

                // Debugging
                if (NostalgicTweaks.isDebugging() && this.isMouseOver(mouseX, mouseY) && this.tweak != null)
                {
                    if (screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH)
                    {
                        String color = "§a";
                        int weight = this.tweak.getWeight();
                        if (weight <= 50) color = "§4";
                        else if (weight <= 60) color = "§c";
                        else if (weight <= 70) color = "§6";
                        else if (weight <= 80) color = "§e";
                        else if (weight <= 99) color = "§2";

                        screen.renderTooltip(poseStack, Component.literal(String.format("Fuzzy Weight: %s%s", color, weight)), mouseX, mouseY);
                    }
                    else
                    {
                        List<Component> lines = new ArrayList<>();
                        Object clientCache = this.tweak.getSavedValue();
                        String clientColor = clientCache instanceof Boolean state ? state ? "§2" : "§4" : "";
                        lines.add(Component.literal(String.format("Client Cache: %s%s", clientColor, clientCache)));

                        TweakServerCache<?> serverCache = TweakServerCache.all().get(this.tweak.getId());
                        if (serverCache != null)
                        {
                            String serverColor = serverCache.getServerCache() instanceof Boolean state ? state ? "§2" : "§4" : "";
                            lines.add(Component.literal(String.format("Server Cache: %s%s", serverColor, serverCache.getServerCache())));
                        }

                        screen.renderComponentTooltip(poseStack, lines, mouseX, mouseY);
                    }
                }
            }

            // Clear rendering tracker
            ConfigRowList.rendering = null;
        }

        /* Required Overrides */

        @Override public List<? extends GuiEventListener> children() { return this.children; }
        @Override public List<? extends NarratableEntry> narratables() { return this.children; }
    }
}
