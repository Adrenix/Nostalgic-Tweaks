package mod.adrenix.nostalgic.forge.event.client.support;

import mod.adrenix.nostalgic.api.event.HudEvent;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

/**
 * Adds Nostalgic HUD support for the AppleSkin (Forge) mod.
 * The GUI utility tracker reports y-positions from Nostalgic Tweak's API.
 */

public class AppleSkinEvents
{
    /**
     * Prevents the rendering of hunger overlay elements when Nostalgic Tweak disables the hunger bar.
     * @param event An AppleSkin HUD overlay event.
     */
    private static void overlay(HUDOverlayEvent event)
    {
        if (ModConfig.Gameplay.disableHungerBar() && !HudEvent.isVanilla())
            event.setCanceled(true);
    }

    /**
     * Prevents tooltip food data for food items if hunger is disabled.
     * @param event An AppleSkin tooltip overlay event.
     */
    private static void tooltip(TooltipOverlayEvent event)
    {
        if (ModConfig.Gameplay.disableHunger())
            event.setCanceled(true);
    }

    /* Subscriptions */

    @SubscribeEvent
    public void onExhaustionEvent(HUDOverlayEvent.Exhaustion event) { AppleSkinEvents.overlay(event); }

    @SubscribeEvent
    public void onSaturationEvent(HUDOverlayEvent.Saturation event) { AppleSkinEvents.overlay(event); }

    @SubscribeEvent
    public void onHungerRestoredEvent(HUDOverlayEvent.HungerRestored event) { AppleSkinEvents.overlay(event); }

    @SubscribeEvent
    public void onHealthRestoredEvent(HUDOverlayEvent.HealthRestored event) { event.y = GuiUtil.heartY; }

    @SubscribeEvent
    public void onPreTooltipEvent(TooltipOverlayEvent.Pre event) { AppleSkinEvents.tooltip(event); }

    @SubscribeEvent
    public void onRenderTooltipEvent(TooltipOverlayEvent.Render event) { AppleSkinEvents.tooltip(event); }
}
