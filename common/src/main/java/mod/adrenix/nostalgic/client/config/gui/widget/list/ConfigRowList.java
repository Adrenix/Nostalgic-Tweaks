package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryListOverlay;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.gui.widget.SearchCrumbs;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.client.config.gui.widget.button.*;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowEntry;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowTweak;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.list.ListMap;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.mixin.widen.AbstractWidgetAccessor;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static ConfigRowList.Row rendering = null;

    // Holds the current tweak that the mouse is over in the search tab.
    public static String overTweakId = null;

    // Holds a tweak identification string for tweak search jumping.
    public static String jumpToTweakId = null;

    // Holds a group identification string for crumb search jumping.
    public static Object jumpToContainerId = null;

    // Holds the current indentation for expanding containers
    public static int currentIndent = TEXT_START;

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
     * Set the current tab focus on a specific widget. Nothing will happen if the widget is null.
     * @param widget The widget to focus on.
     */
    public void setFocusOn(AbstractWidget widget)
    {
        if (widget == null)
            return;

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
        boolean isClicked = super.mouseClicked(mouseX, mouseY, button);

        if (this.screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && isClicked)
            this.screen.getWidgets().getSearchInput().setFocused(false);

        if (this.screen instanceof ListScreen listScreen && isClicked)
            listScreen.getSearchBox().setFocused(false);

        return isClicked;
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
        boolean isDelete = widget.equals(row.delete);
        boolean isRemove = widget.equals(row.remove);
        boolean isController = widget.equals(row.controller);
        boolean isInactive = (isGroup || isReset || isDelete || isRemove || isController) && !widget.isActive();

        return isInactive || (!isGroup && !isReset && !isDelete && !isRemove && !isController);
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

    /**
     * Remove a row from the row list.
     * @param row A config row list row instance.
     */
    public void removeRow(Row row) { this.removeEntry(row); }

    /*

      Config Row Providers

      The following classes and methods are used to generate rows within the config row list. This can be done manually
      or automatically based on the given tweak.

     */

    /**
     * Generate a configuration row based on the given group type and value.
     * Unchecked type casting is fine here since the config validator ensures proper values are loaded into memory.
     *
     * @param group The group associated with the key and value (e.g., eye candy or animations).
     * @param key A tweak key that identifies the configuration row.
     * @param value The value that will be controlled by this row.
     * @param <E> A type that is associated with a provided enumeration value.
     * @return A configuration row instance that handles the given value.
     */
    @CheckReturnValue
    public <E extends Enum<E>> Row rowFromTweak(TweakGroup group, String key, Object value)
    {
        if (TweakClientCache.get(group, key).isNotAutomated())
            return null;

        if (value instanceof Boolean)
        {
            return new ConfigRowTweak.BooleanRow(group, key, (Boolean) value).generate();
        }
        else if (value instanceof Integer)
        {
            return new ConfigRowTweak.IntSliderRow(group, key, (Integer) value).generate();
        }
        else if (value instanceof Enum)
        {
            return new ConfigRowTweak.EnumRow<E>(group, key, value).generate();
        }
        else if (value instanceof String)
        {
            if (CommonReflect.getAnnotation(group, key, TweakData.Color.class) != null)
                return new ConfigRowTweak.ColorRow(group, key, (String) value).generate();

            return new ConfigRowTweak.StringRow(group, key, (String) value).generate();
        }
        else if (value instanceof Set)
        {
            //noinspection unchecked
            return new ConfigRowTweak.ListSetRow(group, key, (Set<String>) value).generate();
        }
        else if (value instanceof Map<?, ?>)
        {
            //noinspection unchecked
            return new ConfigRowTweak.ListMapRow<>(group, key, (Map<String, ?>) value).generate();
        }
        else
        {
            return new ConfigRowTweak.InvalidRow(group, key, value).generate();
        }
    }

    /**
     * Overload method for adding a config row list row from a tweak.
     * @param tweak The tweak to get metadata from.
     * @return A configuration row instance that handles the given tweak.
     */
    @CheckReturnValue
    public Row rowFromTweak(TweakClientCache<?> tweak)
    {
        return this.rowFromTweak(tweak.getGroup(), tweak.getKey(), tweak.getValue());
    }

    /**
     * Generate a configuration row based on the given list entry.
     * @param entry The map entry that will be associated with the new row.
     * @param <V> The value type of the map entry.
     * @return A configuration row instance that handles the given value.
     */
    @SuppressWarnings("unchecked") // All entries will have string keys from the JSON file, only the value is typed checked
    public <V> Row rowFromEntry(ListMap<V> map, Map.Entry<String, V> entry, V reset)
    {
        if (entry.getValue() instanceof Integer)
        {
            return new ConfigRowEntry.IntegerEntryRow
            (
                (ListMap<Integer>) map,
                (Map.Entry<String, Integer>) entry,
                (Integer) reset
            ).generate();
        }
        else
        {
            return new ConfigRowEntry.InvalidEntryRow
            (
                (ListMap<Object>) map,
                (Map.Entry<String, Object>) entry,
                reset
            ).generate();
        }
    }

    /**
     * Manually add a configuration row.
     * @param row The manually constructed row instance.
     */
    public void addRow(ConfigRowList.Row row) { this.addEntry(row); }

    /**
     * If a tweak is annotated as not automated, then the row returned from the generator will be null.
     * This method catches that scenario and ignores adding a new row entry when that occurs.
     *
     * @param group The group associated with the key and value (e.g., eye candy or animations).
     * @param key A tweak key that identifies the configuration row.
     * @param value The value that will be controlled by this row.
     */
    public void addRow(TweakGroup group, String key, Object value)
    {
        Row row = this.rowFromTweak(group, key, value);

        if (row != null)
            this.addEntry(row);
    }

    /**
     * This is the class that defines the children of the configuration row list. These entries will be instances of the
     * row classes defined above.
     *
     * Rendering of all the rows listed above is also defined by this class.
     */
    public static class Row extends AbstractEntry<Row>
    {
        /* Nullable Fields */

        private ContainerButton group;
        public final TweakClientCache<?> tweak;
        public final AbstractWidget controller;
        public ResetButton reset = null;
        public DeleteButton delete = null;
        public RemoveButton remove = null;

        /* Fields */

        private String resourceKey = "";
        private boolean highlight = false;
        private boolean first = false;
        private boolean last = false;
        private float fade = 0F;
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
                else if (widget instanceof RemoveButton button)
                    this.remove = button;
                else if (widget instanceof DeleteButton button)
                    this.delete = button;
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
         * Change whether this row should have highlighting. This will override automatic highlighting logic if enabled.
         * @param state The state of row highlighting.
         */
        public void setHighlight(boolean state) { this.highlight = state; }

        /**
         * Change the item resource key associated with this row. This key is used by abstract list screens. When the
         * edit button is pressed in a manager overlay window, this tracker will be needed to determine which row has
         * the intended item to edit.
         *
         * @param key An item stack's resource location identifier.
         */
        public void setResourceKey(String key) { this.resourceKey = key; }

        /**
         * Get the item resource key associated with this row. If none is attached, then an empty string is returned.
         * @return The item resource key associated with this row, an empty string otherwise.
         */
        public String getResourceKey() { return this.resourceKey; }

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

        /**
         * @return Get the container button for this row.
         */
        @CheckReturnValue
        public ContainerButton getGroup() { return this.group; }

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
         * @param graphics The current GuiGraphics object.
         * @param screen The current screen.
         * @param top Where the top of the rectangle should start rendering.
         * @param height The height of the rectangle.
         */
        private void renderOnHover(GuiGraphics graphics, Screen screen, int top, int height)
        {
            boolean isHoverOn = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_ROW_HIGHLIGHT).getValue();
            boolean isHoverOff = this.tweak == null && !this.isBindingRow();

            if (Overlay.isOpened())
                return;

            if (!this.highlight && (!isHoverOn || isHoverOff))
                return;

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            Matrix4f matrix = graphics.pose().last().pose();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            float z = 0.0F;
            boolean isFaded = (Boolean) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE).getValue();
            int[] rgba = ColorUtil.toHexRGBA((String) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR).getValue());
            int r = rgba[0];
            int g = rgba[1];
            int b = rgba[2];
            int a = rgba[3];
            int alpha = Mth.clamp(isFaded ? (int) (this.fade * a) : a, 0, 255);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(matrix, (float) 0, (float) (top + height), z).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, (float) screen.width, (float) (top + height), z).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, (float) screen.width, (float) (top - 1), z).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, (float) 0, (float) (top - 1), z).color(r, g, b, alpha).endVertex();
            tesselator.end();

            RenderSystem.disableBlend();
        }

        /**
         * Render a tree that connects containers to their subscribed row entries.
         * @param graphics The current GuiGraphics.
         * @param top A starting y-position for the top of the tree.
         * @param height The ending y-position for the bottom of the tree.
         */
        private void renderTree(GuiGraphics graphics, int top, int height)
        {
            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.CATEGORY_TREE_COLOR);
            TweakClientCache<Boolean> tree = TweakClientCache.get(GuiTweak.DISPLAY_CATEGORY_TREE);

            boolean isListScreen = Minecraft.getInstance().screen instanceof ListScreen;
            boolean isTreeEnabled = tree.getValue();
            boolean isIndented = this.indent != TEXT_START;

            if (!isIndented || !isTreeEnabled || isListScreen)
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
            Matrix4f matrix = graphics.pose().last().pose();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int rgba = ColorUtil.toHexInt(color.getValue());

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
            return widget.getX() + widget.getWidth() >= screen.width - TEXT_FROM_END;
        }

        /**
         * The main row renderer.
         * @param graphics The current GuiGraphics object.
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
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            Font font = Minecraft.getInstance().font;
            ConfigScreen screen = (ConfigScreen) Minecraft.getInstance().screen;

            if (screen == null)
                return;

            // Update renderer tracker
            ConfigRowList.rendering = this;

            // Row highlights
            boolean isFaded = (Boolean) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE).getValue();

            if (this.isMouseOver(mouseX, mouseY) && ConfigWidgets.isInsideRowList(mouseY))
                this.fade = isFaded ? MathUtil.moveClampTowards(this.fade, 1.0F, 0.05F, 0.0F, 1.0F) : 1.0F;
            else
                this.fade = isFaded ? MathUtil.moveClampTowards(this.fade, 0.0F, 0.05F, 0.0F, 1.0F) : 0.0F;

            if (this.fade > 0.0F)
                this.renderOnHover(graphics, screen, top, height);

            // Abstract list screen rendering
            if (Minecraft.getInstance().screen instanceof ListScreen)
            {
                for (AbstractWidget widget : this.children)
                {
                    if (Overlay.isOpened())
                        widget.active = false;

                    widget.setY(top);
                    widget.render(graphics, mouseX, mouseY, partialTick);
                }

                // Clear rendering tracker
                ConfigRowList.rendering = null;

                return;
            }

            // Multiplayer row lockout
            boolean isRowLocked = this.isRowLocked();

            // Tree indent highlights
            this.renderTree(graphics, top, height);

            // Update indentation and get focus colors on widgets
            boolean isFocused = false;
            int startX = this.indent;

            for (AbstractWidget widget : this.children)
            {
                if (widget.getX() == TEXT_START && this.indent != TEXT_START)
                    widget.setX(this.indent);

                if (widget.isFocused())
                    isFocused = true;
            }

            // Ensure reset buttons don't overlap scrollbar (different languages will change the width of this button)
            int diffX = 0;

            for (AbstractWidget widget : this.children)
            {
                if (widget instanceof ResetButton && isRowClipped(screen, widget))
                {
                    int prevX = widget.getX();

                    while (isRowClipped(screen, widget))
                        widget.setX(widget.getX() - 1);

                    diffX = prevX - widget.getX();

                    break;
                }
            }

            for (AbstractWidget widget : this.children)
                widget.setX(widget.getX() - diffX);

            // Ensure translation does not overlap controllers
            TweakTag tagger = null;

            if (this.controller != null)
            {
                for (AbstractWidget widget : this.children)
                {
                    if (widget instanceof TweakTag tag)
                    {
                        tagger = tag;

                        if (tag.isWidthChanged())
                            tag.resetTag();

                        // First render pass is invisible and calculates the width of all tweak tags
                        tag.setRender(false);
                        tag.render(graphics, mouseX, mouseY, partialTick);

                        // Second render pass continually shrinks the tweak translation title until it fits the row
                        while (tag.getX() + tag.getWidth() >= this.controller.getX() - 6)
                        {
                            tag.setTitle(TextUtil.ellipsis(tag.getTitle()));
                            tag.render(graphics, mouseX, mouseY, partialTick);

                            if (tag.getTitle() == null || tag.getTitle().length() < 3)
                                break;
                        }

                        // Third pass is visible and renders the final tag result
                        tag.setRender(true);
                        tag.render(graphics, mouseX, mouseY, partialTick);

                        break;
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
                if (Overlay.getVisible() instanceof CategoryListOverlay overlay && overlay.getSelected() == this)
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
                else if (widget instanceof ContainerButton containerButton && Overlay.getVisible() instanceof CategoryListOverlay overlay)
                    containerButton.setHighlight(overlay.getSelected() == this);

                // Render row title
                boolean isSearching = screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH;
                int dy = isSearching ? 11 : 0;
                int startY = top + 6 + dy;

                boolean isHovered = isSearching && MathUtil.isWithinBox(mouseX, mouseY, startX, startY, font.width(title), font.lineHeight);
                title = isHovered ? title.copy().withStyle(ChatFormatting.UNDERLINE) : title;

                graphics.drawString(font, title, startX, startY, 0xFFFFFF);

                if (this.tweak != null && isHovered)
                    ConfigRowList.overTweakId = this.tweak.getId();
                else if (this.tweak != null && ConfigRowList.overTweakId != null && ConfigRowList.overTweakId.equals(this.tweak.getId()))
                    ConfigRowList.overTweakId = null;

                // Realign widgets
                widget.setY(top);
                int cacheX = widget.getX();
                int cacheY = widget.getY();

                if (widget instanceof EditBox)
                {
                    widget.setX(widget.getX() - 1);
                    widget.setY(widget.getY() + 1);
                }

                // Realign text rows if searching
                if (screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH)
                {
                    if (widget instanceof SearchCrumbs crumb)
                    {
                        widget.setX(crumb.startX);
                        widget.setY(widget.getY() + 2);
                    }
                    else
                        widget.setY(widget.getY() + 11);

                    cacheY = widget.getY() + (widget instanceof EditBox ? -1 : 0);
                }

                // Activate and render widget
                widget.active = !Overlay.isOpened();

                // Apply row locking for multiplayer
                if (widget instanceof PermissionLock && isRowLocked)
                    widget.active = false;

                widget.render(graphics, mouseX, mouseY, partialTick);

                // Reset widget positions with caches
                if (widget instanceof EditBox)
                {
                    widget.setX(cacheX);
                    widget.setY(cacheY);
                }

                // If ellipsis, then give tooltip of full tweak name
                boolean isEllipsis = tagger != null && tagger.getTitle().contains("...");
                boolean isOverText = (mouseX >= startX && mouseX <= startX + font.width(title)) && (mouseY >= top + 6 && mouseY <= top + 6 + 8);

                if (isEllipsis && isOverText && this.tweak != null)
                {
                    screen.renderLast.add(() ->
                        graphics.renderComponentTooltip(font, TextUtil.Wrap.tooltip(this.tweak.getComponentTranslation(), 35), mouseX, mouseY))
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

                        graphics.renderTooltip(font, Component.literal(String.format("Fuzzy Weight: %s%s", color, weight)), mouseX, mouseY);
                    }
                    else
                    {
                        List<Component> lines = new ArrayList<>();
                        Object clientCache = this.tweak.getSavedValue();
                        String clientColor = clientCache instanceof Boolean state ? state ? "§2" : "§4" : "";
                        TweakServerCache<?> serverCache = this.tweak.getServerTweak();

                        lines.add(Component.literal(String.format("Client Cache: %s%s", clientColor, clientCache)));

                        if (serverCache != null)
                        {
                            String serverColor = serverCache.getServerCache() instanceof Boolean state ? state ? "§2" : "§4" : "";
                            lines.add(Component.literal(String.format("Server Cache: %s%s", serverColor, serverCache.getServerCache())));
                        }

                        if (this.tweak.isConflict())
                            lines.add(Component.literal("Mod Conflict: §2true"));

                        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
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
