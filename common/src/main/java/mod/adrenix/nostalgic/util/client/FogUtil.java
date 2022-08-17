package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

/**
 * Terrain, sky, and void fog utility.
 */

public abstract class FogUtil
{
    public static boolean isMobEffectActive = false;

    public static boolean isOverworld(Camera camera) { return camera.getEntity().getLevel().dimension() == Level.OVERWORLD; }

    public static boolean isNether(Camera camera) { return camera.getEntity().getLevel().dimension() == Level.NETHER; }

    private static boolean isFluidFog(Camera camera) { return camera.getFluidInCamera() != FogType.NONE; }

    private static boolean isEntityBlind(Camera camera)
    {
        return camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).hasEffect(MobEffects.BLINDNESS);
    }

    private static int getRenderDistance()
    {
        int renderDistance = Minecraft.getInstance().options.renderDistance().get();
        int multiplier = renderDistance <= 6 ? 16 : 32;
        return renderDistance * multiplier;
    }

    private static void setTerrainFog(FogRenderer.FogMode fogMode)
    {
        if (fogMode != FogRenderer.FogMode.FOG_TERRAIN)
            return;

        float distance = Math.min(1024, getRenderDistance());
        RenderSystem.setShaderFogStart(0.0F);
        RenderSystem.setShaderFogEnd(distance * 0.8F);
    }

    private static void setHorizonFog(FogRenderer.FogMode fogMode)
    {
        if (fogMode != FogRenderer.FogMode.FOG_SKY)
            return;

        float distance = Math.min(512, getRenderDistance());
        RenderSystem.setShaderFogStart(distance * 0.25F);
        RenderSystem.setShaderFogEnd(distance);
    }

    private static void renderFog(FogRenderer.FogMode fogMode)
    {
        if (ModConfig.Candy.oldTerrainFog())
            setTerrainFog(fogMode);
        if (ModConfig.Candy.oldHorizonFog())
            setHorizonFog(fogMode);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }

    // Overrides fog in the overworld
    public static void setupFog(Camera camera, FogRenderer.FogMode fogMode)
    {
        if (isFluidFog(camera) || isEntityBlind(camera) || !isOverworld(camera))
            return;
        else if (isMobEffectActive)
        {
            isMobEffectActive = false;
            return;
        }

        renderFog(fogMode);
    }

    // Overrides fog in the nether
    public static void setupNetherFog(Camera camera, FogRenderer.FogMode fogMode)
    {
        if (!ModConfig.Candy.oldNetherFog() || isFluidFog(camera) || isEntityBlind(camera) || !isNether(camera))
            return;
        else if (isMobEffectActive)
        {
            isMobEffectActive = false;
            return;
        }

        renderFog(fogMode);
        RenderSystem.setShaderFogStart(0.0F);
    }
}
