package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
    /**
     * Blocks the rendering of chest block entities. Since chests are modified to return model shapes, the resource
     * pack's block model will be rendered instead.
     */
    @ModifyReturnValue(
        method = "getRenderer",
        at = @At("RETURN")
    )
    private <E extends BlockEntity> BlockEntityRenderer<E> nt_chest_block$modifyRenderer(@Nullable BlockEntityRenderer<E> renderer, E entity)
    {
        if (CandyTweak.OLD_CHEST.get() && entity.getClass().equals(ChestBlockEntity.class))
            return null;
        else if (CandyTweak.OLD_ENDER_CHEST.get() && entity.getClass().equals(EnderChestBlockEntity.class))
            return null;
        else if (CandyTweak.OLD_TRAPPED_CHEST.get() && entity.getClass().equals(TrappedChestBlockEntity.class))
            return null;

        String resourceKey = ItemCommonUtil.getResourceKey(entity.getBlockState().getBlock());

        if (CandyTweak.OLD_MOD_CHESTS.get().containsKey(resourceKey))
            return null;

        return renderer;
    }
}
