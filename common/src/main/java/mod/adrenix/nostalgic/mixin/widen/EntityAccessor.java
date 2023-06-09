package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor
{
    @Invoker("playStepSound") void NT$stepSound(BlockPos pos, BlockState state);
}
