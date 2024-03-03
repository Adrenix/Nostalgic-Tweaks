package mod.adrenix.nostalgic.client.gui.widget;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.client.renderer.TextureLayer;
import mod.adrenix.nostalgic.util.common.asset.GameSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Create a new {@link WidgetBackground} instance using the given widget background sprites.
 *
 * @param background  A {@link ResourceLocation} for the widget's background.
 * @param disabled    A {@link ResourceLocation} for the widget's disabled background.
 * @param highlighted A {@link ResourceLocation} for the widget's highlighted background.
 */
public record WidgetBackground(ResourceLocation background, ResourceLocation highlighted, ResourceLocation disabled)
{
    /* Static */

    public static final TextureLayer LAYER = TextureLayer.withIndex(1);
    public static final WidgetBackground BUTTON = new WidgetBackground(GameSprite.BUTTON, GameSprite.BUTTON_HIGHLIGHTED, GameSprite.BUTTON_DISABLED);
    public static final WidgetBackground SLIDER = new WidgetBackground(GameSprite.SLIDER, GameSprite.SLIDER, GameSprite.SLIDER);

    /* Methods */

    /**
     * Get the proper widget sprite.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return A {@link ResourceLocation} instance.
     */
    public ResourceLocation get(DynamicWidget<?, ?> widget)
    {
        if (widget.isHoveredOrFocused() && widget.isActive())
            return this.highlighted;

        if (widget.isInactive())
            return this.disabled;

        return this.background;
    }

    /**
     * Render a widget sprite background based on the given widget's state context.
     *
     * @param widget   A {@link DynamicWidget} instance.
     * @param graphics A {@link GuiGraphics} instance.
     */
    public void render(DynamicWidget<?, ?> widget, GuiGraphics graphics)
    {
        int x = widget.getX();
        int y = widget.getY();
        int width = widget.getWidth();
        int height = widget.getHeight();

        RenderUtil.beginBatching();
        RenderUtil.pushLayer(LAYER);
        RenderUtil.blitSprite(this.get(widget), graphics, x, y, width, height);
        RenderUtil.endBatching();
        RenderUtil.popLayer();
    }
}
