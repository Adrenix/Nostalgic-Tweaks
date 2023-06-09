package mod.adrenix.nostalgic.client.config.gui.widget.group;

import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowBuild;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The radio group is an alternative widget type to the cycle button for enumerations. This widget is useful for when
 * all options should be visible to the user to choose from.
 *
 * @param <E> The enumeration type associated with this radio group.
 */

public class RadioGroup<E extends Enum<E>> extends AbstractWidget
{
    /* Fields */

    private ArrayList<ConfigRowList.Row> cache;
    private final ConfigRowList list;
    private final Class<E> radios;
    private final E defaultValue;
    private final OnPress<E> onPress;
    private final Supplier<E> currentValue;
    private final Function<E, Component> label;

    /* Interface */

    /**
     * In-class interface that provides logic for when a radio is pressed.
     * @param <E> The enumeration type associated with this radio group.
     */
    public interface OnPress<E extends Enum<E>>
    {
        /**
         * Provide instructions for when a radio is pressed.
         * @param selected The new value that was selected by the user.
         */
        void press(E selected);
    }

    /* Constructor */

    /**
     * Create a new radio group instance.
     * @param radios The enumeration class associated with this radio group.
     * @param defaultValue The default value of this radio group.
     * @param currentValue The current value of this radio group.
     * @param onPress Instructions for when a radio is clicked.
     * @param label A function that accepts an enumeration value and returns a translation to display next to the radio.
     */
    public RadioGroup(Class<E> radios, E defaultValue, Supplier<E> currentValue, OnPress<E> onPress, Function<E, Component> label)
    {
        super(ConfigRowList.TEXT_START, 0, 0, ConfigRowList.BUTTON_HEIGHT, Component.empty());

        this.list = ConfigRowList.getInstance();
        this.radios = radios;
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
        this.onPress = onPress;
        this.label = label;
    }

    /* Methods */

    /**
     * Generate a new manual row to a row list with the provided radio option.
     * @param list An array list of config row list rows.
     * @param option The radio option to generate a row from.
     */
    private void generateRadioRow(ArrayList<ConfigRowList.Row> list, E option)
    {
        Radio<E> radio = new Radio<>(option, this.defaultValue, this.currentValue, this.onPress, this.label);
        ConfigRowBuild.ManualRow row = new ConfigRowBuild.ManualRow(List.of(radio));

        list.add(row.generate());
    }

    /**
     * Get a list of rows that can be added to a config row list.
     * @return An array list of config row instances.
     */
    public ArrayList<ConfigRowList.Row> getRows()
    {
        if (this.cache != null)
            return this.cache;

        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
        this.cache = rows;

        EnumSet.allOf(this.radios).forEach((option) -> this.generateRadioRow(rows, option));

        return rows;
    }

    /**
     * Handler method for rendering a radio group.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        ArrayList<ConfigRowList.Row> rows = this.getRows();
        ArrayList<Integer> found = new ArrayList<>();

        for (ConfigRowList.Row listChild : this.list.children())
        {
            for (ConfigRowList.Row enumChild : rows)
            {
                if (listChild.equals(enumChild))
                    found.add(1);
            }
        }

        if (found.size() != rows.size())
            this.getRows().forEach((row) -> this.list.children().add(row));
    }

    /* Required Widget Overrides */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) { }

    /* Radio */

    /**
     * This class defines an individual radio widget. The class handles clicking logic and rendering instructions of
     * the radio itself. Additionally, helper methods are provided that determine if a radio is selected or is the
     * default value.
     *
     * @param <E> The enumeration type associated with this radio.
     */

    private static class Radio<E extends Enum<E>> extends AbstractWidget
    {
        /* Fields */

        private final E value;
        private final E defaultValue;
        private final Supplier<E> currentValue;
        private final Function<E, Component> label;
        private final OnPress<E> onPress;

        /* Constructor */

        /**
         * Create a new individual radio instance.
         * @param value The value to use for this radio instance.
         * @param defaultValue The default value for the radio group.
         * @param currentValue The current value selected by the radio group.
         * @param onPress Instructions for when a radio is clicked.
         * @param label A function that accepts an enumeration value and returns a translation to display next to the radio.
         */
        public Radio(E value, E defaultValue, Supplier<E> currentValue, OnPress<E> onPress, Function<E, Component> label)
        {
            super(ConfigRowList.TEXT_START, 0, ConfigRowList.BUTTON_HEIGHT, ConfigRowList.BUTTON_HEIGHT, Component.empty());

            this.value = value;
            this.defaultValue = defaultValue;
            this.currentValue = currentValue;
            this.onPress = onPress;
            this.label = label;
        }

        /* Methods */

        /**
         * Check if this radio instance has been selected within the radio group.
         * @return Whether this instance is the selected instance.
         */
        public boolean isSelected() { return this.value.equals(this.currentValue.get()); }

        /**
         * Checks if this radio instance is the default selection for the radio group.
         * @return Whether this instance is the default radio option.
         */
        public boolean isDefault() { return !this.isSelected() && this.value.equals(this.defaultValue); }

        /**
         * Handler method for when the mouse is clicked on this radio.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         */
        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.onPress.press(this.value);
            super.onClick(mouseX, mouseY);
        }

        /**
         * Handler method for individual radio rendering.
         * @param graphics The current GuiGraphics object.
         * @param mouseX The current x-position of the mouse.
         * @param mouseY The current y-position of the mouse.
         * @param partialTick The change in game frame time.
         */
        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            Minecraft minecraft = Minecraft.getInstance();
            Screen screen = minecraft.screen;

            if (screen == null)
                return;

            int uOffset = 0;
            int vOffset = 103;
            int uWidth = 20;
            int vHeight = 20;

            if (this.isMouseOver(mouseX, mouseY))
            {
                uOffset = 20;
                vOffset = this.isSelected() ? 63 : vOffset;
            }
            else if (this.isSelected())
                vOffset = 63;

            graphics.blit(TextureLocation.WIDGETS, this.getX(), this.getY(), uOffset, vOffset, uWidth, vHeight);

            Component defaultText = Component.literal(this.isDefault() ? String.format(" (%s)", Component.translatable(LangUtil.Gui.DEFAULT).getString()) : "");
            Component optionText = Component.literal(this.label.apply(this.value).getString() + defaultText.getString());

            graphics.drawString(Minecraft.getInstance().font, optionText, this.getX() + 24, this.getY() + (this.height - 8) / 2, 0xFFFFFF);
        }

        /* Required Widget Overrides */

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }
}
