package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.CustomSwings;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.gui.screen.CustomizeScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ItemButton;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    protected static class Row extends ElementListWidget.Entry<Row>
    {
        private final List<ClickableWidget> children;
        private final Map.Entry<String, Integer> entry;

        private Row(List<ClickableWidget> list, Map.Entry<String, Integer> entry)
        {
            this.children = list;
            this.entry = entry;
        }

        /* Create Item Row */

        public static CustomizedRowList.Row item(CustomizeScreen screen, Map.Entry<String, Integer> entry)
        {
            List<ClickableWidget> widgets = new ArrayList<>();
            ButtonWidget range = Widgets.createRange(screen, entry);
            ItemButton item = Widgets.createImage(screen, entry);
            GenericSlider slider = Widgets.createSlider(screen, entry);
            ButtonWidget remove = Widgets.createRemove(screen, entry, widgets);
            ButtonWidget undo = Widgets.createUndo(screen, entry, widgets);
            ButtonWidget reset = Widgets.createReset(screen, entry, slider);

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

        public static void disableWidgets(List<ClickableWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = false);
            widgets.get(widgets.size() - 1).active = true;
        }

        public static void enableWidgets(List<ClickableWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = true);
            widgets.get(widgets.size() - 1).active = false;
        }

        public static void delete(Map.Entry<String, Integer> entry, List<ClickableWidget> widgets)
        {
            deleted.add(entry);
            disableWidgets(widgets);
        }

        public static void undo(Map.Entry<String, Integer> entry, List<ClickableWidget> widgets)
        {
            deleted.remove(entry);
            enableWidgets(widgets);
        }

        private void renderWidget(ClickableWidget widget, MatrixStack stack, int mouseX, int mouseY, float ticks)
        {
            if (widget instanceof GenericSlider)
                ((GenericSlider) widget).updateMessage();
            else if (widget instanceof ButtonWidget)
            {
                String title = Formatting.strip(widget.getMessage().getString());
                if (title != null && title.equals(Widgets.REMOVE))
                    widget.setMessage(Text.literal(Widgets.REMOVE).withStyle(widget.active ? Formatting.DARK_RED : Formatting.GRAY).withStyle(Formatting.BOLD));
                else if (title != null && title.equals(Widgets.UNDO))
                    widget.setMessage(Text.literal(Widgets.UNDO).withStyle(widget.active ? Formatting.RED : Formatting.GRAY));
                else if (title != null && title.equals(Text.translatable(NostalgicLang.Cloth.RESET).getString()))
                    widget.active = !CustomizedRowList.deleted.contains(entry) && this.entry.getValue() != DefaultConfig.Swing.OLD_SPEED;
            }

            widget.render(stack, mouseX, mouseY, ticks);
        }

        /* Overrides */

        @Override public List<? extends Selectable> selectableChildren() { return this.children; }
        @Override public List<? extends Element> children() { return this.children; }
        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            for (ClickableWidget widget : this.children)
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
                    ((ItemButton) widget).screen.getItemRenderer().renderGuiItemIcon(isValid ? item.getDefaultStack() : new ItemStack(Items.BARRIER), startX, startY);
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

            public static ButtonWidget createRange(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                boolean isAdded = CustomizedRowList.added != null && CustomizedRowList.added.getKey().equals(entry.getKey());
                return new ButtonWidget(
                    screen.width / 2 - 155,
                    0,
                    20,
                    20,
                    Text.literal("#").withStyle(isAdded ? Formatting.YELLOW : Formatting.RESET),
                    (ignored) -> {},
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderComponentTooltip(stack, CustomSwings.rangeTooltip(), mouseX, mouseY);
                    }
                );
            }

            public static ButtonWidget createRemove(CustomizeScreen screen, Map.Entry<String, Integer> entry, List<ClickableWidget> widgets)
            {
                return new ButtonWidget(
                    screen.width / 2 + 116,
                    0,
                    20,
                    20,
                    Text.literal(REMOVE),
                    (button) -> CustomizedRowList.Row.delete(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.removeTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            public static ButtonWidget createUndo(CustomizeScreen screen, Map.Entry<String, Integer> entry, List<ClickableWidget> widgets)
            {
                return new ButtonWidget(
                    screen.width / 2 + 137,
                    0,
                    20,
                    20,
                    Text.literal(UNDO),
                    (button) -> CustomizedRowList.Row.undo(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.undoTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            public static ButtonWidget createReset(CustomizeScreen screen, Map.Entry<String, Integer> entry, GenericSlider slider)
            {
                Text title = Text.translatable(NostalgicLang.Cloth.RESET);
                return new ButtonWidget(
                    screen.width / 2 + 158,
                    0,
                    screen.getMinecraft().textRenderer.getWidth(title) + 6,
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
