package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class LevelMixin implements BlockAndTintGetter
{
    /* Shadows */

    @Shadow public abstract boolean isClientSide();
    @Shadow public abstract LevelLightEngine getLightEngine();

    /* Unique Helpers */

    /**
     * Changes the light values of sky/block light so that those values are in sync. This only occurs if the level
     * instance is client side.
     * @param lightLayer The light layer being either sky or block.
     * @param blockPos The block position.
     * @return A vanilla or modded light value.
     */
    @Unique
    private int NT$getLightValue(LightLayer lightLayer, BlockPos blockPos)
    {
        if (!this.isClientSide() || !ModConfig.Candy.oldLightRendering())
            return this.getLightEngine().getLayerListener(lightLayer).getLightValue(blockPos);

        int skyLight = this.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(blockPos);
        int blockLight = this.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(blockPos);

        if (ModConfig.Candy.oldWaterLighting() && BlockCommonUtil.isInWater(this, blockPos))
            skyLight = BlockCommonUtil.getWaterLightBlock(skyLight);

        if (LightLayer.SKY.equals(lightLayer) && skyLight <= 4)
            return skyLight;

        return WorldClientUtil.getMaxLight(skyLight, blockLight);
    }

    /* Injection Overrides */

    /**
     * Overrides the {@link BlockAndTintGetter#getBrightness(LightLayer, BlockPos)} method inside the abstract level
     * class. This prevents incorrect brightness values being sent to invokers that are not using light values for
     * rendering. Only the client side level instance will be able to use modded light values.
     */
    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos blockPos)
    {
        return this.NT$getLightValue(lightLayer, blockPos);
    }

    /**
     * Overrides the {@link BlockAndTintGetter#canSeeSky(BlockPos)} method inside the abstract level class. This
     * prevents incorrect flag states being sent to invokers. Since we only want to modify rendering of light, changing
     * light logic is not required. Only the client side level instance will be able to use modded light values.
     */
    @Override
    public boolean canSeeSky(BlockPos blockPos)
    {
        return this.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(blockPos) >= this.getMaxLightLevel();
    }
}
