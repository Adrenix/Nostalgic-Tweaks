package mod.adrenix.nostalgic.client.gui.widget.separator;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.gui.GuiGraphics;

public class SeparatorWidget extends DynamicWidget<SeparatorBuilder, SeparatorWidget>
{
    /* Builders */

    /**
     * Start the creation process of a new separator widget. A separator widget is a line that can act as a separator
     * between widgets. The default {@code height} for a separator is {@code 2}. This can be changed by using the
     * {@link SeparatorBuilder#height(int)} or a similar height setter.
     *
     * @param color The color of the separator line.
     * @return A new {@link SeparatorBuilder} instance.
     */
    public static SeparatorBuilder create(Color color)
    {
        return new SeparatorBuilder(color);
    }

    /* Constructor */

    protected SeparatorWidget(SeparatorBuilder builder)
    {
        super(builder);
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        RenderUtil.fill(graphics, this.getX(), this.getY(), this.getEndX(), this.getEndY(), this.getBuilder().color);
    }
}
