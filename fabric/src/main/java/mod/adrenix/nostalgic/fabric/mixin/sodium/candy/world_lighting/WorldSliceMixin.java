package mod.adrenix.nostalgic.fabric.mixin.sodium.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldSlice.class)
public abstract class WorldSliceMixin
{
    /* Shadows */

    @Shadow @Final private ClientLevel world;

    /* Injections */

    /**
     * Modifies the world brightness based on old lighting context.
     */
    @ModifyReturnValue(
        method = "getBrightness",
        at = @At("RETURN")
    )
    public int nt_sodium_world_lighting$modifyGetBrightness(int lightValue, LightLayer lightLayer, BlockPos blockPos)
    {
        if (lightLayer != LightLayer.SKY)
        {
            if (CandyTweak.OLD_CLASSIC_ENGINE.get())
                return 0;

            return lightValue;
        }

        if (CandyTweak.OLD_CLASSIC_ENGINE.get())
            return LightingMixinHelper.getClassicLight(lightValue, this.world, blockPos);

        if (CandyTweak.ROUND_ROBIN_RELIGHT.get())
            return LightingMixinHelper.getCombinedLight(lightValue, this.world.getBrightness(LightLayer.SKY, blockPos));

        return lightValue;
    }

    /**
     * Modifies the world raw brightness based on old lighting context.
     */
    @ModifyArg(
        index = 1,
        method = "getRawBrightness",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;max(II)I"
        )
    )
    public int nt_sodium_world_lighting$modifyGetRawBrightness(int skyLight, @Local(argsOnly = true) BlockPos blockPos)
    {
        if (ModTweak.ENABLED.get())
            return this.world.getBrightness(LightLayer.SKY, blockPos);

        return skyLight;
    }
}
