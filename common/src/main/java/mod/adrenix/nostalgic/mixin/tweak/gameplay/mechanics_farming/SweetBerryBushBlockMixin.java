package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_farming;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushBlockMixin
{
    /**
     * Immediately grows a sweet berry bush block when using a bonemeal item.
     */
    @ModifyExpressionValue(
        method = "performBonemeal",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(II)I"
        )
    )
    private int nt_mechanics_farming$modifySweetBerryBushBlockAge(int age)
    {
        return GameplayTweak.INSTANT_BONEMEAL.get() ? SweetBerryBushBlock.MAX_AGE : age;
    }
}
