package mod.adrenix.nostalgic.client.config.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.CustomSwings;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.gui.screen.CustomizeScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ItemButton;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomizedRowList extends AbstractRowList<CustomizedRowList.Row>
{
    public static final List<Map.Entry<String, Integer>> deleted = new ArrayList<>();
    public static Map.Entry<String, Integer> added;
    private final CustomizeScreen screen;

    public CustomizedRowList(CustomizeScreen screen, int width, int height, int y0, int y1, int itemHeight)
    {
        super(screen.getMinecraft(), width, height, y0, y1, itemHeight);
        this.screen = screen;
    }

    /* Row List Utilities */

    public void addItem(Map.Entry<String, Integer> entry) { this.addEntry(CustomizedRowList.Row.item(screen, entry)); }

    /* Overrides */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        if (clicked)
            this.screen.setSuggestionFocus(false);
        return clicked;
    }

    /* Row Widgets for Customized Swing */

    protected static class Row extends ContainerObjectSelectionList.Entry<Row>
    {
        private final List<AbstractWidget> children;
        private final Map.Entry<String, Integer> entry;

        private Row(List<AbstractWidget> list, Map.Entry<String, Integer> entry)
        {
            this.children = list;
            this.entry = entry;
        }

        /* Create Item Row */

        public static CustomizedRowList.Row item(CustomizeScreen screen, Map.Entry<String, Integer> entry)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            Button range = Widgets.createRange(screen, entry);
            ItemButton item = Widgets.createImage(screen, entry);
            GenericSlider slider = Widgets.createSlider(screen, entry);
            Button remove = Widgets.createRemove(screen, entry, widgets);
            Button undo = Widgets.createUndo(screen, entry, widgets);
            Button reset = Widgets.createReset(screen, entry, slider);

            undo.active = false;
            reset.active = entry.getValue() != DefaultConfig.Swing.OLD_SPEED;

            widgets.add(range);
            widgets.add(item);
            widgets.add(slider);
            widgets.add(remove);
            widgets.add(reset);
            widgets.add(undo);

            CustomizedRowList.deleted.forEach((deleted) -> {
                if (deleted.getKey().equals(entry.getKey()))
                    disableWidgets(widgets);
            });

            return new CustomizedRowList.Row(ImmutableList.copyOf(widgets), entry);
        }

        /* Row Management */

        public static void disableWidgets(List<AbstractWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = false);
            widgets.get(widgets.size() - 1).active = true;
        }

        public static void enableWidgets(List<AbstractWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = true);
            widgets.get(widgets.size() - 1).active = false;
        }

        public static void delete(Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
        {
            deleted.add(entry);
            disableWidgets(widgets);
        }

        public static void undo(Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
        {
            deleted.remove(entry);
            enableWidgets(widgets);
        }

        private void renderWidget(AbstractWidget widget, PoseStack stack, int mouseX, int mouseY, float ticks)
        {
            if (widget instanceof GenericSlider)
                ((GenericSlider) widget).updateMessage();
            else if (widget instanceof Button)
            {
                String title = ChatFormatting.stripFormatting(widget.getMessage().getString());
                if (title != null && title.equals(Widgets.REMOVE))
                    widget.setMessage(Component.literal(Widgets.REMOVE).withStyle(widget.active ? ChatFormatting.DARK_RED : ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                else if (title != null && title.equals(Widgets.UNDO))
                    widget.setMessage(Component.literal(Widgets.UNDO).withStyle(widget.active ? ChatFormatting.RED : ChatFormatting.GRAY));
                else if (title != null && title.equals(Component.translatable(NostalgicLang.Cloth.RESET).getString()))
                    widget.active = !CustomizedRowList.deleted.contains(entry) && this.entry.getValue() != DefaultConfig.Swing.OLD_SPEED;
            }

            widget.render(stack, mouseX, mouseY, ticks);
        }

        /* Overrides */

        @Override public List<? extends NarratableEntry> narratables() { return this.children; }
        @Override public List<? extends GuiEventListener> children() { return this.children; }
        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            for (AbstractWidget widget : this.children)
            {
                widget.y = top;

                if (widget instanceof ItemButton)
                {
                    Item item = CustomSwings.getItem(((ItemButton) widget).entry);
                    int startX = widget.x + 2;
                    int startY = widget.y + 1;

                    if (item instanceof BlockItem)
                        startY = widget.y + 2;

                    boolean isValid = CustomSwings.isValidEntry(item, ((ItemButton) widget).entry);
                    ((ItemButton) widget).screen.getItemRenderer().renderGuiItem(isValid ? item.getDefaultInstance() : new ItemStack(Items.BARRIER), startX, startY);
                }
                else
                    this.renderWidget(widget, stack, mouseX, mouseY, partialTick);
            }
        }

        /* Widgets */
        private static class Widgets
        {
            public static final String REMOVE = "\u274c";
            public static final String UNDO = "\u2764";

            public static ItemButton createImage(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                return new ItemButton(screen, entry, screen.width / 2 - 134, 0, 20, 20);
            }

            public static GenericSlider createSlider(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                return new GenericSlider(
                    entry::setValue,
                    entry::getValue,
                    () -> CustomSwings.getLocalizedItem(entry),
                    screen.width / 2 - 113,
                    0,
                    228,
                    20
                );
            }

            public static Button createRange(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                boolean isAdded = CustomizedRowList.added != null && CustomizedRowList.added.getKey().equals(entry.getKey());
                return new Button(
                    screen.width / 2 - 155,
                    0,
                    20,
                    20,
                    Component.translatable("#").withStyle(isAdded ? ChatFormatting.YELLOW : ChatFormatting.RESET),
                    (ignored) -> {},
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderComponentTooltip(stack, CustomSwings.rangeTooltip(), mouseX, mouseY);
                    }
                );
            }

            public static Button createRemove(CustomizeScreen screen, Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
            {
                return new Button(
                    screen.width / 2 + 116,
                    0,
                    20,
                    20,
                    Component.translatable(REMOVE),
                    (button) -> CustomizedRowList.Row.delete(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.removeTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            public static Button createUndo(CustomizeScreen screen, Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
            {
                return new Button(
                    screen.width / 2 + 137,
                    0,
                    20,
                    20,
                    Component.translatable(UNDO),
                    (button) -> CustomizedRowList.Row.undo(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.undoTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            public static Button createReset(CustomizeScreen screen, Map.Entry<String, Integer> entry, GenericSlider slider)
            {
                Component title = Component.translatable(NostalgicLang.Cloth.RESET);
                return new Button(
                    screen.width / 2 + 158,
                    0,
                    screen.getMinecraft().font.width(title) + 6,
                    20,
                    title,
                    (button) ->
                    {
                        entry.setValue(DefaultConfig.Swing.OLD_SPEED);
                        slider.setValue(DefaultConfig.Swing.OLD_SPEED);
                    },
                    (button, stack, mouseX, mouseY) -> button.active = entry.getValue() != DefaultConfig.Swing.OLD_SPEED
                );
            }
        }
    }
}
