package mod.adrenix.nostalgic.util.client.gui;

import mod.adrenix.nostalgic.tweak.enums.Corner;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.data.NumberHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;
import java.util.HashSet;

/**
 * This utility manages and keeps track of where the overlay text should be rendered on the HUD. Use this utility when
 * drawing the overlay text to the screen.
 */
public class CornerManager
{
    private final float height = (float) GuiUtil.getGuiHeight();
    private final NumberHolder<Double> topLeft = NumberHolder.create(2.0D);
    private final NumberHolder<Double> topRight = NumberHolder.create(2.0D);
    private final NumberHolder<Double> bottomLeft = NumberHolder.create(this.height - 10.0D);
    private final NumberHolder<Double> bottomRight = NumberHolder.create(this.height - 10.0D);

    /**
     * Get where the next line for the given corner should be drawn and then add 10 units to the corner offset.
     *
     * @param corner The {@link Corner} context.
     * @return Where the next line for the given corner should be drawn.
     */
    public double getAndAdd(Corner corner)
    {
        return switch (corner)
        {
            case TOP_LEFT -> this.topLeft.getAndAdd(10.0D);
            case TOP_RIGHT -> this.topRight.getAndAdd(10.0D);
            case BOTTOM_LEFT -> this.bottomLeft.getAndAdd(-10.0D);
            case BOTTOM_RIGHT -> this.bottomRight.getAndAdd(-10.0D);
        };
    }

    /**
     * Calculates where text rendering from the right side of the screen should start.
     *
     * @param text The text to get a right side offset for.
     * @return The starting point of where the given text should render.
     */
    public int getRightOffset(String text)
    {
        return GuiUtil.getGuiWidth() - GuiUtil.font().width(text) - 2;
    }

    /**
     * Draws the given text to the screen using the given text and corner.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param text     The text to draw.
     * @param corner   The {@link Corner} to draw to.
     */
    public void drawText(GuiGraphics graphics, String text, Corner corner)
    {
        int x = corner.isLeft() ? 2 : this.getRightOffset(text);
        int y = (int) this.getAndAdd(corner);
        int effectOffset = 0;

        if (corner == Corner.TOP_RIGHT)
        {
            LocalPlayer player = Minecraft.getInstance().player;
            Collection<MobEffectInstance> effects = NullableResult.getOrElse(player, new HashSet<>(), LocalPlayer::getActiveEffects);

            if (!effects.isEmpty())
                effectOffset = 25;
        }

        DrawText.begin(graphics, text).pos(x, y + effectOffset).draw();
    }
}
