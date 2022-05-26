package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block
{
    private LeavesBlockMixin(Properties ignored)
    {
        super(ignored);
    }

    /**
     * Overrides shade brightness so AO can be removed from touching full blocks.
     * Controlled by the old leaves lighting toggle.
     */

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return MixinConfig.Candy.oldLeavesLighting() ? 1.0F : super.getShadeBrightness(state, level, pos);
    }
}
