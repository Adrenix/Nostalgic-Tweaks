package mod.adrenix.nostalgic.forge.event.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
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
}
