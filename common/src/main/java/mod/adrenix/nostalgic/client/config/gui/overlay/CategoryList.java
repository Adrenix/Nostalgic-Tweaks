package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.GroupButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.AbstractRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.mixin.widen.AbstractWidgetAccessor;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a list of current categories, subcategories, and tweaks.
 * Assists the configuration screen so users can quickly jump to a specific row.
 */

public class CategoryList extends Overlay
{
    /**
     * Singleton Constructor
     */

    public static final int DEFAULT_WIDTH = 256;
    public static final int DEFAULT_HEIGHT = 220;

    private CategoryList() { super(DEFAULT_WIDTH, DEFAULT_HEIGHT); }

    /**
     * Register Overlay
     */

    public static final CategoryList OVERLAY = new CategoryList();
    static { Overlay.register(OVERLAY); }

    /**
     * Constants
     */

    private static final int U_TOP_LEFT_CORNER = 0;
    private static final int V_TOP_LEFT_CORNER = 0;
    private static final int W_TOP_LEFT_CORNER = 16;
    private static final int H_TOP_LEFT_CORNER = 15;

    private static final int U_TOP_RIGHT_CORNER = 19;
    private static final int V_TOP_RIGHT_CORNER = 0;
    private static final int W_TOP_RIGHT_CORNER = 16;
    private static final int H_TOP_RIGHT_CORNER = 15;

    private static final int U_TOP_BAR = 17;
    private static final int V_TOP_BAR = 0;
    private static final int H_TOP_BAR = 15;

    private static final int U_LEFT_BAR = 0;
    private static final int V_LEFT_BAR = 16;
    private static final int W_LEFT_BAR = 8;

    private static final int U_RIGHT_BAR = 27;
    private static final int V_RIGHT_BAR = 16;
    private static final int W_RIGHT_BAR = 8;

    private static final int U_BOTTOM_LEFT_CORNER = 0;
    private static final int V_BOTTOM_LEFT_CORNER = 18;
    private static final int W_BOTTOM_LEFT_CORNER = 16;
    private static final int H_BOTTOM_LEFT_CORNER = 8;

    private static final int U_BOTTOM_BAR = 17;
    private static final int V_BOTTOM_BAR = 18;
    private static final int H_BOTTOM_BAR = 8;

    private static final int U_BOTTOM_RIGHT_CORNER = 19;
    private static final int V_BOTTOM_RIGHT_CORNER = 18;
    private static final int W_BOTTOM_RIGHT_CORNER = 16;
    private static final int H_BOTTOM_RIGHT_CORNER = 8;

    private static final int U_CLOSE_OFF = 35;
    private static final int V_CLOSE_OFF = 9;
    private static final int U_CLOSE_ON = 35;
    private static final int V_CLOSE_ON = 0;

    private static final int U_HINT_OFF = 44;
    private static final int V_HINT_OFF = 9;
    private static final int U_HINT_ON = 44;
    private static final int V_HINT_ON = 0;
    private static final int HINT_SQUARE = 9;

    private int getListStartX() { return (int) this.x + 10; }
    private int getListStartY() { return (int) this.y + H_TOP_RIGHT_CORNER; }
    private int getListEndY() { return (int) this.y + this.getListHeight() + H_TOP_LEFT_CORNER - 1; }
    private int getListWidth() { return (int) this.x + DEFAULT_WIDTH - 9; }
    private int getListHeight() { return this.height - H_TOP_RIGHT_CORNER - H_TOP_LEFT_CORNER; }
    private int getDrawWidth() { return this.width - W_TOP_RIGHT_CORNER - W_TOP_LEFT_CORNER; }
    private boolean hint = false;
    private static final String TWEAK_STAR = "*";

    /**
     * Text Row Button Widget
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
            super(startX, startY, getTextWidth(title), getTextHeight(), title, onClick);

            this.screen = screen;
            this.title = title;
            this.color = color;
            this.row = row;

            if (this.title.getString().equals(TWEAK_STAR))
                this.active = false;
        }

        /* Widget Overrides */

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            if (Overlay.isOverTitle(mouseX, mouseY))
                return;

            super.onClick(mouseX, mouseY);
        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            boolean isTweak = this.title.getString().equals(TWEAK_STAR);
            boolean isControl = this.title.getString().length() == 1;
            boolean isSelected = this.row.equals(CategoryList.OVERLAY.getSelected()) && isControl;
            boolean isTabbed = this.isFocused() && !isTweak;
            boolean isHover = ModUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height) && !isTweak;
            int highlight = isHover ? 0xFFD800 : this.color;

            if (isTabbed)
                highlight = 0x3AC0FF;

            if (isSelected && CategoryList.OVERLAY.list.getLastSelection() == null)
                CategoryList.OVERLAY.list.setLastSelection(this);

            drawString(poseStack, this.screen.getFont(), isSelected ? this.title.copy().withStyle(ChatFormatting.GOLD) : this.title, this.x, this.y, highlight);
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) { }
    }

    /**
     * Text Row List
     */

    private static class TextRowList extends AbstractRowList<TextRow>
    {
        public static int color = 0xFFFFFF;
        public final ConfigScreen screen;

        public TextRowList(ConfigScreen screen, int width, int height, int startY, int endY, int rowHeight)
        {
            super(screen.getMinecraft(), width, height, startY, endY, rowHeight);
            this.screen = screen;
            this.setAsTransparentList();
        }

        public void addRow(TextRow row) { this.addEntry(row); }

        public record EntryRow(TextRowList list, ConfigRowList.Row row, @Nullable GroupButton group, Component title, int indent, Button.OnPress onClick)
        {
            public TextRow add()
            {
                List<AbstractWidget> widgets = new ArrayList<>();
                Component control = group == null ? Component.literal(TWEAK_STAR) : Component.literal(group.isExpanded() ? "-" : "+");

                widgets.add(new TextButton(this.list().screen, row, color, this.list.x0 + 2 + indent, 0, control, this::toggle));
                widgets.add(new TextButton(this.list().screen, row, color, this.list.x0 + 11 + indent, 0, this.title, onClick));
                color = color == 0xFFFFFF ? 0xB2B2B2 : 0xFFFFFF;

                return new TextRow(ImmutableList.copyOf(widgets));
            }

            private void toggle(Button button)
            {
                if (this.group != null)
                {
                    double scrolled = OVERLAY.list.getScrollAmount();
                    this.group.silentPress();
                    button.setMessage(Component.literal(this.group.isExpanded() ? "-" : "+"));

                    int position = 0;
                    for (int i = 0; i < OVERLAY.list.children().size() - 1; i++)
                    {
                        for (AbstractWidget widget : OVERLAY.list.children().get(i).children)
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

                    OVERLAY.list.children().clear();
                    OVERLAY.generateWidgets();

                    if (scrolled > 0.0D)
                        OVERLAY.list.setScrollAmount(scrolled);

                    if (position < OVERLAY.list.children().size())
                    {
                        AbstractWidget widget = OVERLAY.list.children().get(position).children.get(0);
                        ((AbstractWidgetAccessor) widget).NT$setFocus(true);
                        OVERLAY.list.setLastSelection(widget);
                    }
                }
            }
        }
    }

    private static class TextRow extends ContainerObjectSelectionList.Entry<TextRow>
    {
        public final List<AbstractWidget> children;

        public TextRow(List<AbstractWidget> children)
        {
            this.children = children;
        }

        @Override
        public List<? extends GuiEventListener> children() { return this.children; }

        @Override
        public List<? extends NarratableEntry> narratables() { return this.children; }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            // Draw row background
            if (this.isMouseOver(mouseX, mouseY))
            {
                RenderSystem.depthFunc(515);
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
                RenderSystem.disableTexture();
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();
                Matrix4f matrix = poseStack.last().pose();

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                RenderUtil.fill(buffer, matrix, left - 6, left + DEFAULT_WIDTH - 13, top - 1, top + height + 2, 0x32FFFFFF);
                tesselator.end();
            }

            // Update widget heights and render
            for (AbstractWidget widget : this.children)
            {
                widget.y = top;
                widget.render(poseStack, mouseX, mouseY, partialTick);
            }

            RenderSystem.disableBlend();
        }
    }

    /**
     * Overlay Overrides
     */

    private TextRowList list;
    private ConfigRowList all;
    private ConfigRowList.Row selected = null;

    @Nullable
    public ConfigRowList.Row getSelected() { return this.selected; }

    public void open(ConfigRowList configRowList)
    {
        Overlay.start(CategoryList.OVERLAY);

        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null)
            return;

        this.isJustOpened = true;
        this.hint = false;
        this.all = configRowList;
        this.x = (screen.width / 2.0D) - (this.width / 2.0D);
        this.y = (screen.height / 2.0D) - (this.height / 2.0D);

        if (this.list == null)
            this.generateWidgets();
    }

    @Override
    public void generateWidgets()
    {
        ConfigScreen screen = (ConfigScreen) Minecraft.getInstance().screen;
        if (screen == null || (this.list != null && !this.list.children().isEmpty()))
            return;

        if (this.list != null)
            this.list.resetLastSelection();

        int width = this.getListWidth();
        int height = this.getListHeight();
        int startY = this.getListStartY();
        int endY = this.getListEndY();

        TextRowList.color = 0xFFFFFF;
        this.list = new TextRowList(screen, width, height, startY, endY, screen.getFont().lineHeight + 2);
        this.list.setLeftPos(this.getListStartX());

        for (ConfigRowList.Row row : this.all.children())
        {
            int indent = 0;
            if (row.getIndent() == ConfigRowList.CAT_TEXT_START)
                indent = 9;
            else if (row.getIndent() == ConfigRowList.SUB_TEXT_START)
                indent = 18;
            else if (row.getIndent() == ConfigRowList.EMB_TEXT_START)
                indent = 27;

            Button.OnPress jump = button -> {
                this.all.setScrollOn(row);
                this.selected = row;
                screen.getWidgets().getConfigRowList().setSelection = true;
            };

            if (row.controller instanceof GroupButton group)
                this.list.addRow(new TextRowList.EntryRow(this.list, row, group, group.getTitle(), indent, jump).add());

            if (row.cache != null)
                this.list.addRow(new TextRowList.EntryRow(this.list, row, null, Component.translatable(row.cache.getLangKey()), indent, jump).add());
        }
    }

    private boolean isInvalidWidget(TextRow row, AbstractWidget widget)
    {
        return widget instanceof TextButton text && text.title.getString().equals(TWEAK_STAR);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (ConfigScreen.isEsc(keyCode) && this.list.unsetFocus())
            return true;

        if (ConfigScreen.isTab(keyCode) && this.list.getFocus(this::isInvalidWidget))
            return true;

        if (this.list.getFocusKeyPress(keyCode, scanCode, modifiers))
            return true;

        return super.onKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double delta)
    {
        return this.list.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        boolean isDragging = super.onDrag(mouseX, mouseY, button, dragX, dragY);

        if (isDragging)
        {
            double scrolled = this.list.getScrollAmount();
            this.list.children().clear();
            this.generateWidgets();
            this.list.setScrollAmount(scrolled);
        }
        else
            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);

        return isDragging;
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        int startX = (int) this.x + W_TOP_LEFT_CORNER + this.getDrawWidth() - 10;
        int startY = (int) this.y + 4;
        if (ModUtil.Numbers.isWithinBox(mouseX, mouseY, startX, startY, HINT_SQUARE, HINT_SQUARE))
            this.hint = !this.hint;

        this.list.mouseClicked(mouseX, mouseY, button);
        return super.onClick(mouseX, mouseY, button);
    }

    @Override
    public void onResize()
    {
        this.onClose();

        GroupButton.collapseAll();
        Screen screen = Minecraft.getInstance().screen;

        if (screen instanceof ConfigScreen configScreen)
        {
            configScreen.getRenderer().generateAllList();
            this.open(configScreen.getWidgets().getConfigRowList());
        }
    }

    @Override
    public void onClose()
    {
        this.list.children().clear();
        this.all = null;

        super.onClose();
    }

    @Override
    public void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null || !this.isOpen())
            return;

        if (this.list.children().isEmpty())
            this.generateWidgets();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Render semi-transparent background
        float leftX = (float) this.x + 6;
        float rightX = (float) this.x + this.width - 4;
        float topY = (float) this.y + 14;
        float bottomY = (float) this.y + this.height - 10;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderUtil.fill(buffer, matrix, leftX, rightX, topY, bottomY, 0xC6000000);
        tesselator.end();

        // Render category list
        if (this.list != null)
            this.list.render(poseStack, mouseX, mouseY, partialTick);

        // Render border

        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, ModUtil.Resource.CATEGORY_LIST);

        int startX = (int) this.x;
        int startY = (int) this.y;
        int drawWidth = this.getDrawWidth();
        int drawHeight = this.getListHeight();

        // Draw top left
        blit(poseStack, startX, startY, U_TOP_LEFT_CORNER, V_TOP_LEFT_CORNER, W_TOP_LEFT_CORNER, H_TOP_LEFT_CORNER);

        // Draw top width
        for (int i = 0; i < drawWidth; i++)
            blit(poseStack, i + startX + W_TOP_LEFT_CORNER, startY, U_TOP_BAR, V_TOP_BAR, 1, H_TOP_BAR);

        // Draw top right
        blit(poseStack, startX + W_TOP_LEFT_CORNER + drawWidth, startY, U_TOP_RIGHT_CORNER, V_TOP_RIGHT_CORNER, W_TOP_RIGHT_CORNER, H_TOP_RIGHT_CORNER);

        // Draw sidebars
        for (int i = 0; i < drawHeight; i++)
        {
            blit(poseStack, startX, i + startY + H_TOP_LEFT_CORNER, U_LEFT_BAR, V_LEFT_BAR, W_LEFT_BAR, 1);
            blit(poseStack, startX + W_TOP_LEFT_CORNER + drawWidth + 8, i + startY + H_TOP_RIGHT_CORNER, U_RIGHT_BAR, V_RIGHT_BAR, W_RIGHT_BAR, 1);
        }

        // Draw bottom left
        blit(poseStack, startX, startY + H_TOP_LEFT_CORNER + drawHeight, U_BOTTOM_LEFT_CORNER, V_BOTTOM_LEFT_CORNER, W_BOTTOM_LEFT_CORNER, H_BOTTOM_LEFT_CORNER);

        // Draw bottom width
        for (int i = 0; i < drawWidth; i++)
            blit(poseStack, i + startX + W_BOTTOM_LEFT_CORNER, startY + H_TOP_LEFT_CORNER + drawHeight, U_BOTTOM_BAR, V_BOTTOM_BAR, 1, H_BOTTOM_BAR);

        // Draw bottom right
        blit(poseStack, startX + W_BOTTOM_LEFT_CORNER + drawWidth, startY + H_TOP_RIGHT_CORNER + drawHeight, U_BOTTOM_RIGHT_CORNER, V_BOTTOM_RIGHT_CORNER, W_BOTTOM_RIGHT_CORNER, H_BOTTOM_RIGHT_CORNER);

        // Render close and hint button
        int closeX = startX + W_TOP_LEFT_CORNER + drawWidth;
        int closeY = startY + 4;
        this.isOverClose = ModUtil.Numbers.isWithinBox(mouseX, mouseY, closeX, closeY, CLOSE_WIDTH, CLOSE_HEIGHT);

        blit(poseStack, closeX, closeY, this.isOverClose ? U_CLOSE_ON : U_CLOSE_OFF, this.isOverClose ? V_CLOSE_ON : V_CLOSE_OFF, CLOSE_WIDTH, CLOSE_HEIGHT);

        int hintX = closeX - 10;
        boolean isOverHint = ModUtil.Numbers.isWithinBox(mouseX, mouseY, hintX, closeY, HINT_SQUARE, HINT_SQUARE);

        blit(poseStack, hintX, closeY, isOverHint ? U_HINT_ON : U_HINT_OFF, isOverHint ? V_HINT_ON : V_HINT_OFF, HINT_SQUARE, HINT_SQUARE);

        // Text needs to be rendered last since it will interfere with alpha rendering
        int color = this.isMouseOverTitle(mouseX, mouseY) && !this.isOverClose && !isOverHint ? 0xFFF65B : 0xFFFFFF;
        drawString(Component.translatable(LangUtil.Gui.GUI_OVERLAY_LIST), startX + 20, startY + 4, color);

        // Render dragging and tooltip hints
        boolean isOverIcon = ModUtil.Numbers.isWithinBox(mouseX, mouseY, this.x + 6, this.y + 3, 9, 9);
        if (isOverIcon)
        {
            List<Component> tooltip = ModUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.GUI_OVERLAY_DRAG_TIP), 36);
            screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
        }

        if (isOverHint && this.hint)
        {
            List<Component> tooltip = ModUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.GUI_OVERLAY_LIST_HINT), 36);
            screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
        }

        RenderSystem.disableBlend();
    }
}
