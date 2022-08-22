package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
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

        if (!ModConfig.Candy.disableVoidFog() && fogMode.equals(FogRenderer.FogMode.FOG_TERRAIN))
            VoidFog.render(camera);
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

    /* Void Fog */

    public static class VoidFog
    {
        private static final float[] lastFogRGBA = new float[] { 0.0F, 0.0F, 0.0F, 0.0F };
        private static float starAlpha = 0.0F;
        private static float lastStarAlpha = 0.0F;
        private static float lastFogDistance = 0.0F;
        private static float lastFogStart = 0.0F;
        private static float lastFogEnd = 0.0F;
        private static double lastBrightness = 0.0D;
        private static boolean isInitialized = false;

        public static void setStarAlpha(float value) { starAlpha = value; }

        public static float getStarAlpha() { return lastStarAlpha; }

        public static boolean isBelowHorizon()
        {
            Player player = Minecraft.getInstance().player;
            ClientLevel level = Minecraft.getInstance().level;
            float partialTick = Minecraft.getInstance().getDeltaFrameTime();

            if (player == null || level == null)
                return false;
            return player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level) < 0.0D;
        }

        public static boolean isIgnored(Camera camera)
        {
            return ModConfig.Candy.disableVoidFog() || ModConfig.Candy.getVoidFogStart() < getYLevel(camera.getEntity()) ||
                !ModConfig.Candy.creativeVoidFog() && camera.getEntity() instanceof Player player && player.isCreative() ||
                !isBelowHorizon()
            ;
        }

        public static boolean isRendering()
        {
            return !ModConfig.Candy.disableVoidFog() && isInitialized;
        }

        public static double getYLevel(Entity entity)
        {
            return entity.getY() - entity.level.getMinBuildHeight();
        }

        public static int getSkylight(Entity entity)
        {
            return entity.level.getBrightness(LightLayer.SKY, entity.blockPosition());
        }

        private static int getBrightness(Entity entity)
        {
            return ModConfig.Candy.shouldLightRemoveVoidFog() ? entity.level.getMaxLocalRawBrightness(entity.blockPosition()) : getSkylight(entity);
        }

        private static float getDistance(Entity entity)
        {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            double fogStart = ModConfig.Candy.getVoidFogStart();
            double fogDistance = getBrightness(entity) / 16.0D + getYLevel(entity) / fogStart;
            return fogDistance >= 1 ? renderDistance : (float) Mth.clamp(100.0D * Math.pow(Math.max(fogDistance, 0.0D), 2.0D), 5.0D, renderDistance);
        }

        private static boolean isThick(Camera camera)
        {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null)
                return false;

            int x = Mth.floor(camera.getPosition().x());
            int y = Mth.floor(camera.getPosition().y());

            return mc.level.effects().isFoggyAt(x, y) || mc.gui.getBossOverlay().shouldCreateWorldFog();
        }

        private static float getFogStart(Camera camera, float distance)
        {
            return isThick(camera) ? distance * 0.05F : distance * Math.max(0.0F, 0.55F * (1.0F - (distance - 5.0F) / 127.0F));
        }

        private static float getFogEnd(Camera camera, float distance)
        {
            return isThick(camera) ? Math.min(distance, 192.0F) / 2.0F : distance;
        }

        private static float getEntityDelta(Entity entity)
        {
            float distanceOffset = 15.0F;
            float distanceEnd = ModConfig.Candy.getVoidFogStart() - distanceOffset;
            return Math.max(0.0F, Math.min(1.0F, (1.0F - ((float) getYLevel(entity) - distanceEnd) / distanceOffset)));
        }

        private static double calculateBrightness(Entity entity, float partialTicks)
        {
            double yLevel = Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double brightness = (yLevel * ((ClientLevel) entity.level).getLevelData().getClearColorScale()) * (getSkylight(entity) / 15.0F);
            return brightness >= 1.0D ? 1.0D : Math.pow(Math.max(0.0D, brightness), 3);
        }

        private static float getFinalBrightness(Camera camera, float partialTicks)
        {
            double brightness = calculateBrightness(camera.getEntity(), partialTicks);
            brightness = Mth.lerp(partialTicks / 50.0D, lastBrightness, brightness);
            lastBrightness = brightness;
            return (float) brightness;
        }

        public static void setColor(Camera camera, float partialTicks)
        {
            float r, g, b, a;

            if (isIgnored(camera))
            {
                float[] rgba = RenderSystem.getShaderFogColor();
                r = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[0], rgba[0]);
                g = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[1], rgba[1]);
                b = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[2], rgba[2]);
                a = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[3], rgba[3]);
            }
            else
            {
                final int[] NT_RGBA = ModUtil.Text.toHexRGBA(ModConfig.Candy.getVoidFogColor());
                final float[] V_RGBA = RenderSystem.getShaderFogColor();
                float brightness = getFinalBrightness(camera, partialTicks);
                r = Mth.clamp(V_RGBA[0] * brightness + (NT_RGBA[0] / 255.0F), 0.0F, 1.0F);
                g = Mth.clamp(V_RGBA[1] * brightness + (NT_RGBA[1] / 255.0F), 0.0F, 1.0F);
                b = Mth.clamp(V_RGBA[2] * brightness + (NT_RGBA[2] / 255.0F), 0.0F, 1.0F);
                a = Mth.clamp(V_RGBA[3] * brightness + (NT_RGBA[3] / 255.0F), 0.0F, 1.0F);

                r = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[0], r);
                g = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[1], g);
                b = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[2], b);
                a = Mth.lerp(partialTicks / 10.0F, lastFogRGBA[3], a);
            }

            lastFogRGBA[0] = r;
            lastFogRGBA[1] = g;
            lastFogRGBA[2] = b;
            lastFogRGBA[3] = a;

            RenderSystem.clearColor(r, g, b, a);
            RenderSystem.setShaderFogColor(r, g, b, a);
        }

        private static void initialize(Camera camera, float partialTicks)
        {
            if (isInitialized)
                return;

            float[] rgba = RenderSystem.getShaderFogColor();
            lastFogRGBA[0] = rgba[0];
            lastFogRGBA[1] = rgba[1];
            lastFogRGBA[2] = rgba[2];
            lastFogRGBA[3] = rgba[3];
            lastBrightness = getFinalBrightness(camera, partialTicks);

            lastFogDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            lastFogStart = RenderSystem.getShaderFogStart();
            lastFogEnd = RenderSystem.getShaderFogEnd();
            lastStarAlpha = starAlpha;

            isInitialized = true;
        }

        public static void reset()
        {
            lastFogRGBA[0] = 0.0F;
            lastFogRGBA[1] = 0.0F;
            lastFogRGBA[2] = 0.0F;
            lastFogRGBA[3] = 0.0F;

            lastFogDistance = 0.0F;
            lastFogStart = 0.0F;
            lastFogEnd = 0.0F;
            lastStarAlpha = 0.0F;

            isInitialized = false;
        }

        private static void render(Camera camera)
        {
            Entity entity = camera.getEntity();
            float partialTicks = Minecraft.getInstance().getDeltaFrameTime();

            initialize(camera, partialTicks);

            if (isIgnored(camera))
            {
                float start = Mth.lerp(partialTicks / 10.0F, lastFogStart, RenderSystem.getShaderFogStart());
                float end = Mth.lerp(partialTicks / 10.0F, lastFogEnd, RenderSystem.getShaderFogEnd());
                lastStarAlpha = Mth.lerp(partialTicks / 10.0F, lastStarAlpha, starAlpha);
                lastFogStart = start;
                lastFogEnd = end;

                RenderSystem.setShaderFogStart(start);
                RenderSystem.setShaderFogEnd(end);

                return;
            }

            lastStarAlpha = Mth.lerp(partialTicks / 10.0F, lastStarAlpha, getSkylight(entity) == 0 ? 0.0F : starAlpha);

            float encroach = ModConfig.Candy.getVoidFogEncroach() / 100.0F;
            float distance = getDistance(entity);
            float entityDelta = getEntityDelta(entity) * encroach;

            if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.NIGHT_VISION))
                distance *= 4 * GameRenderer.getNightVisionScale(living, partialTicks);

            distance = Mth.lerp(partialTicks / 100.0F, lastFogDistance, distance);
            lastFogDistance = distance;

            RenderSystem.setShaderFogStart(Mth.lerp(entityDelta, RenderSystem.getShaderFogStart(), getFogStart(camera, distance)));
            RenderSystem.setShaderFogEnd(Mth.lerp(entityDelta, RenderSystem.getShaderFogEnd(), getFogEnd(camera, distance)));
        }
    }
}
