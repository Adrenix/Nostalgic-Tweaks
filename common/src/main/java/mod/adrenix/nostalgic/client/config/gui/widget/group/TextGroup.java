package mod.adrenix.nostalgic.client.config.gui.widget.group;

import com.google.common.collect.ImmutableList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

/**
 * A text group renders text in formatted paragraph form. New text row instances will be created when the end of a
 * sentence has been determined it will be rendered off the screen, making the sentence impossible to read.
 */

public class TextGroup extends AbstractWidget
{
    /* Aligning Options */

    /* Widget Fields */

    private MultiLineLabel label;
    private final TextAlign align;
    private final Component text;
    private final ConfigRowList list;
    private final ArrayList<ConfigRowList.Row> rows;

    /* Constructor */

    /**
     * Constructor helper that calculates maximum width for this text group.
     * @return A widget width for a group text.
     */
    private static int getListWidth()
    {
        return ConfigRowList.getInstance().screen.width - ConfigRowList.TEXT_START - ConfigRowList.TEXT_FROM_END;
    }

    /**
     * Create a new text group widget that will automatically generate rows based on translation width and the maximum
     * width of the container the text rows reside in.
     *
     * @param text The paragraph to render.
     * @param align The alignment type for this group.
     */
    public TextGroup(Component text, TextAlign align)
    {
        super(ConfigRowList.TEXT_START, 0, getListWidth(), 12, Component.empty());

        this.list = ConfigRowList.getInstance();
        this.text = text;
        this.align = align;
        this.rows = new ArrayList<>();
        this.label = MultiLineLabel.EMPTY;
    }

    /**
     * Create a new text group widget that will be aligned to the left. Rows will automatically be generated based on
     * translation widget and the maximum width of the container the text rows reside in.
     *
     * @param text The paragraph to render.
     */
    public TextGroup(Component text) { this(text, TextAlign.LEFT); }

    /* Methods */

    /**
     * @return The maximum width allocated to text rendering per line.
     */
    private int getTextWidth()
    {
        return this.list.getWidthMinusScrollbar() - ConfigRowList.currentIndent - ConfigRowList.TEXT_FROM_END;
    }

    /**
     * Generate the rows required to fully display the paragraph for this text group.
     * @return An array list of config row list row instances.
     */
    public ArrayList<ConfigRowList.Row> generate()
    {
        this.rows.clear();

        // Fixes a color continuation on next line issue when using a multi line label
        String fix = this.text.getString().replaceAll("§r", "§f");

        this.width = this.getTextWidth();
        this.label = MultiLineLabel.create(Minecraft.getInstance().font, Component.literal(fix), this.width);

        int rowsNeeded = (int) Math.ceil((double) (label.getLineCount()) / 2);

        for (int i = 0; i < rowsNeeded; i++)
            this.rows.add(new ConfigRowList.Row(ImmutableList.of(new TextRow(i == 0)), null));

        this.height = this.rows.size() * 22;

        return this.rows;
    }

    /**
     * Handler method for when the mouse clicks on a text group widget.
     * Always returns false to prevent a clicking sound from playing when this widget is left-clicked.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    /**
     * Handler method for rendering a text group widget.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        ArrayList<ConfigRowList.Row> rows = this.generate();
        ArrayList<Integer> found = new ArrayList<>();

        for (ConfigRowList.Row listChild : this.list.children())
        {
            for (ConfigRowList.Row rowChild : rows)
            {
                if (listChild.equals(rowChild))
                    found.add(1);
            }
        }

        if (found.size() != rows.size())
            this.generate().forEach((row) -> this.list.children().add(row));
    }

    /* Required Widget Overrides */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) { }

    /**
     * This class defines the properties of a text row widget that will be used in a config row list row.
     * Special rendering instructions are required for text row widgets.
     */

    public class TextRow extends AbstractWidget
    {
        /* Fields */

        /**
         * This field tracks whether this widget is the first widget within a collection of text row widgets that is a
         * part of a text group. This is needed for rendering logic.
         */
        private final boolean first;

        /* Constructor */

        /**
         * Create a new text row instance for a text group instance.
         * @param first Whether this is the first widget within a text group.
         */
        public TextRow(boolean first)
        {
            super(ConfigRowList.TEXT_START, 0, TextGroup.this.width, ConfigRowList.BUTTON_HEIGHT, Component.empty());

            this.first = first;
            this.active = false;

            if (this.first)
                this.height = TextRow.this.getHeight();
        }

        /* Methods */

        /**
         * A getter method that checks whether this text row is the first within a text group.
         * @return The state of the {@link TextRow#first} field flag.
         */
        public boolean isFirst() { return this.first; }

        /**
         * Handler method for when the mouse clicks on a text row widget.
         * Always returns false to prevent a clicking sound from playing when this widget is left-clicked.
         *
         * @param mouseX The x-position of the mouse.
         * @param mouseY The y-position of the mouse.
         * @param button The mouse button that was clicked.
         * @return Whether this method handled the mouse click event.
         */
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

        /**
         * Handler method for rendering a text row widget.
         * @param graphics The current GuiGraphics object.
         * @param mouseX The x-position of the mouse.
         * @param mouseY The y-position of the mouse.
         * @param partialTick The change in frame time.
         */
        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if (!this.first)
                return;

            Font font = Minecraft.getInstance().font;
            MultiLineLabel label = TextGroup.this.label;

            switch (TextGroup.this.align)
            {
                case LEFT -> label.renderLeftAligned(graphics, ConfigRowList.getStartX(), this.getY() - 1, font.lineHeight + 4, 0xFFFFFF);
                case CENTER -> label.renderCentered(graphics, TextGroup.this.list.getRowWidth() / 2, this.getY() - 1);
            }
        }

        /* Required Widget Overrides */

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }
}
