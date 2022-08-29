package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IMixinEntity
{
    @Invoker("playStepSound") void NT$invokeStepSound(BlockPos pos, BlockState state);
}
