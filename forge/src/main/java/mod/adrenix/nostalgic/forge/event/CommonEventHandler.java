package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.event.common.GameplayEvents;
import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handler class that subscribes mod events to Forge's event bus.
 * This class is focused on events used by both the client and server.
 */

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CommonEventHandler
{
    /**
     * Requests the server to sync the current server tweaks with the client.
     * This will fire on each world join. Since tweak caches work universally, no extra work is required.
     */
    @SubscribeEvent
    public static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) { ServerEventHelper.connect(event.getEntity()); }

    /**
     * Prevents experiences orbs from dropping.
     */
    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) { GameplayEvents.disableExperienceDrop(event); }

    /**
     * Removes any experience orbs that is currently dropped.
     */
    @SubscribeEvent
    public static void onExperiencePickup(PlayerXpEvent.PickupXp event) { GameplayEvents.disableExperiencePickup(event); }

    /**
     * Brings back the old bug that allows players to milk squids.
     */
    @SubscribeEvent
    public static void onSquidInteract(PlayerInteractEvent.EntityInteract event) { GameplayEvents.milkSquid(event); }
}
