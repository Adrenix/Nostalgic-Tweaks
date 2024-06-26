package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_fire;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.access.FireBlockAccess;
import mod.adrenix.nostalgic.mixin.util.gameplay.FireMixinHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin
{
    /**
     * Ensures the tick delay for fire blocks happens every 10 ticks.
     */
    @ModifyReturnValue(
        method = "getFireTickDelay",
        at = @At("RETURN")
    )
    private static int nt_mechanics_fire$modifyFireTickDelay(int tickDelay)
    {
        return GameplayTweak.OLD_FIRE.get() ? 10 : tickDelay;
    }

    /**
     * Revert to the old fire ticking behavior. Due to significant changes made to the fire tick algorithm, the given
     * callback will be canceled.
     */
    @Inject(
        cancellable = true,
        method = "tick",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Ljava/lang/Math;min(II)I"
        )
    )
    private void nt_mechanics_fire$onTick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource randomSource, CallbackInfo callback)
    {
        if (GameplayTweak.OLD_FIRE.get())
            FireMixinHelper.tick((FireBlockAccess) this, level, blockPos, blockState, randomSource, callback);
    }
}
