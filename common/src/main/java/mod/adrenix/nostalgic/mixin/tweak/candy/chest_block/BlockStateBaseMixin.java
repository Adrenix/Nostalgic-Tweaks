package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.helper.candy.block.ChestHelper;
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
     * Setting the chest voxel shape to a full block allows entities to stand on chests properly. Because this changes a
     * chest's block behavior, the server must facilitate this tweak.
     */
    @ModifyReturnValue(
        method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
        at = @At("RETURN")
    )
    private VoxelShape nt_chest_block$modifyShape(VoxelShape voxelShape)
    {
        if (NostalgicTweaks.isMixinEarly())
            return voxelShape;

        if (CandyTweak.APPLY_CHEST_VOXEL.get() && ChestHelper.isOld(this.getBlock()))
            return Shapes.block();

        return voxelShape;
    }
}
