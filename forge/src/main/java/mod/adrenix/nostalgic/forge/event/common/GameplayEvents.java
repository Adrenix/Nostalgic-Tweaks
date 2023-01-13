package mod.adrenix.nostalgic.forge.event.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.server.PlayerServerUtil;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.Event;

/**
 * Helper class that defines instructions for various gameplay related events.
 */

public abstract class GameplayEvents
{
    /**
     * Disables the ability for living entities to drop experience.
     * @param event A living experience drop event instance.
     */
    public static void disableExperienceDrop(LivingExperienceDropEvent event)
    {
        if (ModConfig.Gameplay.disableOrbSpawn())
            event.setCanceled(true);
    }

    /**
     * Disables the ability for experience gained from blocks, such as furnaces, from spawning.
     * @param event A pickup experience event instance.
     */
    public static void disableExperiencePickup(PlayerXpEvent.PickupXp event)
    {
        if (ModConfig.Gameplay.disableOrbSpawn())
        {
            event.getOrb().discard();
            event.setCanceled(true);
        }
    }

    /**
     * Adds the ability to milk squad entities.
     * @param event An entity interact event.
     */
    public static void milkSquid(PlayerInteractEvent.EntityInteract event)
    {
        if (PlayerServerUtil.milkSquid(event.getEntity(), event.getHand(), event.getTarget()).equals(InteractionResult.SUCCESS))
            event.setCanceled(true);
    }

    /**
     * Prevents players from performing critical hits if the disabled critical hit tweak is enabled.
     * @param event A critical hit event.
     */
    public static void criticalHit(CriticalHitEvent event)
    {
        if (ModConfig.Gameplay.disableCriticalHit())
            event.setResult(Event.Result.DENY);
    }
}
