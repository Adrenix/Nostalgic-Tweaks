package mod.adrenix.nostalgic.mixin.client.world;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin
{
    /* Shadows */

    @Shadow @Final private BiomeSpecialEffects specialEffects;

    /* Helpers */

    private static boolean isNether()
    {
        return Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension() == Level.NETHER;
    }

    private static boolean isOverworld()
    {
        return Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension() == Level.OVERWORLD;
    }

    /* Injections */

    /**
     * Brings back the old universal sky color in the overworld.
     * Controlled by the old biome colors tweak.
     */
    @Inject(method = "getSkyColor", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetSkyColor(CallbackInfoReturnable<Integer> callback)
    {
        if (isOverworld())
        {
            if (Minecraft.getInstance().options.renderDistance().get() <= 4)
            {
                callback.setReturnValue(ColorUtil.toIntFromRGBA(RenderSystem.getShaderFogColor()));
                return;
            }

            TweakVersion.SkyColor skyColor = ModConfig.Candy.getUniversalSky();

            if (ModConfig.Candy.isWorldSkyCustom())
                callback.setReturnValue(ColorUtil.toHexInt(ModConfig.Candy.getWorldSkyColor()));
            else if (ModConfig.Candy.oldDynamicSkyColor())
                callback.setReturnValue(WorldClientUtil.getSkyColorFromBiome());
            else if (skyColor == TweakVersion.SkyColor.DISABLED)
                callback.setReturnValue(this.specialEffects.getSkyColor());
            else
            {
                switch (skyColor)
                {
                    case CLASSIC -> callback.setReturnValue(0x9CCDFF);
                    case INF_DEV -> callback.setReturnValue(0xC6DEFF);
                    case ALPHA -> callback.setReturnValue(0x8BBDFF);
                    case BETA -> callback.setReturnValue(0x97A3FF);
                }
            }
        }
        else if (isNether())
        {
            if (ModConfig.Candy.isNetherSkyCustom())
                callback.setReturnValue(ColorUtil.toHexInt(ModConfig.Candy.getNetherSkyColor()));
            else if (ModConfig.Candy.oldNetherSky())
                callback.setReturnValue(0x210505);
        }
    }
}
