package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

public class AppleSkinListener implements AppleSkinApi
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerEvents()
    {
        TooltipOverlayEvent.Pre.EVENT.register(AppleSkinListener::disableTooltip);
        TooltipOverlayEvent.Render.EVENT.register(AppleSkinListener::disableTooltip);

        HUDOverlayEvent.Exhaustion.EVENT.register(AppleSkinListener::disableHUD);
        HUDOverlayEvent.Saturation.EVENT.register(AppleSkinListener::disableHUD);
        HUDOverlayEvent.HungerRestored.EVENT.register(AppleSkinListener::disableHUD);
        HUDOverlayEvent.HealthRestored.EVENT.register(AppleSkinListener::disableHUD);

        NostalgicTweaks.LOGGER.info("Registered Fabric AppleSkin Listener");
    }

    /**
     * Prevents tooltip food data for food items being displayed if hunger is disabled.
     *
     * @param event The {@link TooltipOverlayEvent} instance.
     */
    private static void disableTooltip(TooltipOverlayEvent event)
    {
        if (GameplayTweak.DISABLE_HUNGER.get())
            event.isCanceled = true;
    }

    /**
     * Prevents HUD elements from being drawn when hunger is disabled.
     *
     * @param event The {@link HUDOverlayEvent} instance.
     */
    private static void disableHUD(HUDOverlayEvent event)
    {
        if (GameplayTweak.DISABLE_HUNGER.get())
            event.isCanceled = true;
    }
}
