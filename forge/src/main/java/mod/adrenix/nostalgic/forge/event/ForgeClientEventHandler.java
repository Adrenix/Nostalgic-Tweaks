package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ForgeClientEventHandler
{
    /* Client Network Events */

    /**
     * Changes the network verification state of the mod.
     * If a server is not N.T supported, then we shouldn't be sending packets to it.
     */
    @SubscribeEvent
    public static void onLeaveWorld(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { ClientEventHelper.disconnect(); } /*No, I don't know how events work */

    /* Candy Events */

    /**
     * Redirects the vanilla title screen to the mod's classic title screen.
     * Controlled by the old title screen toggle.
     */
    @SubscribeEvent
    public static void classicTitleScreen(ScreenOpenEvent event) { ForgeCandyEvents.classicTitleScreen(event); }

    /**
     * Redirects various vanilla screens so that classic world loading screens can be rendered.
     * Controlled by the old loading screens toggle.
     */
    @SubscribeEvent
    public static void classicLoadingScreens(ScreenOpenEvent event) { ForgeCandyEvents.classicLoadingScreens(event); }

    /**
     * Handles the rendering of old fog.
     * Controlled by the old overworld/nether fog toggles.
     */
    @SubscribeEvent
    public static void oldFogRendering(EntityViewRenderEvent.RenderFogEvent event) { ForgeCandyEvents.oldFogRendering(event); }

    /* Gui Events */

    /**
     * Renders the current game version to the top left of the HUD along with alternative text HUD tweaks.
     * Controlled by the old version overlay toggle and various alternative HUD tweaks.
     *
     * Also overrides the overlays for armor, food, and air level bar.
     * Controlled by various HUD tweaks.
     */
    @SubscribeEvent
    public static void overlayOverride(RenderGameOverlayEvent.PreLayer event) { ForgeGuiEvents.overlayOverride(event); }
}
