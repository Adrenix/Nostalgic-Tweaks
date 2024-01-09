package mod.adrenix.nostalgic.mixin.tweak.candy.disabled_block_offsets;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * To prevent internal server crashes, it is important to use {@link BlockStateBaseMixin#getBlock()} to retrieve block
 * information. Using a level instance or interface to get block information will cause a singleplayer world to
 * soft-lock.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin
{
    /* Shadows */

    @Shadow
    public abstract boolean hasOffsetFunction();

    @Shadow
    public abstract Block getBlock();

    /* Injection */

    @ModifyReturnValue(
        method = "getOffset",
        at = @At("RETURN")
    )
    private Vec3 NT$onDisableBlockOffsets(Vec3 offset)
    {
        if (NostalgicTweaks.isMixinEarly() || NostalgicTweaks.isServer() || !this.hasOffsetFunction())
            return offset;

        Block block = this.getBlock();
        boolean isCustomOff = CandyTweak.DISABLE_BLOCK_OFFSETS.get().containsBlock(block);
        boolean isAllOff = CandyTweak.DISABLE_ALL_OFFSET.get();

        if (isCustomOff || isAllOff)
            return Vec3.ZERO;

        return offset;
    }
}
