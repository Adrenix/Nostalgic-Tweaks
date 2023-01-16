package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin
{
    /* Shadows */

    @Shadow @Final private AbstractTreeGrower treeGrower;

    /* Injections */

    /**
     * Immediately grows a tree when a bone meal item is used.
     * Controlled by the instant bone meal tweak.
     */
    @Inject(method = "advanceTree", at = @At("HEAD"), cancellable = true)
    private void NT$onAdvanceTree(ServerLevel level, BlockPos pos, BlockState state, Random random, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.instantBonemeal())
            return;

        if (state.getValue(SaplingBlock.STAGE) == 0)
            level.setBlock(pos, state.cycle(SaplingBlock.STAGE), 4);

        this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);

        callback.cancel();
    }
}
