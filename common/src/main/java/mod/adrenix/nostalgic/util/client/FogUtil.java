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
        boolean isTerrain = ModConfig.Candy.oldTerrainFog();
        boolean isHorizon = ModConfig.Candy.oldHorizonFog();

        if (isTerrain)
            setTerrainFog(fogMode);

        if (isHorizon)
            setHorizonFog(fogMode);

        if (isTerrain || isHorizon)
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }

    private static boolean isFogModified(Camera camera)
    {
        return isFluidFog(camera) || isEntityBlind(camera) || !isOverworld(camera) || isMobEffectActive;
    }

    // Overrides fog in the overworld
    public static void setupFog(Camera camera, FogRenderer.FogMode fogMode)
    {
        if (isMobEffectActive)
            isMobEffectActive = false;

        if (!isFogModified(camera))
            renderFog(fogMode);

        if (!ModConfig.Candy.disableVoidFog() && fogMode.equals(FogRenderer.FogMode.FOG_TERRAIN))
        {
            VoidFog.setFogStart(RenderSystem.getShaderFogStart());
            VoidFog.setFogEnd(RenderSystem.getShaderFogEnd());
            VoidFog.render(camera);
        }
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
        /* Interpolation Trackers */

        private static boolean isInitialized = false;

        private static double currentBrightness;
        private static float currentFogStart;
        private static float currentFogEnd;
        private static float currentCelestial;
        private static float currentStarAlpha;
        private static float targetStarAlpha;
        private static float targetFogStart;
        private static float targetFogEnd;
        private static float fogSpeedShift;
        private static float colorSpeedShift;

        private static final float[] CURRENT_VOID_RGB = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] CURRENT_FOG_RGB = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] CURRENT_SKY_RGB = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] TARGET_SKY_RGB = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] TARGET_FOG_RGB = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] TARGET_VOID_RGB = new float[] { 0.0F, 0.0F, 0.0F };

        /* Interpolation Setters/Getters */

        public static void setSkyRed(float red) { TARGET_SKY_RGB[0] = red; }
        public static void setSkyGreen(float green) { TARGET_SKY_RGB[1] = green; }
        public static void setSkyBlue(float blue) { TARGET_SKY_RGB[2] = blue; }

        public static float getSkyRed() { return CURRENT_SKY_RGB[0]; }
        public static float getSkyGreen() { return CURRENT_SKY_RGB[1]; }
        public static float getSkyBlue() { return CURRENT_SKY_RGB[2]; }

        public static void setFogStart(float start) { targetFogStart = start; }
        public static void setFogEnd(float end) { targetFogEnd = end; }

        public static void setFogRGB(float r, float g, float b)
        {
            TARGET_FOG_RGB[0] = r;
            TARGET_FOG_RGB[1] = g;
            TARGET_FOG_RGB[2] = b;
        }

        public static float[] getVoidRGB() { return CURRENT_VOID_RGB; }
        public static void setVoidRGB(float r, float g, float b)
        {
            TARGET_VOID_RGB[0] = r;
            TARGET_VOID_RGB[1] = g;
            TARGET_VOID_RGB[2] = b;
        }


        public static void setStarAlpha(float value) { targetStarAlpha = value; }
        public static float getStarAlpha() { return currentStarAlpha; }
        public static float getCelestial() { return currentCelestial; }

        /* Altitude Speed Shift */

        private enum Shift { NONE, COLOR, FOG }

        private static float getSpeed(float modifier, Shift shift)
        {
            Minecraft minecraft = Minecraft.getInstance();
            Camera camera = minecraft.gameRenderer.getMainCamera();

            if (isFogModified(camera))
                colorSpeedShift = fogSpeedShift = Float.MAX_VALUE;
            else if (getYLevel(camera.getEntity()) > ModConfig.Candy.getVoidFogStart() || !isBelowHorizon())
            {
                if (shift == Shift.COLOR)
                    colorSpeedShift = Mth.clamp(colorSpeedShift + 0.01F, 1.0F, 1.0e5F);

                if (shift == Shift.FOG)
                    fogSpeedShift = Mth.clamp(fogSpeedShift + 0.05F, 1.0F, 1.0e5F);
            }
            else
            {
                if (shift == Shift.COLOR)
                    colorSpeedShift = colorSpeedShift == Float.MAX_VALUE ? 1.0e5F : 1.0F;

                if (shift == Shift.FOG)
                    fogSpeedShift = fogSpeedShift == Float.MAX_VALUE ? 1.0e5F : 1.0F;
            }

            float speedShift = switch (shift)
            {
                case FOG -> fogSpeedShift;
                case COLOR -> colorSpeedShift;
                case NONE -> 1.0F;
            };

            return modifier * minecraft.getDeltaFrameTime() * speedShift;
        }

        /* Cave/Void Fog Methods */

        public static void setCelestialTransparency()
        {
            final float[] RGB = RenderSystem.getShaderColor();

            if (FogUtil.VoidFog.isRendering())
                RenderSystem.setShaderColor(RGB[0], RGB[1], RGB[2], FogUtil.VoidFog.getCelestial());
        }

        public static boolean isBelowHorizon()
        {
            Player player = Minecraft.getInstance().player;
            ClientLevel level = Minecraft.getInstance().level;
            float partialTick = Minecraft.getInstance().getDeltaFrameTime();

            if (player == null || level == null)
                return false;
            return player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level) < 0.0D;
        }

        public static double getYLevel(Entity entity)
        {
            return entity.getY() - entity.level.getMinBuildHeight();
        }

        public static boolean isIgnored(Camera camera)
        {
            return ModConfig.Candy.disableVoidFog() || ModConfig.Candy.getVoidFogStart() < getYLevel(camera.getEntity()) + 0.5D ||
                !ModConfig.Candy.creativeVoidFog() && camera.getEntity() instanceof Player player && player.isCreative() ||
                !isBelowHorizon()
            ;
        }

        public static boolean isRendering()
        {
            return !ModConfig.Candy.disableVoidFog() && isInitialized;
        }

        public static int getSkylight(Entity entity)
        {
            return entity.level.getBrightness(LightLayer.SKY, entity.blockPosition());
        }

        private static int getLocalBrightness(Entity entity)
        {
            int encroachment = (int) ((1 - (ModConfig.Candy.getVoidFogEncroach() / 100.0F)) * 15);
            int brightness = ModConfig.Candy.shouldLightRemoveVoidFog() ?
                entity.level.getMaxLocalRawBrightness(entity.blockPosition()) :
                getSkylight(entity)
            ;

            return Mth.clamp(brightness + encroachment, 0, 15);
        }

        private static float getDistance(Entity entity)
        {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            double fogStart = ModConfig.Candy.getVoidFogStart();
            double fogDistance = getLocalBrightness(entity) / 16.0D + getYLevel(entity) / (fogStart == 0 ? 1 : fogStart);
            return fogDistance >= 1 ? renderDistance : (float) Mth.clamp(100.0D * Math.pow(Math.max(fogDistance, 0.0D), 2.0D), 5.0D, renderDistance);
        }

        private static float getDistanceDelta(Entity entity)
        {
            float distanceOffset = 15.0F;
            float distanceEnd = ModConfig.Candy.getVoidFogStart() - distanceOffset;
            return Math.max(1.0F, Math.min(1.0F, (1.0F - ((float) getYLevel(entity) - distanceEnd) / distanceOffset)));
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

        private static double getBrightness(Entity entity)
        {
            double brightness = Math.max(15.0D - (ModConfig.Candy.getVoidFogStart() - getYLevel(entity)), getSkylight(entity)) / 15.0F;
            currentBrightness = ModUtil.Numbers.moveClampTowards(currentBrightness, brightness, getSpeed(0.002F, Shift.NONE), 0.0D, 1.0D);
            return currentBrightness;
        }

        public static void setColor(Camera camera)
        {
            final float SPEED = getSpeed(0.005F, Shift.COLOR);

            if (isIgnored(camera) || isFogModified(camera) || getSkylight(camera.getEntity()) == 15)
            {
                ModUtil.Numbers.moveTowardsColor(CURRENT_FOG_RGB, TARGET_FOG_RGB, SPEED);
                ModUtil.Numbers.moveTowardsColor(CURRENT_SKY_RGB, TARGET_SKY_RGB, SPEED);
                ModUtil.Numbers.moveTowardsColor(CURRENT_VOID_RGB, TARGET_VOID_RGB, SPEED);
            }
            else
            {
                final float[] CURRENT_FOG = RenderSystem.getShaderFogColor();
                final int[] CUSTOM_FOG = ModUtil.Text.toHexRGBA(ModConfig.Candy.getVoidFogColor());

                final float LIGHT = (float) getBrightness(camera.getEntity());
                final float FOG_R = Mth.clamp(CURRENT_FOG[0] * LIGHT + (CUSTOM_FOG[0] / 255.0F), 0.0F, 1.0F);
                final float FOG_G = Mth.clamp(CURRENT_FOG[1] * LIGHT + (CUSTOM_FOG[1] / 255.0F), 0.0F, 1.0F);
                final float FOG_B = Mth.clamp(CURRENT_FOG[2] * LIGHT + (CUSTOM_FOG[2] / 255.0F), 0.0F, 1.0F);
                final float[] TARGET_RGB = new float[] { FOG_R, FOG_G, FOG_B };

                ModUtil.Numbers.moveTowardsGrayscale(CURRENT_FOG_RGB, TARGET_RGB, SPEED);
                ModUtil.Numbers.moveTowardsGrayscale(CURRENT_SKY_RGB, TARGET_RGB, SPEED);
                ModUtil.Numbers.moveTowardsGrayscale(CURRENT_VOID_RGB, TARGET_RGB, SPEED);
            }

            RenderSystem.clearColor(CURRENT_FOG_RGB[0], CURRENT_FOG_RGB[1], CURRENT_FOG_RGB[2], 1.0F);
            RenderSystem.setShaderFogColor(CURRENT_FOG_RGB[0], CURRENT_FOG_RGB[1], CURRENT_FOG_RGB[2]);
        }

        public static void reset() { isInitialized = false; }

        private static void initialize(Camera camera)
        {
            if (isInitialized)
                return;

            ModUtil.Array.copy(TARGET_VOID_RGB, CURRENT_VOID_RGB);
            ModUtil.Array.copy(TARGET_FOG_RGB, CURRENT_FOG_RGB);
            ModUtil.Array.copy(TARGET_SKY_RGB, CURRENT_SKY_RGB);

            currentBrightness = getBrightness(camera.getEntity());
            currentStarAlpha = targetStarAlpha;
            currentFogStart = 0.0F;
            currentFogEnd = 0.0F;
            fogSpeedShift = 1.0e5F;
            colorSpeedShift = 1.0e5F;
            currentCelestial = 1.0F;

            isInitialized = true;
        }

        private static void render(Camera camera)
        {
            Entity entity = camera.getEntity();
            boolean isIgnored = isIgnored(camera) || isFogModified(camera);
            float partialTicks = Minecraft.getInstance().getDeltaFrameTime();

            float distance = getDistance(entity);
            float encroach = getDistanceDelta(entity);
            float speed = getSpeed(1.0F, Shift.FOG);

            if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.NIGHT_VISION))
                distance *= 4 * GameRenderer.getNightVisionScale(living, partialTicks);

            float celestialTarget = !isIgnored && getSkylight(entity) == 0 ? 0.0F : 1.0F;
            float fogStartTarget = isIgnored ? targetFogStart : getFogStart(camera, distance) * encroach;
            float fogEndTarget = isIgnored ? targetFogEnd : getFogEnd(camera, distance) * encroach;
            float starTarget = !isIgnored && getSkylight(entity) == 0 ? 0.0F : targetStarAlpha;

            initialize(camera);
            currentCelestial = ModUtil.Numbers.moveTowards(currentCelestial, celestialTarget, getSpeed(0.07F, Shift.NONE));
            currentStarAlpha = ModUtil.Numbers.moveTowards(currentStarAlpha, starTarget, getSpeed(0.01F, Shift.NONE));
            currentFogStart = ModUtil.Numbers.moveTowards(currentFogStart, fogStartTarget, speed);
            currentFogEnd = ModUtil.Numbers.moveTowards(currentFogEnd, fogEndTarget, speed);

            RenderSystem.setShaderFogStart(currentFogStart);
            RenderSystem.setShaderFogEnd(currentFogEnd);
        }
    }
}
