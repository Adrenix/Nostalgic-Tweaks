package mod.adrenix.nostalgic.client.config.gui.widget.group;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A text group renders text in formatted paragraph form. New text row instances will be created when the end of a
 * sentence has been determined it will be rendered off the screen, making the sentence impossible to read.
 */

public class TextGroup extends AbstractWidget
{
    /* Rendering Constants */

    public static final int LINE_HEIGHT = 13;

    /* Widget Fields */

    private final Component text;
    private final ConfigRowList list;
    private final ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
    private List<Component> lines = new ArrayList<>();

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
     * width of the current screen.
     *
     * @param text The paragraph to render.
     */
    public TextGroup(Component text)
    {
        super(ConfigRowList.TEXT_START, 0, getListWidth(), 12, Component.empty());

        this.list = ConfigRowList.getInstance();
        this.text = text;
    }

    /* Methods */

    /**
     * Generate the rows required to fully display the paragraph for this text group.
     * @return An array list of config row list row instances.
     */
    public ArrayList<ConfigRowList.Row> getRows()
    {
        this.rows.clear();

        this.width = this.list.screen.width - ConfigRowList.getStartX() - ConfigRowList.TEXT_FROM_END;
        this.lines = ModUtil.Wrap.tooltip(this.text, (int) (this.width / 5.5F));
        int rowsNeeded = (int) Math.ceil((double) (lines.size()) / 2);

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
     * @param poseStack The current pose stack.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        ArrayList<ConfigRowList.Row> rows = this.getRows();
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
            this.getRows().forEach((row) -> this.list.children().add(row));
    }

    /* Required Widget Overrides */

    @Override
    public void updateNarration(NarrationElementOutput ignored) { }

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
         * @param poseStack The current pose stack.
         * @param mouseX The x-position of the mouse.
         * @param mouseY The y-position of the mouse.
         * @param partialTick The change in frame time.
         */
        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            if (!this.first)
                return;

            int startY = this.y - 1;

            for (Component line : TextGroup.this.lines)
            {
                Screen.drawString(poseStack, Minecraft.getInstance().font, line, this.x, startY, 0xFFFFFF);
                startY += LINE_HEIGHT;
            }
        }

        /* Required Widget Overrides */

        @Override
        public void updateNarration(NarrationElementOutput ignored) { }
    }
}
