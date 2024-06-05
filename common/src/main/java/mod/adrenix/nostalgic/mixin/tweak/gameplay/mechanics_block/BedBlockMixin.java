package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin
{
    /**
     * Disables the reduction in fall damage when an entity falls on a bed block.
     */
    @ModifyExpressionValue(
        method = "fallOn",
        at = @At(
            value = "CONSTANT",
            args = "floatValue=0.5F"
        )
    )
    private float nt_mechanics_block$modifyFallDistanceMultiplier(float multiplier)
    {
        return GameplayTweak.DISABLE_BED_BOUNCE.get() ? 1.0F : multiplier;
    }

    /**
     * Disables the ability for entities to bounce on bed blocks.
     */
    @WrapWithCondition(
        method = "bounceUp",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
        )
    )
    private boolean nt_mechanics_block$shouldEntityBounce(Entity entity, double x, double y, double z)
    {
        return !GameplayTweak.DISABLE_BED_BOUNCE.get();
    }
}
