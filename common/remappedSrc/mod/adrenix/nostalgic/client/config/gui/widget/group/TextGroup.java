package mod.adrenix.nostalgic.client.config.gui.widget.group;

import com.google.common.collect.ImmutableList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class TextGroup extends ClickableWidget
{
    public static final int LINE_HEIGHT = 13;
    public static final int TEXT_START = ConfigRowList.TEXT_START;
    protected final Text text;
    protected final ConfigRowList list;
    protected final ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
    protected List<Text> lines = new ArrayList<>();

    public TextGroup(ConfigRowList list, Text text)
    {
        super(TEXT_START, 0, list.screen.width - TEXT_START - ConfigRowList.TEXT_FROM_END, 12, Text.empty());

        this.list = list;
        this.text = text;
    }

    public ArrayList<ConfigRowList.Row> getRows()
    {
        this.rows.clear();

        this.width = this.list.screen.width - ConfigRowList.getStartX() - ConfigRowList.TEXT_FROM_END;
        this.lines = NostalgicUtil.Wrap.tooltip(this.text, (int) (this.width / 5.5F));
        int rowsNeeded = (int) Math.ceil((double) (lines.size()) / 2);

        for (int i = 0; i < rowsNeeded; i++)
            this.rows.add(new ConfigRowList.Row(ImmutableList.of(new TextRow(i == 0)), null));
        this.height = this.rows.size() * 22;

        return this.rows;
    }

    @Override // Prevents the clicking sound from playing when left-clicking a text group widget
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    @Override public void appendNarrations(NarrationMessageBuilder ignored) {}
    @Override public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
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

    public class TextRow extends ClickableWidget
    {
        private final boolean first;

        public TextRow(boolean first)
        {
            super(ConfigRowList.TEXT_START, 0, TextGroup.this.width, ConfigRowList.BUTTON_HEIGHT, Text.empty());

            this.first = first;
            this.active = false;

            if (this.first)
                this.height = TextRow.this.getHeight();
        }

        public boolean isFirst() { return this.first; }

        // Prevents the clicking sound from playing when left-clicking a text row widget
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            return false;
        }

        @Override
        public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            if (!this.first) return;

            int startY = this.y - 1;

            for (Text line : TextGroup.this.lines)
            {
                Screen.drawTextWithShadow(poseStack, MinecraftClient.getInstance().textRenderer, line, this.x, startY, 0xFFFFFF);
                startY += LINE_HEIGHT;
            }
        }

        @Override public void appendNarrations(NarrationMessageBuilder ignored) {}
    }
}
