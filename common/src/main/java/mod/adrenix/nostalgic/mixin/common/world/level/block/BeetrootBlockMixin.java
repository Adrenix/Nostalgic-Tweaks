package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeetrootBlock.class)
public abstract class BeetrootBlockMixin
{
    /* Shadows */

    @Shadow public abstract int getMaxAge();

    /* Injections */

    /**
     * Immediately grows a beetroot block when a bone meal item is used.
     * Controlled by the instant bone meal tweak.
     */
    @Inject(method = "getBonemealAgeIncrease", at = @At("HEAD"), cancellable = true)
    private void NT$onGetBonemealAgeIncrease(Level level, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantBonemeal())
            callback.setReturnValue(this.getMaxAge());
    }
}
