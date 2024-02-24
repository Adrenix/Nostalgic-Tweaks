package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * This utility class is used only by the client.
 */
public abstract class DebugMixinHelper
{
    /**
     * Determine if an entity's debug id should be shown.
     *
     * @param entity An {@link Entity} instance.
     * @return Whether an entity's debug id should be shown.
     */
    public static boolean shouldShowDebugId(Entity entity)
    {
        boolean isDebugging = Minecraft.getInstance().gui.getDebugOverlay().showDebugScreen();
        boolean isValidTarget = entity instanceof LivingEntity && !(entity instanceof Player);

        return CandyTweak.DEBUG_ENTITY_ID.get() && isDebugging && isValidTarget;
    }
}
