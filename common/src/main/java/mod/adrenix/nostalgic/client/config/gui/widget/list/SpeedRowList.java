package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.CustomSwings;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.gui.screen.SwingScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ItemButton;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.util.common.LangUtil;
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

/**
 * Creates a list of rows that have widgets that control an individual custom swing speed value. These rows are generated
 * based on what is currently saved in the config file.
 */

public class SpeedRowList extends AbstractRowList<SpeedRowList.Row>
{
    /* Static Fields */

    /**
     * This list contains all entries that were deleted by the user. This is used so that swing speed screen knows when
     * values were deleted and what restore if the user cancels those deletions.
     */
    public static final List<Map.Entry<String, Integer>> DELETED_ROWS = new ArrayList<>();

    /**
     * Keeps track of the most recent entry added by the user. This is used so that the most recently added entry is
     * always kept on top during row sorting. Additionally, this entry is highlighted so the user can quickly find the
     * row that was just added by the row list.
     */
    public static Map.Entry<String, Integer> added;

    /* Fields */

    private final SwingScreen screen;

    /* Constructor */

    /**
     * Create a new custom swing speed row list.
     * @param screen The parent swing screen.
     * @param width The width of this row list.
     * @param height The height of this row list.
     * @param startY The starting y-position of this row list.
     * @param endY The ending y-position of this row list.
     * @param rowHeight The height of individual rows.
     */
    public SpeedRowList(SwingScreen screen, int width, int height, int startY, int endY, int rowHeight)
    {
        super(width, height, startY, endY, rowHeight);

        this.screen = screen;
    }

    /* List Utilities */

    /**
     * Add a new row entry to the speed row list.
     * @param entry The entry to add.
     */
    public void addRow(Map.Entry<String, Integer> entry) { this.addEntry(SpeedRowList.Row.create(screen, entry)); }

    /* Overrides */

    /**
     * Handler method for when the mouse is clicked.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        if (clicked)
            this.screen.setSearchBoxFocus(false);

        return clicked;
    }

    /* Speed Row Definition */

    /**
     * This static class provides widgets for rows and the logic that is associated with those widgets.
     * These rows are what is displayed by the parent speed row list.
     */
    protected static class Row extends ContainerObjectSelectionList.Entry<Row>
    {
        /* Fields */

        private final List<AbstractWidget> children;
        private final Map.Entry<String, Integer> entry;

        /* Constructor */

        /**
         * Create a new swing speed item control row instance.
         * @param list The parent speed row list.
         * @param entry The config item entry that is associated with this row.
         */
        private Row(List<AbstractWidget> list, Map.Entry<String, Integer> entry)
        {
            this.children = list;
            this.entry = entry;
        }

        /**
         * Create a new row instance based on a configuration entry.
         * @param screen The swing screen parent instance.
         * @param entry A config item entry.
         * @return A new speed row list row instance.
         */
        public static SpeedRowList.Row create(SwingScreen screen, Map.Entry<String, Integer> entry)
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

            SpeedRowList.DELETED_ROWS.forEach((deleted) ->
            {
                if (deleted.getKey().equals(entry.getKey()))
                    disableWidgets(widgets);
            });

            return new SpeedRowList.Row(ImmutableList.copyOf(widgets), entry);
        }

        /* Widget Logic */

        /**
         * Disable all widgets within a row.
         * The undo widget is the only widget not disabled.
         *
         * @param widgets The list of widgets to disable.
         */
        public static void disableWidgets(List<AbstractWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = false);
            widgets.get(widgets.size() - 1).active = true;
        }

        /**
         * Enable all widgets within a row.
         * The undo widget is the only widget not enabled.
         *
         * @param widgets The list of widgets to enable.
         */
        public static void enableWidgets(List<AbstractWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = true);
            widgets.get(widgets.size() - 1).active = false;
        }

        /**
         * Delete a row entry. The row will still be visible and can be later restored.
         * The deletion is not saved until the changes are confirmed by the parent screen.
         *
         * @param entry The entry to add to the deleted row list.
         * @param widgets The widgets to disable.
         */
        public static void delete(Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
        {
            DELETED_ROWS.add(entry);
            disableWidgets(widgets);
        }

        /**
         * Undo the deletion of a row entry. The row will have its widgets enabled.
         * @param entry The entry to remove from the deleted row list.
         * @param widgets The widgets to enable.
         */
        public static void undo(Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
        {
            DELETED_ROWS.remove(entry);
            enableWidgets(widgets);
        }

        /**
         * Render a widget.
         * @param widget The widget to render.
         * @param poseStack The current pose stack.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         * @param partialTick The change in game frame time.
         */
        private void renderWidget(AbstractWidget widget, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
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
                else if (title != null && title.equals(Component.translatable(LangUtil.Gui.BUTTON_RESET).getString()))
                    widget.active = !SpeedRowList.DELETED_ROWS.contains(entry) && this.entry.getValue() != DefaultConfig.Swing.OLD_SPEED;
            }

            widget.render(poseStack, mouseX, mouseY, partialTick);
        }

        /* Overrides */

        /**
         * Handler method for rendering a row entry.
         * @param poseStack The current pose stack.
         * @param index Unused parameter.
         * @param top The top of the row entry.
         * @param left The starting x-position of the row entry.
         * @param width The width of the row entry.
         * @param height The height of the row entry.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         * @param isMouseOver Whether the mouse is over this row entry.
         * @param partialTick The change in game frame time.
         */
        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
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
                    this.renderWidget(widget, poseStack, mouseX, mouseY, partialTick);
            }
        }

        /* Required Overrides */

        @Override public List<? extends NarratableEntry> narratables() { return this.children; }
        @Override public List<? extends GuiEventListener> children() { return this.children; }

        /**
         * This class provides widgets for row entries to use.
         * Widget rendering and state management is handled by the speed row class.
         */

        private static class Widgets
        {
            /* Static Fields */

            public static final String REMOVE = "\u274c";
            public static final String UNDO = "\u2764";

            /* Static Methods */

            /**
             * Creates a new item button that displays the item sprite at the start of the row entry.
             * @param screen The swing screen parent instance.
             * @param entry The config entry associated with this row.
             * @return A new item button instance.
             */
            public static ItemButton createImage(SwingScreen screen, Map.Entry<String, Integer> entry)
            {
                return new ItemButton(screen, entry, screen.width / 2 - 134);
            }

            /**
             * Creates a new generic slider instance that controls the value of the swing speed.
             * @param screen The swing screen parent instance.
             * @param entry The config entry associated with this row.
             * @return A new generic slider instance.
             */
            public static GenericSlider createSlider(SwingScreen screen, Map.Entry<String, Integer> entry)
            {
                return new GenericSlider
                (
                    entry::setValue,
                    entry::getValue,
                    () -> CustomSwings.getLocalizedItem(entry),
                    screen.width / 2 - 113,
                    0,
                    228,
                    20
                );
            }

            /**
             * Creates a new button that only displays a tooltip when hovered providing information about swing speeds.
             * @param screen The swing screen parent instance.
             * @param entry The config entry associated with this row.
             * @return A new button instance.
             */
            public static Button createRange(SwingScreen screen, Map.Entry<String, Integer> entry)
            {
                boolean isAdded = SpeedRowList.added != null && SpeedRowList.added.getKey().equals(entry.getKey());

                return new Button
                (
                    screen.width / 2 - 155,
                    0,
                    20,
                    20,
                    Component.literal("#").withStyle(isAdded ? ChatFormatting.YELLOW : ChatFormatting.RESET),
                    (ignored) -> {},
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderComponentTooltip(stack, CustomSwings.rangeTooltip(), mouseX, mouseY);
                    }
                );
            }

            /**
             * Creates a new button that allows for this entry to be removed.
             * @param screen The swing screen parent instance.
             * @param entry The config entry associated with this row.
             * @param widgets A list of widgets to disable.
             * @return A new button instance.
             */
            public static Button createRemove(SwingScreen screen, Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
            {
                return new Button
                (
                    screen.width / 2 + 116,
                    0,
                    20,
                    20,
                    Component.literal(REMOVE),
                    (button) -> SpeedRowList.Row.delete(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.removeTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            /**
             * Creates a new button that allows for this entry's deletion to be undone.
             * @param screen The swing screen parent instance.
             * @param entry The config entry associated with this row.
             * @param widgets A list of widgets to enable.
             * @return A new button instance.
             */
            public static Button createUndo(SwingScreen screen, Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
            {
                return new Button
                (
                    screen.width / 2 + 137,
                    0,
                    20,
                    20,
                    Component.literal(UNDO),
                    (button) -> SpeedRowList.Row.undo(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.undoTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            /**
             * Creates a new button that resets the value of this entry to the default old swing speed.
             * @param screen The swing screen parent instance.
             * @param entry The config entry associated with this row.
             * @param slider The slider associated with this entry so that its value can be reset.
             * @return A new button instance.
             */
            public static Button createReset(SwingScreen screen, Map.Entry<String, Integer> entry, GenericSlider slider)
            {
                Component title = Component.translatable(LangUtil.Gui.BUTTON_RESET);

                return new Button
                (
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
