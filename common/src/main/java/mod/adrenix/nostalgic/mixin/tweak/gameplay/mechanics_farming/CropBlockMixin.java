package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_farming;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.CropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin
{
    /* Shadows */

    @Shadow
    public abstract int getMaxAge();

    /* Injections */

    /**
     * Immediately grows a crop when a bonemeal item is used.
     */
    @ModifyReturnValue(
        method = "getBonemealAgeIncrease",
        at = @At("RETURN")
    )
    private int nt_mechanics_farming$modifyCropBonemealAgeIncrease(int ageIncrease)
    {
        return GameplayTweak.INSTANT_BONEMEAL.get() ? this.getMaxAge() : ageIncrease;
    }
}
