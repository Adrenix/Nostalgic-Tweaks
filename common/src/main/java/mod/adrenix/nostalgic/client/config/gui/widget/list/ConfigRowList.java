package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryList;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.IPermissionWidget;
import mod.adrenix.nostalgic.client.config.gui.widget.TextWidget;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.input.StringInput;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.client.config.gui.widget.button.*;
import mod.adrenix.nostalgic.client.config.gui.widget.button.CycleButton;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.ConfigSlider;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.mixin.widen.IMixinAbstractWidget;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConfigRowList extends AbstractRowList<ConfigRowList.Row>
{
    /* Widget Constants */

    public static final int BUTTON_START_Y = 0;
    public static final int BUTTON_HEIGHT = 20;
    public static final int BUTTON_WIDTH = 120;
    public static final int ROW_WIDGET_GAP = 2;
    public static final int TEXT_FROM_END = 8;
    public static final int TEXT_START = 8;
    public static final int CAT_TEXT_START = 28;
    public static final int SUB_TEXT_START = 48;
    public static final int EMB_TEXT_START = 68;

    @Nullable
    private static ConfigRowList.Row rendering = null;

    /* Widget Start Positions */

    public static int getStartX()
    {
        if (ConfigRowList.rendering == null)
            return TEXT_START;
        else
            return ConfigRowList.rendering.getIndent();
    }

    public static int getControlStartX()
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null) return 0;
        return screen.width;
    }

    /* Instance Fields */

    public final ConfigScreen screen;
    public boolean setSelection = false;

    /* Constructor */

    public ConfigRowList(ConfigScreen screen, int width, int height, int y0, int y1, int itemHeight)
    {
        super(screen.getMinecraft(), width, height, y0, y1, itemHeight);
        this.screen = screen;
    }

    /* Overrides */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        if (this.screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && clicked)
            this.screen.getWidgets().getSearchInput().setFocus(false);
        return clicked;
    }

    /* Tab Key Support */

    private boolean isInvalidWidget(Row row, AbstractWidget widget)
    {
        boolean isGroup = widget instanceof GroupButton;
        boolean isReset = widget.equals(row.reset);
        boolean isController = widget.equals(row.controller);
        boolean isInactive = (isGroup || isReset || isController) && !widget.isActive();

        return isInactive || (!isGroup && !isReset && !isController);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (ConfigScreen.isEsc(keyCode) && this.unsetFocus())
            return true;

        if (ConfigScreen.isTab(keyCode) && this.getFocus(this::isInvalidWidget))
            return true;

        return this.getFocusKeyPress(keyCode, scanCode, modifiers);
    }

    /* Config Row Providers */

    public <E extends Enum<E>> Row getRow(GroupType group, String key, Object value)
    {
        if (value instanceof Boolean)
            return new BooleanRow(group, key, (Boolean) value).add();
        else if (value instanceof Integer)
            return new IntSliderRow(group, key, (Integer) value).add();
        else if (value instanceof Enum)
            return new EnumRow<E>(group, key, value).add();
        else if (value instanceof String)
        {
            if (CommonReflect.getAnnotation(group, key, TweakSide.Color.class) != null)
                return new ColorRow(group, key, (String) value).add();
            return new StringRow(group, key, (String) value).add();
        }
        else
            return new InvalidRow(group, key, value).add();
    }

    public void addRow(ConfigRowList.Row row) { this.addEntry(row); }
    public void addRow(GroupType group, String key, Object value) { this.addEntry(this.getRow(group, key, value)); }

    /* Row Templates */

    // Abstract Entry Row
    public abstract static class AbstractRow<T>
    {
        protected final TweakClientCache<T> cache;
        protected final GroupType group;
        protected final String key;
        protected final T value;

        protected AbstractRow(GroupType group, String key, T value)
        {
            this.cache = TweakClientCache.get(group, key);
            this.group = group;
            this.key = key;
            this.value = value;
        }

        protected ConfigRowList.Row create(AbstractWidget controller)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            TweakClient.Gui.NoTooltip noTooltip = CommonReflect.getAnnotation(this.cache, TweakClient.Gui.NoTooltip.class);

            widgets.add(controller);
            widgets.add(new ResetButton(this.cache, controller));
            widgets.add(new StatusButton(this.cache, controller));
            widgets.add(new TweakTag(this.cache, controller, noTooltip == null));

            if (noTooltip == null)
                widgets.add(new TooltipButton(this.cache, controller));

            if (controller instanceof ColorInput color)
                widgets.add(color.getWidget());

            return new ConfigRowList.Row(widgets, controller, this.cache);
        }

        public ConfigRowList.Row add() { return new ConfigRowList.Row(new ArrayList<>(), this.cache); }
    }

    // Invalid Entry
    public static class InvalidRow extends AbstractRow<Object>
    {
        public InvalidRow(GroupType group, String key, Object value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row add()
        {
            return new ConfigRowList.Row(new ArrayList<>(), this.cache);
        }
    }

    // Boolean Entry
    public static class BooleanRow extends AbstractRow<Boolean>
    {
        public BooleanRow(GroupType group, String key, boolean value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row add()
        {
            return this.create(new BooleanButton(this.cache, (button) -> this.cache.setCurrent(!this.cache.getCurrent())));
        }
    }

    // Integer Slider Entry
    public static class IntSliderRow extends AbstractRow<Integer>
    {
        public IntSliderRow(GroupType group, String key, int value) { super(group, key, value); }

        @Override public ConfigRowList.Row add() { return this.create(new ConfigSlider(this.cache)); }
    }

    // String Entry
    public static class StringRow extends AbstractRow<String>
    {
        public StringRow(GroupType group, String key, String value) { super(group, key, value); }

        @Override public ConfigRowList.Row add() { return this.create(new StringInput(this.cache).getWidget()); }
    }

    // Color Entry
    public static class ColorRow extends AbstractRow<String>
    {
        public ColorRow(GroupType group, String key, String value) { super(group, key, value); }

        @Override public ConfigRowList.Row add() { return this.create(new ColorInput(this.cache)); }
    }

    // Enum Cycle Entry
    public static class EnumRow<E extends Enum<E>> extends AbstractRow<E>
    {
        @SuppressWarnings("unchecked")
        public EnumRow(GroupType group, String key, Object value) { super(group, key, (E) value); }

        @Override
        public ConfigRowList.Row add()
        {
            return this.create(new CycleButton<>(this.cache, this.cache.getCurrent().getDeclaringClass(), (button) -> ((CycleButton<?>) button).toggle()));
        }
    }

    /* Manual Custom Row Builders */

    // Key Binding Row
    public record BindingRow(KeyMapping mapping)
    {
        public ConfigRowList.Row add()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            KeyBindButton controller = new KeyBindButton(this.mapping);

            widgets.add(controller);
            widgets.add(new ResetButton(null, controller));

            return new ConfigRowList.Row(widgets, controller, null);
        }
    }

    // Single Centered Row Entry
    public record SingleCenteredRow(Screen screen, AbstractWidget center)
    {
        public ConfigRowList.Row add()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.center.x = (this.screen.width / 2) - (this.center.getWidth() / 2);
            this.center.y = 0;

            widgets.add(this.center);

            return new ConfigRowList.Row(widgets, null);
        }
    }

    // Single Left-Aligned Row Entry
    public record SingleLeftRow(AbstractWidget left, int indent)
    {
        public ConfigRowList.Row add()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.left.x = indent;
            this.left.y = 0;

            widgets.add(this.left);

            return new ConfigRowList.Row(widgets, null);
        }
    }

    // Manual Row Entry
    public record ManualRow(List<AbstractWidget> widgets)
    {
        public ConfigRowList.Row add() { return new ConfigRowList.Row(widgets, null); }
    }

    // Category & Subcategory Entry

    public enum CatType
    {
        CATEGORY,    // The main parent (subscribes to a main group like Eye Candy)
        SUBCATEGORY, // A child of the parent (subscribes to a category like Combat Gameplay)
        EMBEDDED     // A category embedded within a subcategory (like 'Buttons' to 'Title Screen Candy')
    }

    public static class CategoryRow
    {
        private ArrayList<ConfigRowList.Row> cache;
        private GroupButton controller;
        private final Enum<?> id;
        private final ConfigRowList list;
        private final Component title;
        private final Supplier<ArrayList<ConfigRowList.Row>> childrenSupply;
        private final CatType categoryType;
        private boolean expanded = false;

        public CategoryRow(ConfigRowList list, Component title, Supplier<ArrayList<ConfigRowList.Row>> childrenSupply, Enum<?> id, CatType categoryType)
        {
            this.id = id;
            this.list = list;
            this.title = title;
            this.childrenSupply = childrenSupply;
            this.categoryType = categoryType;
        }

        public CategoryRow(ConfigRowList list, Component title, Supplier<ArrayList<ConfigRowList.Row>> childrenSupply, Enum<?> id)
        {
            this(list, title, childrenSupply, id, CatType.CATEGORY);
        }

        public static int getIndent(CatType categoryType)
        {
            return switch (categoryType)
            {
                case CATEGORY -> TEXT_START;
                case SUBCATEGORY -> CAT_TEXT_START;
                case EMBEDDED -> SUB_TEXT_START;
            };
        }

        public boolean isExpanded() { return this.expanded; }

        private void setGroupMetadata()
        {
            // If the parent group contains only subcategories then we don't want pipe bars in the last subcategory
            for (ConfigRowList.Row categories : this.list.children())
            {
                for (AbstractWidget widget : categories.children)
                {
                    // Check if parent group
                    if (widget instanceof GroupButton group && this.controller.equals(group))
                    {
                        // Ensure children only consist of subcategories
                        GroupButton last = null;
                        boolean isSubOnly = true;

                        for (ConfigRowList.Row subcategories : this.cache)
                        {
                            for (AbstractWidget subWidget : subcategories.children)
                            {
                                if (subWidget instanceof GroupButton subGroup)
                                    last = subGroup;
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
                        if (isSubOnly && last != null)
                            last.setLastSubcategory(true);

                        // If category group has tweaks at the end, a category bar is needed for embedded tree rendering
                        if ((!isSubOnly || group.isParentTreeNeeded()) && last != null)
                            last.setParentTreeNeeded(true);
                    }
                }
            }
        }

        private int getHeaderIndex()
        {
            int header = -1;
            for (int i = 0; i < this.list.children().size(); i++)
            {
                if (header != -1)
                    break;

                for (AbstractWidget widget : this.list.children().get(i).children)
                {
                    if (widget instanceof GroupButton && widget.equals(this.controller))
                    {
                        header = i;
                        break;
                    }
                }
            }

            return header == -1 ? 0 : ++header;
        }

        public void expand()
        {
            this.expanded = true;
            this.cache = this.childrenSupply.get();
            this.setGroupMetadata();

            int header = this.getHeaderIndex();
            for (ConfigRowList.Row row : this.cache)
            {
                this.list.children().add(header, row);

                row.setIndent(getIndent(this.categoryType) + 20);
                row.setGroup(this.controller);
                header++;
            }

            if (this.cache.size() > 0)
            {
                this.cache.get(0).setFirst(true);
                this.cache.get(this.cache.size() - 1).setLast(true);
            }
        }

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
                            if (widget instanceof GroupButton group)
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

        public ConfigRowList.Row add()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller = new GroupButton(this, this.id, this.title, this.categoryType);
            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, this.controller, null);
        }
    }

    /* Row Provider */

    public static class Row extends ContainerObjectSelectionList.Entry<Row>
    {
        /* Instance Fields */

        @Nullable private GroupButton group;
        @Nullable public final TweakClientCache<?> cache;
        @Nullable public final AbstractWidget controller;
        @Nullable public ResetButton reset = null;
        public final List<AbstractWidget> children;
        private boolean first = false;
        private boolean last = false;
        private float fadeIn = 0F;
        private int indent = TEXT_START;

        /* Constructor */

        public Row(List<AbstractWidget> list, @Nullable AbstractWidget controller, @Nullable TweakClientCache<?> cache)
        {
            this.children = list;
            this.controller = controller;
            this.cache = cache;

            // Assign reset button to row if applicable
            for (AbstractWidget widget : list)
            {
                if (widget instanceof ResetButton button)
                    this.reset = button;
            }

            if (this.controller instanceof GroupButton groupButton)
                this.group = groupButton;
            else
                this.group = null;
        }

        public Row(List<AbstractWidget> list, @Nullable TweakClientCache<?> cache) { this(list, null, cache); }

        /* Setters & Getters */

        public boolean isFirst() { return this.first; }
        public void setFirst(boolean state) { this.first = state; }

        public boolean isLast() { return this.last; }
        public void setLast(boolean state) { this.last = state; }

        public void setIndent(int indent) { this.indent = indent; }
        public int getIndent() { return this.indent; }

        public void setGroup(@Nullable GroupButton group) { this.group = group; }

        /* Overrides & Rendering */

        private boolean isBindingRow()
        {
            for (AbstractWidget widget : this.children)
                if (widget instanceof KeyBindButton)
                    return true;
            return false;
        }

        private boolean isRowLocked()
        {
            if (this.cache == null)
                return false;

            boolean isClient = this.cache.isClient();
            boolean isDynamic = this.cache.isDynamic();

            if ((isClient && !isDynamic) || !NostalgicTweaks.isNetworkVerified())
                return false;

            if (this.cache.isDynamic() && NostalgicTweaks.isNetworkVerified() && !NetUtil.isPlayerOp())
                return true;

            for (AbstractWidget widget : this.children)
                if (widget instanceof IPermissionWidget && Minecraft.getInstance().player != null)
                    return !NetUtil.isPlayerOp(Minecraft.getInstance().player);

            return false;
        }

        private void renderOnHover(PoseStack poseStack, Screen screen, int top, int height)
        {
            boolean isHoverOn = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_ROW_HIGHLIGHT).getCurrent();
            if (Overlay.isOpened() || !isHoverOn || (this.cache == null && !this.isBindingRow()))
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
            boolean isFaded = (Boolean) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE).getCurrent();
            int[] rgba = ModUtil.Text.toHexRGBA((String) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR).getCurrent());
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

        private void renderTree(PoseStack poseStack, int top, int height)
        {
            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.CATEGORY_TREE_COLOR);
            TweakClientCache<Boolean> tree = TweakClientCache.get(GuiTweak.DISPLAY_CATEGORY_TREE);
            boolean isTreeEnabled = tree.getCurrent();
            boolean isIndented = this.indent != TEXT_START;

            if (!isIndented || !isTreeEnabled)
                return;

            boolean isSubIndented = this.group == null || !this.group.isLastSubcategory();
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

            int rgba = ModUtil.Text.toHexInt(color.getCurrent());
            float leftX = this.indent - 16.0F;
            float rightX = leftX + 10.0F;
            float topY = (float) top + (height / 2.0F) - 2.0F;
            float bottomY = topY + 2.0F;

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            // Horizontal bar [ - ]

            if (!isTextRow && !isRowEmpty)
                RenderUtil.fill(buffer, matrix, leftX + 2.0F, rightX, topY, bottomY, rgba);

            // Vertical bar [ | ]

            rightX = leftX + 2.0F;
            topY = this.isFirst() ? top - 6.0F : top - 1.0F;
            bottomY = this.isLast() ? (float) top + (height / 2.0F) : (float) (top + height) + 3.0F;

            RenderUtil.fill(buffer, matrix, leftX, rightX, topY, bottomY, rgba);

            // Secondary embedded and subcategory vertical bar [ |  L ]
            boolean isVertical = this.indent == SUB_TEXT_START || this.indent == EMB_TEXT_START;

            if (isVertical && isSubIndented)
            {
                leftX = this.indent - 36.0F;
                rightX = leftX + 2.0F;
                bottomY = (float) (top + height) + 3.0F;

                RenderUtil.fill(buffer, matrix, leftX, rightX, topY + (this.isFirst() ? 5.0F : 0.0F), bottomY, rgba);

                if (this.indent == EMB_TEXT_START)
                    RenderUtil.fill(buffer, matrix, leftX - 20.0F, rightX - 20.0F, topY + (this.isFirst() ? 5.0F : 0.0F), bottomY, rgba);
            }

            if (this.indent == EMB_TEXT_START && this.group.isParentTreeNeeded() && this.group.isLastSubcategory())
            {
                leftX = this.indent - 56.0F;
                rightX = leftX + 2.0F;
                bottomY = (float) (top + height) + 3.0F;

                RenderUtil.fill(buffer, matrix, leftX, rightX, topY + (this.isFirst() ? 5.0F : 0.0F), bottomY, rgba);
            }

            tesselator.end();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        private static boolean isRowClipped(Screen screen, AbstractWidget widget)
        {
            return widget.x + widget.getWidth() >= screen.width - TEXT_FROM_END;
        }

        @Override
        public List<? extends NarratableEntry> narratables() { return this.children; }

        @Override
        public List<? extends GuiEventListener> children() { return this.children; }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            Font font = Minecraft.getInstance().font;
            ConfigScreen screen = (ConfigScreen) Minecraft.getInstance().screen;
            if (screen == null) return;

            // Update renderer tracker
            ConfigRowList.rendering = this;

            // Multiplayer row lockout
            boolean isRowLocked = this.isRowLocked();

            // Row highlights
            boolean isFading = (Boolean) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE).getCurrent();
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
                                tag.setTitle(ModUtil.Text.ellipsis(tag.getTitle()));
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
                            ((IMixinAbstractWidget) list.getLastSelection()).NT$setFocus(false);

                        list.setSelection = false;
                        list.setLastSelection(widget);
                        ((IMixinAbstractWidget) widget).NT$setFocus(true);
                    }
                }

                // Apply row title color formatting
                Component title = Component.empty();

                if (this.cache != null)
                {
                    Component translation = Component.translatable(tagger == null ? this.cache.getLangKey() : tagger.getTitle());
                    title = this.cache.isSavable() ? translation.copy().withStyle(ChatFormatting.ITALIC) : translation.copy().withStyle(ChatFormatting.RESET);

                    if (isFocused)
                        title = title.copy().withStyle(ChatFormatting.GOLD);
                }
                else if (widget instanceof KeyBindButton)
                {
                    Component translation = Component.translatable(((KeyBindButton) widget).getMapping().getName());
                    title = KeyBindButton.isMappingConflicted(((KeyBindButton) widget).getMapping()) ? translation.copy().withStyle(ChatFormatting.RED) : translation.copy().withStyle(ChatFormatting.RESET);
                }
                else if (widget instanceof GroupButton groupButton)
                    groupButton.setHighlight(CategoryList.OVERLAY.getSelected() == this);

                // Render row title
                int dy = screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH ? 11 : 0;
                Screen.drawString(poseStack, font, title, startX, top + 6 + dy, 0xFFFFFF);

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
                    if (widget instanceof TextWidget text)
                    {
                        widget.x = text.startX;
                        widget.y += 2;
                    }
                    else
                        widget.y += 11;

                    cacheY = widget.y + (widget instanceof EditBox ? -1 : 0);
                }

                // Render final widget
                widget.active = !Overlay.isOpened();

                // Apply row locking for multiplayer
                if (widget instanceof IPermissionWidget && Minecraft.getInstance().player != null)
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

                if (isEllipsis && isOverText && this.cache != null)
                {
                    screen.renderLast.add(() ->
                        screen.renderComponentTooltip(poseStack, ModUtil.Wrap.tooltip(Component.translatable(this.cache.getTranslation()), 35), mouseX, mouseY))
                    ;
                }

                // Debugging
                if (NostalgicTweaks.isDebugging() && this.isMouseOver(mouseX, mouseY) && this.cache != null)
                {
                    if (screen.getConfigTab() == ConfigScreen.ConfigTab.SEARCH)
                    {
                        String color = "§a";
                        int weight = this.cache.getWeight();
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
                        Object clientCache = this.cache.getSavedValue();
                        String clientColor = clientCache instanceof Boolean state ? state ? "§2" : "§4" : "";
                        lines.add(Component.literal(String.format("Client Cache: %s%s", clientColor, clientCache)));

                        TweakServerCache<?> serverCache = TweakServerCache.all().get(this.cache.getId());
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
    }
}
