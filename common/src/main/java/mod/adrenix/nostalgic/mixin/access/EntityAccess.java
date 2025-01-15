package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccess
{
    @Invoker("getPrimaryStepSoundBlockPos")
    BlockPos nt$getPrimaryStepSoundBlockPos(BlockPos blockPos);
}
