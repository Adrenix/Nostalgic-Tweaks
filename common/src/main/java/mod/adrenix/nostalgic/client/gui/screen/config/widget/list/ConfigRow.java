package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRowMaker;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public abstract class ConfigRow<Builder extends AbstractRowMaker<Builder, Row>, Row extends AbstractRow<Builder, Row>>
    extends AbstractRow<Builder, Row>
{
    /* Static */

    public static final int TREE_OFFSET = 13;

    /* Fields */

    @Nullable protected GroupRow parent;
    protected final Animation highlighter;

    /* Constructor */

    /**
     * Create a new {@link ConfigRow} instance.
     *
     * @param builder A {@link Builder} instance.
     */
    protected ConfigRow(Builder builder)
    {
        super(builder);

        this.highlighter = Animation.linear(150L, TimeUnit.MILLISECONDS);

        this.getBuilder()
            .heightOverflowMargin(1)
            .highlight(0.15D, this.highlighter)
            .postRenderer(this::renderTree)
            .hiddenRenderer(this::renderTree);
    }

    /* Methods */

    /**
     * Handler method for rendering a row tree. Each config row has an optional parent group row. If this parent is
     * present, then each config row is responsible for rendering the tree relative to itself. Group rows with a parent
     * group row are responsible for rendering grandparent extension lines. Tweak rows are only responsible for
     * rendering the sideways "T" connection to the tree.
     *
     * @param row         A {@link Row} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderTree(Row row, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.parent == null || !ModTweak.DISPLAY_CATEGORY_TREE.fromCache())
            return;

        Container container = this.parent.getContainer();
        int color = container.getColor().fromAlpha(ModTweak.CATEGORY_TREE_OPACITY.fromCache() / 100.0F).get();
        float startY = MathUtil.center(row.getY(), 2, row.getHeight());
        float startX = row.getX() - TREE_OFFSET;
        float endX = row.getX();
        float endY = startY + 1;
        float extendY = row.getY() - this.getRowList().getRowMargin();
        boolean debug = NostalgicTweaks.isDebugging();

        RenderUtil.beginBatching();
        RenderUtil.fill(graphics, startX, endX, startY, endY, debug ? 0x5A00FF00 : color);
        RenderUtil.fill(graphics, startX, startX - 1, startY + 1, extendY, debug ? 0x5AFF00FF : color);

        // Prevents rendering the grandparent line and the bottom portion of the sideways "T" if this is the last row of a tree
        if (CollectionUtil.last(this.parent.getChildren()).stream().anyMatch(this::equals))
        {
            RenderUtil.endBatching();
            return;
        }

        // Continue grandparent tree lines for group rows that are expanded
        if (this instanceof GroupRow groupRow && groupRow.isExpanded())
        {
            float lastParentY = this.getRowList().getRowMargin() + CollectionUtil.last(groupRow.getChildren())
                .stream()
                .mapToInt(ConfigRow::getEndY)
                .sum();

            float lastChildY = CollectionUtil.fromCast(groupRow.getChildren(), GroupRow.class)
                .filter(GroupRow::isExpanded)
                .mapToInt(child -> CollectionUtil.last(child.getChildren()).stream().mapToInt(ConfigRow::getEndY).sum())
                .max()
                .orElse(0);

            float topY = groupRow.getEndY();
            float bottomY = Math.max(lastParentY - this.getRowList().getRowMargin(), lastChildY);

            RenderUtil.fill(graphics, startX - 1, startX, topY, bottomY, debug ? 0x5AFF0000 : color);
        }

        RenderUtil.fill(graphics, startX - 1, startX, startY + 1, row.getEndY(), debug ? 0x5A0000FF : color);
        RenderUtil.endBatching();
    }
}
