package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.EnderChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin
{
    /**
     * Always allows chest blocks to be opened regardless of whether there is a block above the ender chest.
     */
    @ModifyExpressionValue(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean nt_mechanics_block$isEnderChestBlocked(boolean isEnderChestBlocked)
    {
        return !GameplayTweak.ALWAYS_OPEN_CHEST.get() && isEnderChestBlocked;
    }
}
