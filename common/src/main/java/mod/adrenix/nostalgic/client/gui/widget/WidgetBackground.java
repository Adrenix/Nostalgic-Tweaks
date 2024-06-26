package mod.adrenix.nostalgic.client.gui.widget;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.client.renderer.TextureLayer;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.GameSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
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
     * @param isActive           Whether the widget is active.
     * @param isHoveredOrFocused Whether the widget is hovered or focused.
     * @return A sprite {@link ResourceLocation} instance.
     */
    public ResourceLocation get(boolean isActive, boolean isHoveredOrFocused)
    {
        if (isHoveredOrFocused && isActive)
            return this.highlighted;

        if (!isActive)
            return this.disabled;

        return this.background;
    }

    /**
     * Get the proper widget sprite.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return A sprite {@link ResourceLocation} instance.
     */
    public ResourceLocation get(DynamicWidget<?, ?> widget)
    {
        return this.get(widget.isActive(), widget.isHoveredOrFocused());
    }

    /**
     * Get the proper widget sprite.
     *
     * @param widget A {@link AbstractWidget} instance.
     * @return A sprite {@link ResourceLocation} instance.
     */
    public ResourceLocation get(AbstractWidget widget)
    {
        return this.get(widget.isActive(), widget.isHoveredOrFocused());
    }

    /**
     * Render a widget sprite background based on the given context.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param sprite   The sprite {@link ResourceLocation} instance.
     * @param x        The starting x-coordinate.
     * @param y        The starting y-coordinate.
     * @param width    The width of the render.
     * @param height   The height of the render.
     */
    @PublicAPI
    public void render(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height)
    {
        RenderUtil.beginBatching();
        RenderUtil.pushLayer(LAYER);
        RenderUtil.blitSprite(sprite, graphics, x, y, width, height);
        RenderUtil.endBatching();
        RenderUtil.popLayer();
    }

    /**
     * Render a widget sprite background based on the given widget's state context.
     *
     * @param widget   A {@link DynamicWidget} instance.
     * @param graphics A {@link GuiGraphics} instance.
     */
    @PublicAPI
    public void render(DynamicWidget<?, ?> widget, GuiGraphics graphics)
    {
        this.render(graphics, this.get(widget), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }

    /**
     * Render a widget sprite background based on the given widget's state context.
     *
     * @param widget   A {@link AbstractWidget} instance.
     * @param graphics A {@link GuiGraphics} instance.
     */
    @PublicAPI
    public void render(AbstractWidget widget, GuiGraphics graphics)
    {
        this.render(graphics, this.get(widget), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }
}
