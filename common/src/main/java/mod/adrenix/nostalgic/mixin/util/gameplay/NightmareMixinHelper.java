package mod.adrenix.nostalgic.mixin.util.gameplay;

import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.LightLayer;

/**
 * This utility class is used by both the client and server.
 */
public abstract class NightmareMixinHelper
{
    /**
     * Check if the player is about to have a nightmare.
     *
     * @param player The sleeping {@link ServerPlayer} instance.
     */
    public static void tick(ServerPlayer player)
    {
        if (player.level().getDifficulty() == Difficulty.PEACEFUL || player.isCreative())
            return;

        ServerLevel level = (ServerLevel) player.level();
        BlockPos playerPos = player.getOnPos();

        if (player.isSleepingLongEnough() && level.getBrightness(LightLayer.BLOCK, playerPos) == 0)
        {
            player.stopSleepInBed(true, true);

            Monster monster = MathUtil.randomInt(0, 1) == 0 ? new Zombie(level) : new Skeleton(EntityType.SKELETON, level);
            Entity entity = monster.getType().create(level, null, playerPos, MobSpawnType.TRIGGERED, true, false);

            if (entity != null)
                level.addFreshEntity(entity);
        }
    }
}
