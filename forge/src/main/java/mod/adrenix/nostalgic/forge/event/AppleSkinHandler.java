package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.neoforged.bus.api.SubscribeEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

public abstract class AppleSkinHandler
{
    /**
     * Prevents tooltip food data from rendering if hunger is disabled.
     *
     * @param event The {@link TooltipOverlayEvent.Render} instance.
     */
    @SubscribeEvent
    public static void onRenderTooltipEvent(TooltipOverlayEvent.Render event)
    {
        disableTooltip(event);
    }

    /**
     * Prevents tooltip food data pre-processing if hunger is disabled.
     *
     * @param event The {@link TooltipOverlayEvent.Pre} instance.
     */
    @SubscribeEvent
    public static void onPreTooltipEvent(TooltipOverlayEvent.Pre event)
    {
        disableTooltip(event);
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
}
