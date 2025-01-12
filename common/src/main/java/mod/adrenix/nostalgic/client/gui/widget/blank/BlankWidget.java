package mod.adrenix.nostalgic.client.gui.widget.blank;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.minecraft.client.gui.GuiGraphics;

public class BlankWidget extends DynamicWidget<BlankBuilder, BlankWidget>
{
    /* Builder */

    /**
     * Start the creation process of a new {@link BlankWidget}. Blank widgets are just that, blank. A use case would be
     * when another widget needs to extend to a certain point on the screen. A blank widget can be positioned there, and
     * the widget can use the blank widget as the width extension reference.
     *
     * @return A new {@link BlankBuilder} instance.
     */
    public static BlankBuilder create()
    {
        return new BlankBuilder();
    }

    /* Constructor */

    protected BlankWidget(BlankBuilder builder)
    {
        super(builder);
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isInactive() || this.isInvisible())
            return false;

        if (this.isValidClick(mouseX, mouseY, button) && this.getBuilder().onPress != null)
        {
            if (this.getBuilder().useClickSound)
                GuiUtil.playClick();

            this.getBuilder().onPress.run();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        this.getBuilder().renderer.accept(this, graphics, mouseX, mouseY, partialTick);
        this.renderDebug(graphics);
    }
}
