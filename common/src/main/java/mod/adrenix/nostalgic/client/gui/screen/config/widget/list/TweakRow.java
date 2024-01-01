package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.gui.GuiGraphics;

public class TweakRow extends ConfigRow<TweakRowMaker, TweakRow>
{
    /* Builders */

    /**
     * Create a new {@link TweakRow}.
     *
     * @param tweak   The {@link Tweak} this row manages.
     * @param rowList The {@link RowList} instance.
     * @return A new {@link TweakRowMaker}.
     */
    public static TweakRowMaker create(Tweak<?> tweak, RowList rowList)
    {
        return new TweakRowMaker(tweak, rowList);
    }

    /**
     * Create a new {@link TweakRow} with a parent {@link GroupRow}.
     *
     * @param tweak  The {@link Tweak} this row manages.
     * @param parent The {@link GroupRow} instance.
     * @return A new {@link TweakRowMaker}.
     */
    public static TweakRowMaker create(Tweak<?> tweak, GroupRow parent)
    {
        return new TweakRowMaker(tweak, parent);
    }

    /* Fields */

    private final TweakRowLayout layout;
    private final Tweak<?> tweak;

    /* Constructors */

    TweakRow(TweakRowMaker maker)
    {
        super(maker);

        this.parent = maker.parent;
        this.tweak = maker.tweak;
        this.layout = new TweakRowLayout(this);

        this.getBuilder()
            .indent(maker.indent ? this.getIndent() : this.getContainer().getIndentForTweakRow())
            .highlightColor(this.getColor())
            .highlight(0.06D, this.highlighter)
            .heightOverflowMargin(this.layout.padding)
            .postRenderer(this::renderBox);
    }

    /* Methods */

    /**
     * @return The {@link Tweak} that was assigned to this tweak row.
     */
    public Tweak<?> getTweak()
    {
        return this.tweak;
    }

    /**
     * @return The {@link TweakRowLayout} instance built for this tweak row.
     */
    public TweakRowLayout getLayout()
    {
        return this.layout;
    }

    /**
     * @return The {@link Container} assigned to this row's tweak.
     */
    Container getContainer()
    {
        return this.tweak.getContainer();
    }

    /**
     * @return The {@link Color} assigned to this row's tweak {@link Container}.
     */
    Color getColor()
    {
        return this.getContainer().getColor();
    }

    /**
     * Handler method for rendering the box outline for a tweak row.
     *
     * @param row         A {@link TweakRow} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderBox(TweakRow row, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Color color = this.getColor();
        boolean isFocused = row.isWidgetFocused();

        if (!isFocused && !ModTweak.DISPLAY_CATEGORY_TREE.fromCache())
            return;

        if (this.parent != null)
            color = this.parent.getContainer().getColor();

        if (isFocused)
            color = Color.FRENCH_SKY_BLUE;
        else
            color = color.fromAlpha(ModTweak.CATEGORY_TREE_OPACITY.fromCache() / 100.0F);

        RenderUtil.outline(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), color);
        this.renderTree(row, graphics, mouseX, mouseY, partialTick);
    }
}
