package mod.adrenix.nostalgic.client.config.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.client.config.annotation.TweakEntry;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.*;
import mod.adrenix.nostalgic.client.config.gui.widget.button.CycleButton;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.ConfigSlider;
import mod.adrenix.nostalgic.client.config.reflect.ConfigReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import mod.adrenix.nostalgic.client.config.reflect.GroupType;
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

    public static final int BUTTON_HEIGHT = 20;
    public static final int CONTROL_BUTTON_WIDTH = 120;
    public static final int ROW_WIDGET_GAP = 2;
    public static final int TEXT_START = 8;
    public static final int TEXT_FROM_END = 8;

    public static int getControlStartX()
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null) return 0;
        return screen.width;
    }

    /* Instance Fields */

    public final ConfigScreen screen;

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

    /* Config Row Providers */

    public <E extends Enum<E>> Row getRow(GroupType group, String key, Object value)
    {
        if (value instanceof Boolean)
            return new BooleanRow(group, key, (Boolean) value).add();
        else if (value instanceof Integer)
            return new IntSliderRow(group, key, (Integer) value).add();
        else if (value instanceof String)
            return new StringRow(group, key, (String) value).add();
        else if (value instanceof Enum)
            return new EnumRow<E>(group, key, value).add();
        else
            return new InvalidRow(group, key, value).add();
    }

    public void addRow(ConfigRowList.Row row) { this.addEntry(row); }
    public void addRow(GroupType group, String key, Object value) { this.addEntry(this.getRow(group, key, value)); }

    /* Row Templates */

    // Abstract Entry Row
    public abstract static class AbstractRow<T>
    {
        protected final TweakCache<T> cache;
        protected final GroupType group;
        protected final String key;
        protected final T value;

        protected AbstractRow(GroupType group, String key, T value)
        {
            this.cache = TweakCache.get(group, key);
            this.group = group;
            this.key = key;
            this.value = value;
        }

        protected ConfigRowList.Row create(AbstractWidget controller)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            TweakEntry.Gui.NoTooltip noTooltip = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.NoTooltip.class);

            widgets.add(controller);
            widgets.add(new ResetButton(this.cache, controller));
            widgets.add(new StatusButton(this.cache, controller));
            widgets.add(new TweakTag(this.cache, controller, noTooltip == null));

            if (noTooltip == null)
                widgets.add(new TooltipButton(this.cache, controller));

            return new ConfigRowList.Row(ImmutableList.copyOf(widgets), this.cache);
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
            return new ConfigRowList.Row(ImmutableList.copyOf(new ArrayList<>()), this.cache);
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

            return new ConfigRowList.Row(ImmutableList.copyOf(widgets), null);
        }
    }

    // Single Button Entry
    public record SingleCenteredRow(Screen screen, Component title, Button.OnPress onPress)
    {
        public ConfigRowList.Row add() { return add(120); }
        public ConfigRowList.Row add(int width)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            widgets.add(new Button((this.screen.width / 2) - (width / 2), 0, width, BUTTON_HEIGHT, this.title, this.onPress));

            return new ConfigRowList.Row(ImmutableList.copyOf(widgets), null);
        }
    }

    // Manual Row Entry
    public record ManualRow(List<AbstractWidget> widgets)
    {
        public ConfigRowList.Row add() { return new ConfigRowList.Row(ImmutableList.copyOf(widgets), null); }
    }

    // Subcategory Entry
    public static class CategoryRow
    {
        private ArrayList<ConfigRowList.Row> cache;
        private final Component title;
        private final ConfigRowList list;
        private final Supplier<ArrayList<ConfigRowList.Row>> childrenSupply;
        private final boolean expanded;
        private GroupButton controller;

        public CategoryRow(ConfigRowList list, Component title, Supplier<ArrayList<ConfigRowList.Row>> childrenSupply, boolean isExpanded)
        {
            this.list = list;
            this.title = title;
            this.childrenSupply = childrenSupply;
            this.expanded = isExpanded;
        }

        public CategoryRow(ConfigRowList list, Component title, Supplier<ArrayList<ConfigRowList.Row>> childrenSupply)
        {
            this(list, title, childrenSupply, false);
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
            this.cache = this.childrenSupply.get();

            int header = this.getHeaderIndex();
            for (ConfigRowList.Row row : this.cache)
            {
                this.list.children().add(header, row);
                header++;
            }
        }

        public void collapse()
        {
            for (ConfigRowList.Row cache : this.cache)
            {
                for (ConfigRowList.Row child : this.list.children())
                {
                    if (child.equals(cache))
                    {
                        this.list.removeEntry(child);
                        break;
                    }
                }
            }
        }

        public ConfigRowList.Row add()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller = new GroupButton(this, this.title, this.expanded);
            widgets.add(this.controller);

            return new ConfigRowList.Row(ImmutableList.copyOf(widgets), null);
        }
    }

    /* Row Provider */

    public static class Row extends ContainerObjectSelectionList.Entry<Row>
    {
        /* Instance Fields */

        @Nullable private final TweakCache<?> cache;
        public final List<AbstractWidget> children;
        private float fadeIn = 0F;

        /* Constructor */

        public Row(List<AbstractWidget> list, @Nullable TweakCache<?> cache)
        {
            this.children = list;
            this.cache = cache;
        }

        /* Overrides & Rendering */

        private boolean isBindingRow()
        {
            for (AbstractWidget widget : this.children)
                if (widget instanceof KeyBindButton)
                    return true;
            return false;
        }

        private void renderOnHover(PoseStack poseStack, Screen screen, int top, int height)
        {
            if (this.cache == null && !this.isBindingRow()) return;

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            Matrix4f matrix = poseStack.last().pose();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int alpha = (int) (this.fadeIn * 50);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(matrix, (float) 0, (float) (top + height), 0.0F).color(255, 255, 255, alpha).endVertex();
            buffer.vertex(matrix, (float) screen.width, (float) (top + height), 0.0F).color(255, 255, 255, alpha).endVertex();
            buffer.vertex(matrix, (float) screen.width, (float) (top - 1), 0.0F).color(255, 255, 255, alpha).endVertex();
            buffer.vertex(matrix, (float) 0, (float) (top - 1), 0.0F).color(255, 255, 255, alpha).endVertex();
            tesselator.end();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        private static boolean isRowClipped(Screen screen, AbstractWidget widget)
        {
            return widget.x + widget.getWidth() >= screen.width - TEXT_FROM_END;
        }

        @Override public List<? extends NarratableEntry> narratables() { return this.children; }
        @Override public List<? extends GuiEventListener> children() { return this.children; }
        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            Font font = Minecraft.getInstance().font;
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null) return;

            this.fadeIn = Mth.clamp(this.fadeIn, 0F, 1F);
            if (this.isMouseOver(mouseX, mouseY))
                this.fadeIn += 0.05F;
            else
                this.fadeIn -= 0.05F;
            if (this.fadeIn > 0F)
                this.renderOnHover(poseStack, screen, top, height);

            int diffX = 0;
            for (AbstractWidget widget : this.children)
            {
                if (widget instanceof ResetButton && isRowClipped(screen, widget))
                {
                    int startX = widget.x;
                    while (isRowClipped(screen, widget))
                        widget.x--;

                    diffX = startX - widget.x;
                    break;
                }
            }

            for (AbstractWidget widget : this.children)
                widget.x -= diffX;

            for (AbstractWidget widget : this.children)
            {
                Component title = Component.empty();

                if (this.cache != null)
                {
                    Component translation = Component.translatable(this.cache.getLangKey());
                    title = this.cache.isSavable() ? translation.copy().withStyle(ChatFormatting.ITALIC) : translation.copy().withStyle(ChatFormatting.RESET);

                }
                else if (widget instanceof KeyBindButton)
                {
                    Component translation = Component.translatable(((KeyBindButton) widget).getMapping().getName());
                    title = KeyBindButton.isMappingConflicted(((KeyBindButton) widget).getMapping()) ? translation.copy().withStyle(ChatFormatting.RED) : translation.copy().withStyle(ChatFormatting.RESET);
                }

                Screen.drawString(poseStack, font, title, TEXT_START, top + 6, 0xFFFFFF);

                widget.y = top;
                int cacheX = widget.x;
                int cacheY = widget.y;

                if (widget instanceof EditBox)
                {
                    widget.x -= 1;
                    widget.y += 1;
                }

                widget.render(poseStack, mouseX, mouseY, partialTick);

                if (widget instanceof EditBox)
                {
                    widget.x = cacheX;
                    widget.y = cacheY;
                }
            }
        }
    }
}
