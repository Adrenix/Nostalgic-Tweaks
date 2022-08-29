package mod.adrenix.nostalgic.client.config.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TextGroup extends AbstractWidget
{
    public static final int LINE_HEIGHT = 13;
    protected final Component text;
    protected final ConfigRowList list;
    protected final ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
    protected List<Component> lines = new ArrayList<>();

    public TextGroup(ConfigRowList list, Component text)
    {
        super(ConfigRowList.TEXT_START, 0, list.screen.width - ConfigRowList.TEXT_START - ConfigRowList.TEXT_FROM_END, 12, Component.empty());

        this.list = list;
        this.text = text;
    }

    public ArrayList<ConfigRowList.Row> getRows()
    {
        this.rows.clear();

        this.lines = NostalgicUtil.Wrap.tooltips(this.text, (this.width / 5) - 3);
        int rowsNeeded = (int) Math.ceil((double) (lines.size()) / 2);

        for (int i = 0; i < rowsNeeded; i++)
            this.rows.add(new ConfigRowList.Row(ImmutableList.of(new TextRow(i == 0)), null));
        this.height = this.rows.size() * 22;

        return this.rows;
    }

    @Override public void updateNarration(NarrationElementOutput ignored) {}
    @Override public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
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

    public class TextRow extends AbstractWidget
    {
        private final boolean first;

        public TextRow(boolean first)
        {
            super(ConfigRowList.TEXT_START, 0, TextGroup.this.width, ConfigRowList.BUTTON_HEIGHT, Component.empty());

            this.first = first;
            this.active = false;

            if (this.first)
                this.height = TextRow.this.getHeight();
        }

        public boolean isFirst() { return this.first; }

        @Override public void updateNarration(NarrationElementOutput ignored) {}
        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            if (!this.first) return;

            int startY = this.y - 1;

            for (Component line : TextGroup.this.lines)
            {
                Screen.drawString(poseStack, Minecraft.getInstance().font, line, this.x, startY, 0xFFFFFF);
                startY += LINE_HEIGHT;
            }
        }
    }
}
