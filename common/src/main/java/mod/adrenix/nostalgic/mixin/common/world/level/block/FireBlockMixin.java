package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin
{
    /* Shadows */

    @Shadow @Final public static IntegerProperty AGE;
    @Shadow protected abstract BlockState getStateWithAge(LevelAccessor level, BlockPos pos, int age);
    @Shadow protected abstract boolean isValidFireLocation(BlockGetter level, BlockPos pos);
    @Shadow protected abstract boolean isNearRain(Level level, BlockPos pos);
    @Shadow protected abstract boolean canBurn(BlockState state);
    @Shadow protected abstract int getFireOdds(LevelReader levelReader, BlockPos blockPos);
    @Shadow protected abstract int getBurnOdd(BlockState blockState);

    /* Unique Helpers */

    @Unique
    private void NT$oldCheckBurnOut(Level level, BlockPos blockPos, int rand, Random random)
    {
        BlockState blockState = level.getBlockState(blockPos);
        int burnOdds = this.getBurnOdd(blockState);

        if (random.nextInt(rand) < burnOdds)
        {
            if (random.nextInt(2) == 0 && !level.isRainingAt(blockPos))
                level.setBlock(blockPos, this.getStateWithAge(level, blockPos, 0), 3);
            else
                level.removeBlock(blockPos, false);

            if (blockState.getBlock() instanceof TntBlock)
                TntBlock.explode(level, blockPos);
        }
    }

    /* Injections */

    /**
     * Ensures the tick delay for fire blocks happens every 10 ticks.
     * Controlled by the old fire tweak.
     */
    @Inject(method = "getFireTickDelay", at = @At("HEAD"), cancellable = true)
    private static void NT$onGetFireTickDelay(Random random, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.oldFire())
            callback.setReturnValue(10);
    }

    /**
     * Changes the default vanilla ticking behavior to the old classic style.
     * Controlled by the old fire and infinite burn tweak.
     */
    @Inject(method = "tick", cancellable = true, at = @At(ordinal = 0, value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private void NT$onTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.oldFire())
            return;

        int age = state.getValue(AGE);
        BlockState stateBelow = level.getBlockState(pos.below());
        boolean isBlockForeverBurning = stateBelow.is(level.dimensionType().infiniburn());

        int newAge = Math.min(15, age + 1);
        if (age != newAge)
        {
            state = state.setValue(AGE, newAge);
            level.setBlock(pos, state, 4);
        }

        if (!isBlockForeverBurning)
        {
            if (!this.isValidFireLocation(level, pos))
            {
                BlockPos posBelow = pos.below();
                if (!level.getBlockState(posBelow).isFaceSturdy(level, posBelow, Direction.UP) || age > 3)
                    level.removeBlock(pos, false);

                callback.cancel();
                return;
            }

            if (age == 15 && random.nextInt(4) == 0 && !this.canBurn(level.getBlockState(pos.below())))
            {
                level.removeBlock(pos, false);
                callback.cancel();
                return;
            }
        }

        if (age % 2 == 0 && age > 2)
        {
            this.NT$oldCheckBurnOut(level, pos.east(), 300, random);
            this.NT$oldCheckBurnOut(level, pos.west(), 300, random);
            this.NT$oldCheckBurnOut(level, pos.below(), 250, random);
            this.NT$oldCheckBurnOut(level, pos.above(), 250, random);
            this.NT$oldCheckBurnOut(level, pos.north(), 300, random);
            this.NT$oldCheckBurnOut(level, pos.south(), 300, random);

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int offsetX = -1; offsetX <= 1; offsetX++)
            {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++)
                {
                    for (int offsetY = -1; offsetY <= 4; offsetY++)
                    {
                        if (offsetX == 0 && offsetY == 0 && offsetZ == 0)
                            continue;

                        int rand = 100;
                        if (offsetY > 1)
                            rand += (offsetY - 1) * 100;

                        mutablePos.setWithOffset(pos, offsetX, offsetY, offsetZ);

                        int igniteOdds = this.getFireOdds(level, mutablePos);
                        if (igniteOdds <= 0 || random.nextInt(rand) > igniteOdds || level.isRaining() && this.isNearRain(level, mutablePos))
                            continue;

                        level.setBlock(mutablePos, this.getStateWithAge(level, mutablePos, 0), 3);
                    }
                }
            }
        }

        if (age == 15 && !ModConfig.Gameplay.infiniteBurn())
        {
            this.NT$oldCheckBurnOut(level, pos.east(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.west(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.below(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.above(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.north(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.south(), 1, random);
        }

        callback.cancel();
    }
}
