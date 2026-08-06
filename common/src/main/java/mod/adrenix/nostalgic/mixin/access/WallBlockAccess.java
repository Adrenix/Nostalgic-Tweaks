package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WallBlock.class)
public interface WallBlockAccess
{
    @Invoker("connectsTo")
    boolean nt$connectsTo(BlockState blockState, boolean isFaceSolid, Direction face);

    @Invoker("shouldRaisePost")
    boolean nt$shouldRaisePost(BlockState blockState, BlockState neighborState, VoxelShape shape);
}
