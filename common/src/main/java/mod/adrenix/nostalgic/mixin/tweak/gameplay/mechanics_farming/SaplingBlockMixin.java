package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_farming;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.SaplingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin
{
    /**
     * Immediately grows a sapling into a tree when a bonemeal item is used.
     */
    @ModifyExpressionValue(
        method = "advanceTree",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"
        )
    )
    private Comparable<Integer> nt_mechanics_farming$modifySaplingStage(Comparable<Integer> stage)
    {
        return GameplayTweak.INSTANT_BONEMEAL.get() ? 4 : stage;
    }
}
