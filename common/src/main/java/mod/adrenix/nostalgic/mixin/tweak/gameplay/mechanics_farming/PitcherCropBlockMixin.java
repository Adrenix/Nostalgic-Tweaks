package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_farming;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.PitcherCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PitcherCropBlock.class)
public abstract class PitcherCropBlockMixin
{
    /**
     * Immediately grows a pitcher plant when a bonemeal item is used.
     */
    @ModifyExpressionValue(
        method = "grow",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(II)I"
        )
    )
    private int nt_mechanics_farming$modifyPitcherAgeIncrease(int age)
    {
        return GameplayTweak.INSTANT_BONEMEAL.get() ? PitcherCropBlock.MAX_AGE : age;
    }
}
