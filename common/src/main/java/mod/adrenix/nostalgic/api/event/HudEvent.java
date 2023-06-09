package mod.adrenix.nostalgic.api.event;

import mod.adrenix.nostalgic.api.EventHandler;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Use a mod loader specific event handler to tap into this event.
 */

public interface HudEvent extends EventHandler
{
    /**
     * In situations where there are mod conflicts, some HUD events may not fire. It is recommended that this helper
     * method be used to check if the vanilla HUD is being rendered.
     *
     * @return Whether the vanilla HUD has taken over rendering.
     */
    static boolean isVanilla()
    {
        return !ModConfig.Gameplay.disableHungerBar() && !ModConfig.Gameplay.disableExperienceBar();
    }

    /**
     * Get the current pose stack handling the rendering of hearts.
     * @return A pose stack instance.
     */
    GuiGraphics getGraphics();

    /**
     * Change where this overlay starts on the x-axis.
     *
     * If the mod sees that this value is not 0 before rendering the overlay, then the current set value will be
     * used. This is useful if you want to override the mod's position logic.
     *
     * @param x The new position on the x-axis.
     */
    void setX(int x);

    /**
     * Change where this overlay starts on the y-axis.
     *
     * If the mod sees that this value is not 0 before rendering the overlay, then the current set value will be
     * used. This is useful if you want to override the mod's position logic.
     *
     * @param y The new position on the y-axis.
     */
    void setY(int y);

    /**
     * Get where this overlay is on the x-axis.
     *
     * This value is set after the mod has rendered the overlay to the screen. Use this when you want to add extra
     * visual elements to the overlay.
     *
     * @return The current position on the x-axis.
     */
    int getX();

    /**
     * Get where this overlay is on the y-axis.
     *
     * This value is set after the mod has rendered the overlay to the screen. Use this when you want to add extra
     * visual elements to the overlay.
     *
     * @return The current position on the y-axis.
     */
    int getY();

    /**
     * Get the current icon index during the render cycle.
     * This index is zero based and goes from zero to infinity.
     *
     * @return A zero based rendering icon index.
     */
    int getIconIndex();

    /**
     * Checks if the disable hunger bar tweak is enabled by the user.
     * @return Whether hunger icons are invisible.
     */
    default boolean isHungerBarOff() { return ModConfig.Gameplay.disableHungerBar(); }

    /**
     * Checks if the disable experience bar tweak is enabled by the user.
     * @return Whether the experience bar is invisible.
     */
    default boolean isExperienceBarOff() { return ModConfig.Gameplay.disableExperienceBar(); }
}
