package mod.adrenix.nostalgic.fabric.mixin.tweak.gameplay.mechanics_farming;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.item.BoneMealItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin
{
    /**
     * Forces any bonemealable block to always return a successful application when the instant bonemeal tweak is
     * enabled.
     */
    @ModifyExpressionValue(
        method = "growCrop",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/BonemealableBlock;isBonemealSuccess(Lnet/minecraft/world/level/Level;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
        )
    )
    private static boolean nt_fabric_mechanics_farming$modifyBonemealSuccess(boolean isBonemealSuccess)
    {
        if (GameplayTweak.INSTANT_BONEMEAL.get())
            return true;

        return isBonemealSuccess;
    }
}
