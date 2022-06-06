package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin
{
    /**
     * Changes the darkness of the shade that forms on the corner of blocks.
     * Controlled by the old smooth lighting toggle.
     */
    @ModifyConstant(method = "getShadeBrightness", constant = @Constant(floatValue = 0.2F))
    private float onGetShadeBrightness(float vanilla)
    {
        return MixinConfig.Candy.oldSmoothLighting() ? 0.0F : 0.2F;
    }
}
