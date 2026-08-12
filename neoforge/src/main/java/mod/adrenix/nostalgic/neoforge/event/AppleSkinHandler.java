package mod.adrenix.nostalgic.neoforge.event;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.neoforged.bus.api.SubscribeEvent;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

public abstract class AppleSkinHandler
{
    @SubscribeEvent
    public static void onRenderTooltipEvent(TooltipOverlayEvent.Render event)
    {
        disableTooltip(event);
    }

    @SubscribeEvent
    public static void onPreTooltipEvent(TooltipOverlayEvent.Pre event)
    {
        disableTooltip(event);
    }

    @SubscribeEvent
    public static void onRenderHUDExhaustion(HUDOverlayEvent.Exhaustion event)
    {
        disableHUD(event);
    }

    @SubscribeEvent
    public static void onRenderHUDSaturation(HUDOverlayEvent.Saturation event)
    {
        disableHUD(event);
    }

    @SubscribeEvent
    public static void onRenderHUDHungerRestored(HUDOverlayEvent.HungerRestored event)
    {
        disableHUD(event);
    }

    @SubscribeEvent
    public static void onRenderHUDHealthRestored(HUDOverlayEvent.HealthRestored event)
    {
        disableHUD(event);
    }

    /**
     * Prevents tooltip food data for food items being displayed if the hunger bar is hidden.
     *
     * @param event The {@link TooltipOverlayEvent} instance.
     */
    private static void disableTooltip(TooltipOverlayEvent event)
    {
        if (GameplayTweak.DISABLE_HUNGER.get())
            event.setCanceled(true);
    }

    /**
     * Prevents HUD elements from being drawn when hunger is disabled.
     *
     * @param event The {@link HUDOverlayEvent} instance.
     */
    private static void disableHUD(HUDOverlayEvent event)
    {
        if (GameplayTweak.DISABLE_HUNGER.get())
            event.setCanceled(true);
    }
}
