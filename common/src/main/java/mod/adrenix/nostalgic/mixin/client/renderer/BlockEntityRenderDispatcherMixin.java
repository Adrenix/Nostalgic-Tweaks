package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.client.config.ModConfig;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
    /**
     * Blocks the rendering for chest block entities. Since chests were modified to return model shapes, the resource
     * pack block model will be rendered instead.
     *
     * Controlled by various old chest tweaks.
     */
    @Inject
    (
        method = "getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;",
        at = @At("HEAD"),
        cancellable = true
    )
    private <E extends BlockEntity> void NT$onGetRenderer(E blockEntity, CallbackInfoReturnable<@Nullable BlockEntityRenderer<E>> callback)
    {
        if (ModConfig.Candy.oldChest() && blockEntity.getClass().equals(ChestBlockEntity.class))
            callback.setReturnValue(null);
        else if (ModConfig.Candy.oldEnderChest() && blockEntity.getClass().equals(EnderChestBlockEntity.class))
            callback.setReturnValue(null);
        else if (ModConfig.Candy.oldTrappedChest() && blockEntity.getClass().equals(TrappedChestBlockEntity.class))
            callback.setReturnValue(null);
    }
}
