package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class EventHandler
{
    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay toggle.
     */
    @SubscribeEvent
    public static void versionOverlay(RenderGameOverlayEvent.PreLayer event) { CandyEvents.versionOverlay(event); }

    /**
     * Redirects the vanilla title screen to the mod's classic title screen.
     * Controlled by the old title screen toggle.
     */
    @SubscribeEvent
    public static void classicTitleScreen(ScreenOpenEvent event) { CandyEvents.classicTitleScreen(event); }

    /**
     * Redirects various vanilla screens so that classic world loading screens can be rendered.
     * Controlled by the old loading screens toggle.
     */
    @SubscribeEvent
    public static void classicLoadingScreens(ScreenOpenEvent event) { CandyEvents.classicLoadingScreens(event); }

    /**
     * Handles the rendering of old fog.
     * Controlled by the old overworld/nether fog toggles.
     */
    @SubscribeEvent
    public static void oldFogRendering(EntityViewRenderEvent.RenderFogEvent event) { CandyEvents.oldFogRendering(event); }
}
