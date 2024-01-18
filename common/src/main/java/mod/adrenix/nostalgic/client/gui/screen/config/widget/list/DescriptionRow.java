package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.GuiGraphics;

public class DescriptionRow extends ConfigRow<DescriptionRowMaker, DescriptionRow>
{
    /* Builder */

    /**
     * Create a new {@link DescriptionRow}.
     *
     * @param container The {@link Container} instance to get a description translation from.
     * @param rowList   The {@link RowList} instance.
     * @return A new {@link DescriptionRowMaker}.
     */
    public static DescriptionRowMaker create(Container container, RowList rowList)
    {
        return new DescriptionRowMaker(container, rowList);
    }

    /* Fields */

    protected final Container container;

    /* Constructor */

    DescriptionRow(DescriptionRowMaker maker)
    {
        super(maker);

        TextWidget.create(maker.container.getDescription().orElse(Lang.literal("null")))
            .extendWidthToEnd(this, 0)
            .build(this::addWidget);

        this.getBuilder()
            .indent(maker.container.getIndentForTweakRow())
            .postRenderer(this::renderTree)
            .hiddenRenderer(this::renderTree)
            .highlight(0.0D, this.highlighter)
            .heightOverflowMargin(1);

        this.container = maker.container;
    }

    /* Methods */

    /**
     * Handler method for rendering a row tree extension if the container needs it.
     *
     * @param row         A {@link Row} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void renderTree(DescriptionRow row, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.container.isCategory() || !ModTweak.DISPLAY_CATEGORY_TREE.fromCache())
            return;

        Color color = this.container.getColor().fromAlpha(ModTweak.CATEGORY_TREE_OPACITY.fromCache() / 100.0F);
        float startX = row.getX() - ConfigRow.TREE_OFFSET;
        float startY = row.getY() - this.getRowList().getRowMargin();
        float endY = row.getEndY();

        RenderUtil.fill(graphics, startX - 1, startY, startX, endY, color);
    }
}
