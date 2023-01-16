package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemFabricMixin
{
    /**
     * Enforces any block that inherits the {@link BonemealableBlock} interface to return <code>true</code> when the
     * {@link BonemealableBlock#isBonemealSuccess(Level, Random, BlockPos, BlockState)} method is invoked.
     *
     * Controlled by the instant bone meal tweak.
     */
    @Redirect
    (
        method = "growCrop",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/BonemealableBlock;isBonemealSuccess(Lnet/minecraft/world/level/Level;Ljava/util/Random;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
        )
    )
    private static boolean NT$onGrowCrop(BonemealableBlock block, Level level, Random random, BlockPos pos, BlockState state)
    {
        return WorldCommonUtil.isBonemealApplicable(block, level, random, pos, state);
    }
}
