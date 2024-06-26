package mod.adrenix.nostalgic.mixin.tweak.candy.block_hitbox;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin
{
    /* Shadows */

    @Shadow
    public abstract Block getBlock();

    /* Injections */

    /**
     * Setting a custom voxel shape to a full block allows players to stand on their custom full blocks properly.
     * Because this changes a block's behavior, the client must get permission from the server to use this tweak.
     */
    @ModifyReturnValue(
        method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
        at = @At("RETURN")
    )
    private VoxelShape nt_block_hitbox$modifyShape(VoxelShape voxelShape)
    {
        if (NostalgicTweaks.isMixinEarly() || !CandyTweak.APPLY_BLOCK_OUTLINE_VOXELS.get())
            return voxelShape;

        if (CandyTweak.FULL_BLOCK_OUTLINE_VOXELS.get().containsBlock(this.getBlock()))
            return Shapes.block();

        return voxelShape;
    }
}
