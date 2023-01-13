package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentTableBlock.class)
public abstract class EnchantmentTableBlockMixin
{
    /**
     * Prevents the use of the enchantment table.
     * Controlled by the disabled enchantment table tweak.
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void NT$onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> callback)
    {
        if (ModConfig.Gameplay.disableEnchantTable())
            callback.setReturnValue(InteractionResult.FAIL);
    }
}
