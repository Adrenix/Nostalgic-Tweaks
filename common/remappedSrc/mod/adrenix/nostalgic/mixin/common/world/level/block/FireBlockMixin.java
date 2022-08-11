package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin
{
    /* Shadows */

    @Shadow @Final public static IntProperty AGE;
    @Shadow protected abstract BlockState getStateWithAge(WorldAccess level, BlockPos pos, int age);
    @Shadow protected abstract boolean isValidFireLocation(BlockView level, BlockPos pos);
    @Shadow protected abstract boolean isNearRain(World level, BlockPos pos);
    @Shadow protected abstract boolean canBurn(BlockState state);
    @Shadow protected abstract int getIgniteOdds(WorldView levelReader, BlockPos blockPos);
    @Shadow protected abstract int getBurnOdds(BlockState blockState);

    /* Unique Helpers */

    @Unique
    private void NT$oldCheckBurnOut(World level, BlockPos blockPos, int rand, RandomSource randomSource)
    {
        BlockState blockState = level.getBlockState(blockPos);
        int burnOdds = this.getBurnOdds(blockState);

        if (randomSource.nextInt(rand) < burnOdds)
        {
            if (randomSource.nextInt(2) == 0 && !level.hasRain(blockPos))
                level.setBlockState(blockPos, this.getStateWithAge(level, blockPos, 0), 3);
            else
                level.removeBlock(blockPos, false);

            if (blockState.getBlock() instanceof TntBlock)
                TntBlock.primeTnt(level, blockPos);
        }
    }

    /* Injections */

    /**
     * Ensures the tick delay for fire blocks happens every 10 ticks.
     * Controlled by the old fire tweak.
     */
    @Inject(method = "getFireTickDelay", at = @At("HEAD"), cancellable = true)
    private static void NT$onGetFireTickDelay(RandomSource randomSource, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.oldFire())
            callback.setReturnValue(10);
    }

    /**
     * Changes the default vanilla ticking behavior to the old classic style.
     * Controlled by the old fire and infinite burn tweak.
     */
    @Inject(method = "tick", cancellable = true, at = @At(ordinal = 0, value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private void NT$onTick(BlockState state, ServerWorld level, BlockPos pos, RandomSource random, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.oldFire())
            return;

        int age = state.get(AGE);
        BlockState stateBelow = level.getBlockState(pos.down());
        boolean isBlockForeverBurning = stateBelow.isIn(level.getDimension().getInfiniburnBlocks());

        int newAge = Math.min(15, age + 1);
        if (age != newAge)
        {
            state = state.with(AGE, newAge);
            level.setBlockState(pos, state, 4);
        }

        if (!isBlockForeverBurning)
        {
            if (!this.isValidFireLocation(level, pos))
            {
                BlockPos posBelow = pos.down();
                if (!level.getBlockState(posBelow).isSideSolidFullSquare(level, posBelow, Direction.UP) || age > 3)
                    level.removeBlock(pos, false);

                callback.cancel();
                return;
            }

            if (age == 15 && random.nextInt(4) == 0 && !this.canBurn(level.getBlockState(pos.down())))
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
            this.NT$oldCheckBurnOut(level, pos.down(), 250, random);
            this.NT$oldCheckBurnOut(level, pos.up(), 250, random);
            this.NT$oldCheckBurnOut(level, pos.north(), 300, random);
            this.NT$oldCheckBurnOut(level, pos.south(), 300, random);

            BlockPos.Mutable mutablePos = new BlockPos.Mutable();

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

                        mutablePos.set(pos, offsetX, offsetY, offsetZ);

                        int igniteOdds = this.getIgniteOdds(level, mutablePos);
                        if (igniteOdds <= 0 || random.nextInt(rand) > igniteOdds || level.isRaining() && this.isNearRain(level, mutablePos))
                            continue;

                        level.setBlockState(mutablePos, this.getStateWithAge(level, mutablePos, 0), 3);
                    }
                }
            }
        }

        if (age == 15 && !ModConfig.Gameplay.infiniteBurn())
        {
            this.NT$oldCheckBurnOut(level, pos.east(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.west(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.down(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.up(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.north(), 1, random);
            this.NT$oldCheckBurnOut(level, pos.south(), 1, random);
        }

        callback.cancel();
    }
}
