package mod.adrenix.nostalgic.mixin.util.gameplay;

import mod.adrenix.nostalgic.mixin.access.FireBlockAccess;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This utility class is used by both the client and server.
 */
public abstract class FireMixinHelper
{
    /**
     * Apply the old burn-out check algorithm to fire blocks.
     *
     * @param fire           The {@link FireBlockAccess} instance.
     * @param level          The {@link ServerLevel} instance.
     * @param blockPos       The {@link BlockPos} of the fire.
     * @param randomSource   The {@link RandomSource} instance.
     * @param randomizeBound The bound used to compare against the fire's burnout odds.
     */
    private static void checkBurnOut(FireBlockAccess fire, ServerLevel level, BlockPos blockPos, RandomSource randomSource, int randomizeBound)
    {
        BlockState blockState = level.getBlockState(blockPos);
        int burnOdds = fire.nt$getBurnOdds(blockState);

        if (randomSource.nextInt(randomizeBound) < burnOdds)
        {
            if (randomSource.nextInt(2) == 0 && !level.isRainingAt(blockPos))
                level.setBlock(blockPos, fire.nt$getStateWithAge(level, blockPos, 0), Block.UPDATE_ALL);
            else
                level.removeBlock(blockPos, false);

            if (blockState.getBlock() instanceof TntBlock)
                TntBlock.explode(level, blockPos);
        }
    }

    /**
     * Apply the old fire algorithm every tick. Due to significant changes made to the fire tick algorithm, the given
     * callback will be canceled.
     *
     * @param fire         The {@link FireBlockAccess} instance.
     * @param level        The {@link ServerLevel} instance.
     * @param blockPos     The {@link BlockPos} of the fire.
     * @param blockState   The {@link BlockState} of the fire.
     * @param randomSource The {@link RandomSource} instance.
     * @param callback     The {@link CallbackInfo} to cancel.
     */
    public static void tick(FireBlockAccess fire, ServerLevel level, BlockPos blockPos, BlockState blockState, RandomSource randomSource, CallbackInfo callback)
    {
        BlockPos belowPos = blockPos.below();
        BlockState belowState = level.getBlockState(belowPos);
        boolean isAboveInfiniteBurn = belowState.is(level.dimensionType().infiniburn());
        int fireAge = blockState.getValue(FireBlock.AGE);
        int nextAge = Math.min(15, fireAge + 1);

        if (fireAge != nextAge)
            level.setBlock(blockPos, blockState.setValue(FireBlock.AGE, nextAge), Block.UPDATE_INVISIBLE);

        if (!isAboveInfiniteBurn)
        {
            if (!fire.nt$isValidFireLocation(level, blockPos))
            {
                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP) || fireAge > 3)
                    level.removeBlock(blockPos, false);

                callback.cancel();

                return;
            }

            if (fireAge == 15 && randomSource.nextInt(4) == 0 && !fire.nt$canBurn(belowState))
            {
                level.removeBlock(blockPos, false);
                callback.cancel();

                return;
            }
        }

        if (fireAge % 2 == 0 && fireAge > 2)
        {
            checkBurnOut(fire, level, blockPos.east(), randomSource, 300);
            checkBurnOut(fire, level, blockPos.west(), randomSource, 300);
            checkBurnOut(fire, level, blockPos.below(), randomSource, 250);
            checkBurnOut(fire, level, blockPos.above(), randomSource, 250);
            checkBurnOut(fire, level, blockPos.north(), randomSource, 300);
            checkBurnOut(fire, level, blockPos.south(), randomSource, 300);

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int offsetX = -1; offsetX <= 1; offsetX++)
            {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++)
                {
                    for (int offsetY = -1; offsetY <= 4; offsetY++)
                    {
                        if (offsetX == 0 && offsetY == 0 && offsetZ == 0)
                            continue;

                        int bound = 100;

                        if (offsetY > 1)
                            bound += (offsetY - 1) * 100;

                        mutablePos.setWithOffset(blockPos, offsetX, offsetY, offsetZ);

                        int igniteOdds = fire.nt$getIgniteOdds(level, mutablePos);

                        if (igniteOdds <= 0 || randomSource.nextInt(bound) > igniteOdds || level.isRaining() && fire.nt$isNearRain(level, mutablePos))
                            continue;

                        level.setBlock(mutablePos, fire.nt$getStateWithAge(level, mutablePos, 0), Block.UPDATE_ALL);
                    }
                }
            }
        }

        if (fireAge == 15 && !GameplayTweak.INFINITE_BURN.get())
        {
            checkBurnOut(fire, level, blockPos.east(), randomSource, 1);
            checkBurnOut(fire, level, blockPos.west(), randomSource, 1);
            checkBurnOut(fire, level, blockPos.below(), randomSource, 1);
            checkBurnOut(fire, level, blockPos.above(), randomSource, 1);
            checkBurnOut(fire, level, blockPos.north(), randomSource, 1);
            checkBurnOut(fire, level, blockPos.south(), randomSource, 1);
        }

        callback.cancel();
    }
}
