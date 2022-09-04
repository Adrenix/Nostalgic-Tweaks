package mod.adrenix.nostalgic.forge.event.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.server.PlayerServerUtil;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;

public abstract class GameplayEvents
{
    // Disable Experience Drop
    public static void disableExperienceDrop(LivingExperienceDropEvent event)
    {
        if (ModConfig.Gameplay.disableOrbSpawn())
            event.setCanceled(true);
    }

    // Remove Dropped Experience
    public static void disableExperiencePickup(PlayerXpEvent.PickupXp event)
    {
        if (ModConfig.Gameplay.disableOrbSpawn())
        {
            event.getOrb().discard();
            event.setCanceled(true);
        }
    }

    // Squid Milking
    public static void milkSquid(PlayerInteractEvent.EntityInteract event)
    {
        if (PlayerServerUtil.milkSquid(event.getEntity(), event.getHand(), event.getTarget()).equals(InteractionResult.SUCCESS))
            event.setCanceled(true);
    }
}
