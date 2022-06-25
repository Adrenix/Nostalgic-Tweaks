package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class ForgeCommonEventHandler
{
    /**
     * Requests the server to sync the current server tweaks with the client.
     * This will fire on each world join. Since tweak caches work universally, no extra work is required.
     */
    @SubscribeEvent
    public static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) { ServerEventHelper.connect(event.getPlayer()); }
}
