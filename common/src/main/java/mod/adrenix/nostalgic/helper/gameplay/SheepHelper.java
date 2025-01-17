package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.data.loot.packs.LootData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

/**
 * This utility class is used by both the client and server.
 */
public abstract class SheepHelper
{
    /**
     * Check if sheep eating grass is disabled.
     *
     * @param mob A {@link Mob} instance.
     * @return Whether sheep is prevented from eating grass.
     */
    public static boolean isEatGrassDisabled(Mob mob)
    {
        if (mob instanceof Sheep)
            return GameplayTweak.DISABLE_SHEEP_EAT_GRASS.get() && !GameplayTweak.RANDOM_SHEEP_WOOL_REGEN.get();

        return false;
    }

    /**
     * Check if sheep can randomly regrow their wool.
     *
     * @param mob A {@link Mob} instance.
     * @return Whether sheep can randomly regrow their wool.
     */
    public static boolean isRandomWoolRegen(Mob mob)
    {
        if (mob instanceof Sheep)
            return GameplayTweak.RANDOM_SHEEP_WOOL_REGEN.get() && GameplayTweak.DISABLE_SHEEP_EAT_GRASS.get();

        return false;
    }

    /**
     * Punch sheep, get wool.
     *
     * @param sheep        The {@link Sheep} instance.
     * @param level        The {@link ServerLevel} instance.
     * @param damageSource The {@link DamageSource} instance.
     * @param randomSource The {@link RandomSource} instance.
     */
    public static void punch(Sheep sheep, ServerLevel level, DamageSource damageSource, RandomSource randomSource)
    {
        boolean isHurtByPlayer = damageSource.getEntity() instanceof Player;
        boolean canShearSheep = sheep.readyForShearing() && !level.isClientSide;

        if (!isHurtByPlayer || !canShearSheep)
            return;

        sheep.setSheared(true);

        int cap = GameplayTweak.ONE_WOOL_PUNCH.get() ? 1 : 1 + randomSource.nextInt(3);

        for (int i = 0; i < cap; i++)
        {
            ItemLike item = LootData.WOOL_ITEM_BY_DYE.getOrDefault(sheep.getColor(), Blocks.WHITE_WOOL);
            ItemEntity wool = sheep.spawnAtLocation(level, item, 1);

            if (wool == null)
                continue;

            double x = (randomSource.nextFloat() - randomSource.nextFloat()) * 0.1F;
            double y = randomSource.nextFloat() * 0.05F;
            double z = (randomSource.nextFloat() - randomSource.nextFloat()) * 0.1F;

            wool.setDeltaMovement(wool.getDeltaMovement().add(x, y, z));
        }
    }
}
