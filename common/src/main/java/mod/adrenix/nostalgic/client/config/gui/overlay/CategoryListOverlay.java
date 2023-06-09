package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.GenericOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.AbstractEntry;
import mod.adrenix.nostalgic.client.config.gui.widget.list.AbstractRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.mixin.widen.AbstractWidgetAccessor;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * This class display a list of all categories, subcategories, and tweaks.
 * Assists the configuration screen so users can quickly jump to a specific row.
 */

public class CategoryListOverlay extends GenericOverlay
{
    /* Static Fields */

    public static final int DEFAULT_WIDTH = 256;
    public static final int DEFAULT_HEIGHT = 220;

    /* Overlay Fields */

    private TextRowList list;
    private ConfigRowList configRowList;
    private ConfigRowList.Row selected = null;

    /* Constructor & Initialize */

    /**
     * Start a new category list overlay window instance.
     */
    public CategoryListOverlay()
    {
        super(Component.translatable(LangUtil.Gui.OVERLAY_LIST), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        this.setBackground(0xEF000000);
        this.init();
    }

    /**
     * Sets up overlay fields based on current game window properties.
     */
    @Override
    public void init()
    {
        super.init();

        this.configRowList = ConfigRowList.getInstance();
        this.list = null;

        this.generateWidgets();
    }

    /* Rendering Utility */

    private int getListEndY() { return (int) this.y + this.getOverlayHeight() + H_TOP_LEFT_CORNER - 1; }
    private int getListWidth() { return (int) this.x + DEFAULT_WIDTH - 9; }

    /**
     * Refreshes the internal overlay row list.
     */
    private void refreshRowList()
    {
        double scrolled = this.list.getScrollAmount();

        this.list.children().clear();
        this.generateWidgets();
        this.list.setScrollAmount(scrolled);
    }

    // Star Char
    private static final String TWEAK_STAR = "*";

    /**
     * Text Row Button Widget
     *
     * This class turns text into clickable buttons.
     * These buttons can be stars, container names, or tweak names.
     */

    private static class TextButton extends Button
    {
        /* Widget Constructor Helpers */

        private static int getTextWidth(Component title) { return Minecraft.getInstance().font.width(title); }
        private static int getTextHeight() { return Minecraft.getInstance().font.lineHeight; }

        /* Fields */

        private final ConfigRowList.Row row;
        private final ConfigScreen screen;
        private final Component title;
        private final int color;

        /* Constructor */

        public TextButton(ConfigScreen screen, ConfigRowList.Row row, int color, int startX, int startY, Component title, Button.OnPress onClick)
        {
            super(startX, startY, getTextWidth(title), getTextHeight(), title, onClick, DEFAULT_NARRATION);

            this.screen = screen;
            this.title = title;
            this.color = color;
            this.row = row;

            if (this.title.getString().equals(TWEAK_STAR))
                this.active = false;
        }

        /* Widget Overrides */

        /**
         * Handler method for when the mouse is clicked.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         */
        @Override
        public void onClick(double mouseX, double mouseY)
        {
            if (Overlay.isOverTitle(mouseX, mouseY))
                return;

            super.onClick(mouseX, mouseY);
        }

        /**
         * Rendering instructions for the text button widget.
         * @param graphics The current GuiGraphics object.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         * @param partialTick The change in frame time.
         */
        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            CategoryListOverlay overlay = (CategoryListOverlay) Overlay.getVisible();

            if (ClassUtil.isNotInstanceOf(overlay, CategoryListOverlay.class))
                return;

            boolean isTweak = this.title.getString().equals(TWEAK_STAR);
            boolean isControl = this.title.getString().length() == 1;
            boolean isSelected = this.row.equals(overlay.getSelected()) && isControl;
            boolean isTabbed = this.isFocused() && !isTweak;
            boolean isHover = MathUtil.isWithinBox(mouseX, mouseY, this.getX(), this.getY(), this.width, this.height) && !isTweak;
            int highlight = isHover ? 0xFFD800 : this.color;

            if (isTabbed)
                highlight = 0x3AC0FF;

            if (isSelected && overlay.list.getLastSelection() == null)
                overlay.list.setLastSelection(this);

            graphics.drawString(this.screen.getFont(), isSelected ? this.title.copy().withStyle(ChatFormatting.GOLD) : this.title, this.getX(), this.getY(), highlight);
        }
    }

    /**
     * Text Row List
     *
     * This class defines a list of rows that contain text button widgets.
     * Only text button widgets are used in this list.
     */

    private static class TextRowList extends AbstractRowList<TextRow>
    {
        /* Fields */

        public static int color = 0xFFFFFF;
        public final ConfigScreen screen;

        /* Constructor */

        public TextRowList(ConfigScreen screen, int width, int height, int startY, int endY, int rowHeight)
        {
            super(width, height, startY, endY, rowHeight);

            this.screen = screen;
            this.setAsSemiTransparent();
        }

        /* Methods */

        /**
         * Add a row to the text row list.
         * @param row A row instance to add.
         */
        public void addRow(TextRow row) { this.addEntry(row); }

        /**
         * This record defines the type of row being created.
         * These rows will either be text buttons for containers (container buttons) or individual tweaks.
         *
         * @param list A text row list instance.
         * @param row A config row list row instance.
         * @param container Whether there is a container button associated with this entry.
         * @param title The title to display when this entry is rendered.
         * @param indent How far in from the left of the overlay window to start rendering text.
         * @param onClick A handler method for when this entry is clicked.
         */
        public record EntryRow(TextRowList list, ConfigRowList.Row row, @Nullable ContainerButton container, Component title, int indent, Button.OnPress onClick)
        {
            /**
             * When an entry's definition is finished, invoke this to create a text row instance.
             * @return A new text row instance that can be added to a text row list instance.
             */
            public TextRow add()
            {
                List<AbstractWidget> widgets = new ArrayList<>();
                Component control = container == null ? Component.literal(TWEAK_STAR) : Component.literal(container.isExpanded() ? "-" : "+");

                widgets.add(new TextButton(this.list().screen, row, color, this.list.x0 + 2 + indent, 0, control, this::toggle));
                widgets.add(new TextButton(this.list().screen, row, color, this.list.x0 + 11 + indent, 0, this.title, onClick));
                color = color == 0xFFFFFF ? 0xB2B2B2 : 0xFFFFFF;

                return new TextRow(ImmutableList.copyOf(widgets));
            }

            /**
             * A handler method for text row entries that toggles container rows.
             * @param button A text button widget.
             */
            private void toggle(Button button)
            {
                if (this.container != null && Overlay.getVisible() instanceof CategoryListOverlay overlay)
                {
                    double scrolled = overlay.list.getScrollAmount();
                    int position = 0;

                    this.container.silentPress();
                    button.setMessage(Component.literal(this.container.isExpanded() ? "-" : "+"));

                    for (int i = 0; i < overlay.list.children().size() - 1; i++)
                    {
                        for (AbstractWidget widget : overlay.list.children().get(i).children)
                        {
                            if (widget.isFocused())
                            {
                                position = i;
                                break;
                            }
                        }

                        if (position != 0)
                            break;
                    }

                    overlay.list.children().clear();
                    overlay.generateWidgets();

                    if (scrolled > 0.0D)
                        overlay.list.setScrollAmount(scrolled);

                    if (position < overlay.list.children().size())
                    {
                        AbstractWidget widget = overlay.list.children().get(position).children.get(0);
                        ((AbstractWidgetAccessor) widget).NT$setFocus(true);
                        overlay.list.setLastSelection(widget);
                    }
                }
            }
        }
    }

    /**
     * Text Row
     *
     * This class defines the rows that will be included in a text row list instance.
     * Only text button widgets are used in rows.
     */
    private static class TextRow extends AbstractEntry<TextRow>
    {
        /* Fields */

        public final List<AbstractWidget> children;

        /* Constructor */

        public TextRow(List<AbstractWidget> children) { this.children = children; }

        /* List Overrides */

        /**
         * Rendering instructions for an individual text row.
         * @param graphics The current GuiGraphics object.
         * @param index Unused parameter.
         * @param top The top of this row.
         * @param left The left side (starting x-position) of this row.
         * @param width The width of this row.
         * @param height The height of this row.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         * @param isMouseOver A flag that states whether the mouse is over this row.
         * @param partialTick A change in time between frames.
         */
        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            // Draw row background
            if (this.isMouseOver(mouseX, mouseY))
            {
                RenderSystem.depthFunc(515);
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();
                Matrix4f matrix = graphics.pose().last().pose();

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                RenderUtil.fill(buffer, matrix, left - 6, left + DEFAULT_WIDTH - 13, top - 1, top + height + 2, 0x32FFFFFF);
                tesselator.end();
            }

            // Update widget heights and render
            for (AbstractWidget widget : this.children)
            {
                widget.setY(top);
                widget.render(graphics, mouseX, mouseY, partialTick);
            }

            RenderSystem.disableBlend();
        }

        /* Required Overrides */

        @Override
        public List<? extends GuiEventListener> children() { return this.children; }

        @Override
        public List<? extends NarratableEntry> narratables() { return this.children; }
    }

    /* Overlay Overrides */

    /**
     * There is no setter method for this field. This is handled by widget handlers automatically.
     * @return A selected config row, null otherwise.
     */
    @CheckReturnValue
    public ConfigRowList.Row getSelected() { return this.selected; }

    /**
     * This method defines instructions that create widgets for this overlay window.
     * A text row list and its corresponding rows are created here.
     */
    @Override
    public void generateWidgets()
    {
        ConfigScreen screen = (ConfigScreen) Minecraft.getInstance().screen;

        if (screen == null || (this.list != null && !this.list.children().isEmpty()))
            return;

        if (this.list != null)
            this.list.resetLastSelection();

        int width = this.getListWidth();
        int height = this.getOverlayHeight();
        int startY = this.getOverlayStartY();
        int endY = this.getListEndY();

        TextRowList.color = 0xFFFFFF;

        this.list = new TextRowList(screen, width, height, startY, endY, screen.getFont().lineHeight + 2);
        this.list.setLeftPos(this.getOverlayStartX());

        for (ConfigRowList.Row row : this.configRowList.children())
        {
            int indent = 0;

            if (row.getIndent() == ConfigRowList.CAT_TEXT_START)
                indent = 9;
            else if (row.getIndent() == ConfigRowList.SUB_TEXT_START)
                indent = 18;
            else if (row.getIndent() == ConfigRowList.EMB_TEXT_START)
                indent = 27;

            Button.OnPress jump = button ->
            {
                this.configRowList.setScrollOn(row);
                this.configRowList.setSelection = true;
                this.selected = row;

                this.refreshRowList();
            };

            if (row.controller instanceof ContainerButton container)
                this.list.addRow(new TextRowList.EntryRow(this.list, row, container, container.getTitle(), indent, jump).add());

            if (row.tweak != null)
                this.list.addRow(new TextRowList.EntryRow(this.list, row, null, Component.translatable(row.tweak.getLangKey()), indent, jump).add());
        }
    }

    /**
     * A filter method that checks if a text button widget is just a prefix star.
     * @param row A text row instance.
     * @param widget A widget instance.
     * @return Whether the provided widget is invalid for selection.
     */
    private boolean isInvalidWidget(TextRow row, AbstractWidget widget)
    {
        return widget instanceof TextButton text && text.title.getString().equals(TWEAK_STAR);
    }

    /**
     * A handler method for when a key is pressed while an overlay window is open.
     * @param keyCode The pressed key code.
     * @param scanCode A scan code.
     * @param modifiers Any key modifiers.
     * @return Whether this method handled the key press event.
     */
    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (KeyUtil.isEsc(keyCode) && this.list.unsetFocus())
            return true;

        if (KeyUtil.isTab(keyCode) && this.list.setFocus(this::isInvalidWidget))
            return true;

        if (this.list.getFocusKeyPress(keyCode, scanCode, modifiers))
            return true;

        return super.onKeyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * A handler method for when the mouse is scrolled while an overlay window is open.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param delta A change in time between frames.
     * @return Whether this method handled the mouse scroll event.
     */
    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double delta)
    {
        return this.list.mouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * A handler method for when a dragging action occurs while a mouse button is pressed while an overlay window is open.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The clicked mouse button.
     * @param dragX A new drag x-position.
     * @param dragY A new drag y-position.
     * @return Whether this method handled the mouse drag event.
     */
    @Override
    public boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        boolean isDragging = super.onDrag(mouseX, mouseY, button, dragX, dragY);

        if (isDragging)
            this.refreshRowList();
        else
            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);

        return isDragging;
    }

    /**
     * A handler method for when click event occurs while an overlay window is open.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The clicked mouse button.
     * @return Whether this method handled the mouse drag event.
     */
    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        int startX = (int) this.x + W_TOP_LEFT_CORNER + this.getDrawWidth() - 10;
        int startY = (int) this.y + 4;

        if (MathUtil.isWithinBox(mouseX, mouseY, startX, startY, HINT_SQUARE, HINT_SQUARE))
            this.hint = !this.hint;

        this.list.mouseClicked(mouseX, mouseY, button);

        return super.onClick(mouseX, mouseY, button);
    }

    /**
     * A handler method for when the game window is resized while an overlay window is open.
     */
    @Override
    public void onResize()
    {
        ContainerButton.collapseAll();
        Screen screen = Minecraft.getInstance().screen;

        if (screen instanceof ConfigScreen configScreen)
        {
            configScreen.getRenderer().generateRowsFromAllGroups();
            this.init();
        }
    }

    /**
     * A handler method for when the overlay window is closed.
     */
    @Override
    public void onClose()
    {
        this.list.children().clear();
        this.configRowList = null;

        super.onClose();
    }

    /**
     * Rendering instructions for this overlay window.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in time between frames.
     */
    @Override
    public void onMainRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Render category list
        if (this.list != null)
            this.list.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Rendering instructions for post overlay window rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in time between frames.
     */
    @Override
    public void onPostRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Render hint button
        this.renderHintButton(graphics, mouseX, mouseY);

        // Render hint tooltip
        this.renderTooltipHint(graphics, mouseX, mouseY);
    }
}
