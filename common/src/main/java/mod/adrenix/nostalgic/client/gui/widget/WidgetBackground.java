package mod.adrenix.nostalgic.client.gui.widget;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.client.renderer.TextureLayer;
import mod.adrenix.nostalgic.util.common.asset.GameSprite;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Create a new {@link WidgetBackground} instance using the given widget background texture states.
 *
 * @param background  A {@link TextureLocation} for the 200x20 widget background.
 * @param disabled    A {@link TextureLocation} for the 200x20 widget disabled background.
 * @param highlighted A {@link TextureLocation} for the 200x20 widget highlighted background.
 */
public record WidgetBackground(TextureLocation background, TextureLocation highlighted, TextureLocation disabled)
{
    /* Static */

    public static final TextureLayer LAYER = TextureLayer.withIndex(1);
    public static final WidgetBackground BUTTON = new WidgetBackground(GameSprite.BUTTON, GameSprite.BUTTON_HIGHLIGHTED, GameSprite.BUTTON_DISABLED);
    public static final WidgetBackground SLIDER = new WidgetBackground(GameSprite.SLIDER, GameSprite.SLIDER, GameSprite.SLIDER);

    /* Methods */

    /**
     * Get a 200x20 background using the state context of the given widget. This enhanced version of the vanilla button
     * background renderer is capable of rendering an infinite number of backgrounds for a widget regardless of the
     * widget's width.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return A {@link TextureLocation} instance.
     */
    public TextureLocation get(DynamicWidget<?, ?> widget)
    {
        if (widget.isHoveredOrFocused() && widget.isActive())
            return this.highlighted;

        if (widget.isInactive())
            return this.disabled;

        return this.background;
    }

    /**
     * Render a 200x20 widget background based on the given widget's state context.
     *
     * @param widget   A {@link DynamicWidget} instance.
     * @param graphics A {@link GuiGraphics} instance.
     */
    public void render(DynamicWidget<?, ?> widget, GuiGraphics graphics)
    {
        TextureLocation texture = this.get(widget);

        RenderUtil.beginBatching();
        RenderUtil.pushLayer(LAYER);

        int x = widget.getX();
        int y = widget.getY();
        int endX = widget.getEndX();
        int width = widget.getWidth();
        int height = widget.getHeight();

        if (width <= 200)
        {
            int halfOfWidth = width / 2;
            int offsetX = MathUtil.isOdd(width) ? 1 : 0;

            RenderUtil.blitTexture(texture, graphics, x, y, 0, 0, halfOfWidth + offsetX, height);
            RenderUtil.blitTexture(texture, graphics, x + halfOfWidth + offsetX, y, 200 - halfOfWidth, 0, halfOfWidth, height);
        }
        else
        {
            int uOffset = 2;
            int repeat = width / 196;
            int remainder = width % 196;

            RenderUtil.blitTexture(texture, graphics, x, y, 0, 0, 2, height);

            for (int i = 0; i < repeat; i++)
            {
                RenderUtil.blitTexture(texture, graphics, x + uOffset, y, 2, 0, 196, height);
                uOffset += 196;
            }

            RenderUtil.blitTexture(texture, graphics, x + uOffset, y, 2, 0, remainder - 2, height);
            RenderUtil.blitTexture(texture, graphics, endX - 2, y, 198, 0, 2, height);
        }

        RenderUtil.endBatching();
        RenderUtil.popLayer();
    }
}
