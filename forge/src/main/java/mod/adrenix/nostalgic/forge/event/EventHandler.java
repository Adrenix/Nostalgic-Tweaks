package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientKeyMapping;
import mod.adrenix.nostalgic.util.KeyUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class EventHandler
{
    /* Key Input Events */

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            KeyUtil.onOpenConfig(ClientKeyMapping.CONFIG_KEY);
            KeyUtil.onToggleFog(ClientKeyMapping.FOG_KEY);
        }
    }

    /* Candy Events */

    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay toggle.
     */
    @SubscribeEvent
    public static void versionOverlay(RenderGuiOverlayEvent.Pre event) { CandyEvents.versionOverlay(event); }

    /**
     * Redirects the vanilla title screen to the mod's classic title screen.
     * Controlled by the old title screen toggle.
     */
    @SubscribeEvent
    public static void classicTitleScreen(ScreenEvent.Opening event) { CandyEvents.classicTitleScreen(event); }

    /**
     * Redirects various vanilla screens so that classic world loading screens can be rendered.
     * Controlled by the old loading screens toggle.
     */
    @SubscribeEvent
    public static void classicLoadingScreens(ScreenEvent.Opening event) { CandyEvents.classicLoadingScreens(event); }

    /**
     * Handles the rendering of old fog.
     * Controlled by the old overworld/nether fog toggles.
     */
    @SubscribeEvent
    public static void oldFogRendering(ViewportEvent.RenderFog event) { CandyEvents.oldFogRendering(event); }
}
