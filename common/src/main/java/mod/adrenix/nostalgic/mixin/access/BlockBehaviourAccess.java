package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccess
{
    @Accessor("hasCollision")
    boolean nt$hasCollision();
}
