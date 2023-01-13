package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin
{
    /**
     * Disables the reduction in fall damage when an entity falls on a bed block.
     * Controlled by the disable bed bounce tweak.
     */
    @ModifyConstant(method = "fallOn", constant = @Constant(floatValue = 0.5F))
    private float NT$onCalculateFallDistance(float vanilla)
    {
        return ModConfig.Gameplay.disableBedBounce() ? 1.0F : vanilla;
    }

    /**
     * Disables the ability for entities to bounce on bed blocks.
     * Controlled by the disable bed bounce tweak.
     */
    @Inject(method = "bounceUp", at = @At("HEAD"), cancellable = true)
    private void NT$onBounceUp(Entity entity, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableBedBounce())
            callback.cancel();
    }
}
