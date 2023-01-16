package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block
{
    /* Dummy Constructor */

    private LeavesBlockMixin(Properties ignored) { super(ignored); }

    /**
     * Overrides shade brightness so AO can be removed from touching full blocks.
     * Controlled by the old leaves lighting tweak.
     *
     * Since this injection is meant to act as a method override, it is important that
     * the method is a public float and matches the overriding method name.
     */

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return ModConfig.Candy.oldLeavesLighting() ? 1.0F : super.getShadeBrightness(state, level, pos);
    }
}
