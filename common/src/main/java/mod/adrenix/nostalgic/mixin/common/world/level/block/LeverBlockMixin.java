package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.level.block.LeverBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin
{
    /**
     * Disables the redstone particles emitted by levers when activated. This is done by modifying the alpha value to be
     * fully transparent.
     *
     * Controlled by the disable lever particles tweak.
     */
    @ModifyArg
    (
        method = "makeParticle",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/core/particles/DustParticleOptions;<init>(Lcom/mojang/math/Vector3f;F)V"
        )
    )
    private static float NT$onMakeParticle(float alpha)
    {
        return ModConfig.Candy.disableLeverParticles() ? 0.0F : alpha;
    }
}
