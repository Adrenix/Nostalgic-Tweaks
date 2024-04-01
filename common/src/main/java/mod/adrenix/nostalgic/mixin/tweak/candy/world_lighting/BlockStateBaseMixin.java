package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.mixin.util.candy.ChestMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * To prevent internal server crashes, it is important to use {@link #getBlock()} to retrieve block information. Using a
 * level instance or interface to get block information will cause a singleplayer world to soft-lock.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin
{
    /* Shadows */

    @Shadow
    public abstract int getLightEmission();

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    /* Injections */

    /**
     * If classic lighting is enabled, then block light values no longer exist. If the block emits light, then setting
     * the texture as emissive will give the illusion of block light as it was done in classic.
     */
    @ModifyReturnValue(
        method = "emissiveRendering",
        at = @At("RETURN")
    )
    private boolean nt_world_lighting$modifyEmissiveRendering(boolean isEmissive)
    {
        if (NostalgicTweaks.isMixinEarly())
            return isEmissive;

        if (CandyTweak.OLD_CLASSIC_ENGINE.get() && this.getLightEmission() > 0)
            return true;

        if (CandyTweak.OLD_LIGHT_COLOR.get() && this.getLightEmission() >= 14)
            return true;

        return isEmissive;
    }

    /**
     * Changes the amount of light blocked for certain blocks that require it.
     */
    @ModifyReturnValue(
        method = "getLightBlock",
        at = @At("RETURN")
    )
    private int nt_world_lighting$modifyLightBlock(int lightBlock)
    {
        if (NostalgicTweaks.isMixinEarly() || !RenderSystem.isOnRenderThread())
            return lightBlock;

        if (CandyTweak.OLD_WATER_LIGHTING.get() && BlockUtil.isWaterLike(this.asState()))
            return 3;

        if (CandyTweak.CHEST_LIGHT_BLOCK.get())
        {
            if (ChestMixinHelper.isOld(this.getBlock()) && !ChestMixinHelper.isTranslucent(this.getBlock()))
                return 15;
        }

        return lightBlock;
    }
}
