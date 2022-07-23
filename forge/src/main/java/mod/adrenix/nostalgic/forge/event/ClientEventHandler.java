package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientKeyMapping;
import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import mod.adrenix.nostalgic.forge.event.client.CandyEvents;
import mod.adrenix.nostalgic.forge.event.client.GuiEvents;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ClientEventHandler
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

    /* Client Network Events */

    /**
     * Changes the network verification state of the mod.
     *
     * Network verification is authenticated when the client receives a
     * {@link mod.adrenix.nostalgic.network.packet.PacketS2CHandshake PacketS2CHandshake} packet from a modded server.
     * If a server is not N.T supported, then network verification is false, and we shouldn't be sending packets to
     * the server.
     */
    @SubscribeEvent
    public static void onLeaveWorld(ClientPlayerNetworkEvent.LoggingOut event) { ClientEventHelper.disconnect(); }

    /* Candy Events */

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

    /* Gui Events */

    /**
     * Renders the current game version to the top left of the HUD along with alternative text HUD tweaks.
     * Controlled by the old version overlay toggle and various alternative HUD tweaks.
     *
     * Also overrides the overlays for armor, food, and air level bar.
     * Controlled by various HUD tweaks.
     */
    @SubscribeEvent
    public static void overlayOverride(RenderGuiOverlayEvent.Pre event) { GuiEvents.overlayOverride(event); }
}
