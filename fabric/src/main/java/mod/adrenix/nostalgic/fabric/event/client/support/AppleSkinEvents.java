package mod.adrenix.nostalgic.fabric.event.client.support;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

/**
 * Adds Nostalgic HUD support for the AppleSkin (Fabric) mod.
 * The GUI utility tracker reports y-positions from Nostalgic Tweak's API.
 */

public class AppleSkinEvents implements AppleSkinApi
{
    /**
     * Registers Nostalgic Tweaks events to AppleSkin's API.
     */
    @Override
    public void registerEvents()
    {
        HUDOverlayEvent.Exhaustion.EVENT.register(AppleSkinEvents::setFoodPosition);
        HUDOverlayEvent.Saturation.EVENT.register(AppleSkinEvents::setFoodPosition);
        HUDOverlayEvent.HungerRestored.EVENT.register(AppleSkinEvents::setFoodPosition);
        HUDOverlayEvent.HealthRestored.EVENT.register(AppleSkinEvents::setHeartPosition);
        TooltipOverlayEvent.Pre.EVENT.register(AppleSkinEvents::setTooltipState);
        TooltipOverlayEvent.Render.EVENT.register(AppleSkinEvents::setTooltipState);

        NostalgicTweaks.LOGGER.info("Registered AppleSkin support events");
    }

    /**
     * Updates the food overlays based on tweak states.
     * @param event An AppleSkin HUD overlay event.
     */
    private static void setFoodPosition(HUDOverlayEvent event)
    {
        if (ModConfig.Gameplay.disableHungerBar())
            event.isCanceled = true;
        else
            event.y = GuiUtil.foodY;
    }

    /**
     * Updates the heart overlays based on tweak states.
     * @param event An AppleSkin HUD overlay event.
     */
    private static void setHeartPosition(HUDOverlayEvent event) { event.y = GuiUtil.heartY; }

    /**
     * Prevents tooltip food data for food items if hunger is disabled.
     * @param event An AppleSkin tooltip overlay event.
     */
    private static void setTooltipState(TooltipOverlayEvent event)
    {
        if (ModConfig.Gameplay.disableHunger())
            event.isCanceled = true;
    }
}
