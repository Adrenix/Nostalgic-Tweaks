package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccess
{
    @Invoker("getBurnOdds")
    int nt$getBurnOdds(BlockState blockState);

    @Invoker("getIgniteOdds")
    int nt$getIgniteOdds(LevelReader level, BlockPos blockPos);

    @Invoker("getStateWithAge")
    BlockState nt$getStateWithAge(LevelAccessor level, BlockPos blockPos, int age);

    @Invoker("isValidFireLocation")
    boolean nt$isValidFireLocation(BlockGetter level, BlockPos blockPos);

    @Invoker("isNearRain")
    boolean nt$isNearRain(Level level, BlockPos blockPos);

    @Invoker("canBurn")
    boolean nt$canBurn(BlockState blockState);
}
