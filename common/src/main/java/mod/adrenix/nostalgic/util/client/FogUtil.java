package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.common.ArrayUtil;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;

/**
 * Terrain, sky, nether, water, cave, and void fog utility.
 */

public abstract class FogUtil
{
    /**
     * Checks of there is a mob present that is changing the fog.
     */
    public static boolean isMobEffectActive = false;

    /**
     * Checks if the camera is currently in the overworld.
     * @param camera The game's camera.
     * @return Whether the camera entity is in the overworld dimension.
     */
    public static boolean isOverworld(Camera camera)
    {
        return camera.getEntity().level().dimension() == Level.OVERWORLD;
    }

    /**
     * Checks if the camera is currently in the nether.
     * @param camera The game's camera.
     * @return Whether the camera entity is in the nether dimension.
     */
    public static boolean isNether(Camera camera) { return camera.getEntity().level().dimension() == Level.NETHER; }

    /**
     * Checks if the camera is in some type of fluid.
     * @param camera The game's camera.
     * @return Whether the camera is within a fluid like water or lava.
     */
    private static boolean isFluidFog(Camera camera) { return camera.getFluidInCamera() != FogType.NONE; }

    /**
     * Checks if the camera entity currently has the blindness effect.
     * @param camera The game's camera.
     * @return Whether the camera entity has a blindness effect applied.
     */
    private static boolean isEntityBlind(Camera camera)
    {
        return camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).hasEffect(MobEffects.BLINDNESS);
    }

    /**
     * Changes the game's terrain fog starting and ending points.
     * @param fogMode The fog mode that is rendering.
     * @param worldFog The current world fog type.
     */
    private static void setTerrainFog(FogRenderer.FogMode fogMode, TweakVersion.WorldFog worldFog)
    {
        if (fogMode != FogRenderer.FogMode.FOG_TERRAIN)
            return;

        int farPlaneDistance;
        int renderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();

        if (worldFog == TweakVersion.WorldFog.CLASSIC)
        {
            farPlaneDistance = switch (renderDistance)
            {
                case 2 -> 18;
                case 3, 4 -> 96;
                case 5, 6, 7, 8 -> 302;
                case 9, 10, 11, 12, 13, 14, 15 -> 824;
                default -> 1028;
            };

            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(farPlaneDistance * 0.8F);

            return;
        }

        if (worldFog == TweakVersion.WorldFog.ALPHA_R164)
        {
            farPlaneDistance = switch (renderDistance)
            {
                case 2, 3 -> 8;
                case 4, 5 -> 32;
                case 6, 7, 8, 9 -> 256;
                case 10, 11, 12, 13, 14, 15, 16 -> 512;
                default -> renderDistance * 32;
            };
        }
        else
            farPlaneDistance = renderDistance * 16;

        float linearEnding = switch (farPlaneDistance)
        {
            case 8 -> 4.6F;
            case 32 -> 2.2F;
            default -> 0.8F;
        };

        RenderSystem.setShaderFogStart(0.0F);
        RenderSystem.setShaderFogEnd(farPlaneDistance * linearEnding);
    }

    /**
     * Changes the game's horizon fog starting and ending points.
     * @param fogMode The fog mode that is rendering.
     * @param worldFog The current world fog type.
     */
    private static void setHorizonFog(FogRenderer.FogMode fogMode, TweakVersion.WorldFog worldFog)
    {
        if (fogMode != FogRenderer.FogMode.FOG_SKY || !ModConfig.Candy.disableHorizonFog())
            return;

        int farPlaneDistance;
        int renderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();

        if (worldFog == TweakVersion.WorldFog.CLASSIC)
        {
            farPlaneDistance = switch (renderDistance)
            {
                case 2, 3, 4 -> 128;
                case 5, 6, 7, 8 -> 256;
                default -> 512;
            };

            RenderSystem.setShaderFogStart(farPlaneDistance * 0.2F);
            RenderSystem.setShaderFogEnd(farPlaneDistance);

            return;
        }

        if (worldFog == TweakVersion.WorldFog.ALPHA_R164)
        {
            farPlaneDistance = switch (renderDistance)
            {
                case 2, 3 -> 8;
                case 4, 5 -> 32;
                case 6, 7, 8, 9 -> 256;
                default -> 512;
            };
        }
        else
            farPlaneDistance = renderDistance * 16;

        RenderSystem.setShaderFogStart(farPlaneDistance * 0.25F);
        RenderSystem.setShaderFogEnd(farPlaneDistance);
    }

    /**
     * Renders fog based on the current state of fog related tweaks.
     * @param fogMode The fog mode that is rendering.
     */
    private static void renderFog(FogRenderer.FogMode fogMode)
    {
        TweakVersion.WorldFog worldFog = ModConfig.Candy.getWorldFog();

        if (worldFog != TweakVersion.WorldFog.MODERN)
        {
            if (worldFog != TweakVersion.WorldFog.R17_R118)
            {
                setTerrainFog(fogMode, worldFog);
                setHorizonFog(fogMode, worldFog);
            }
            else
            {
                int renderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();
                int farPlaneDistance = renderDistance * 16;

                if (fogMode == FogRenderer.FogMode.FOG_TERRAIN)
                {
                    RenderSystem.setShaderFogStart(farPlaneDistance * 0.75F);
                    RenderSystem.setShaderFogEnd(farPlaneDistance);
                }
                else
                {
                    RenderSystem.setShaderFogStart(0.0F);
                    RenderSystem.setShaderFogEnd(farPlaneDistance);
                }
            }

            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }

    /**
     * Checks if the fog is currently being modified by another process such as a fluid or blindness effect.
     * @param camera The game's camera.
     * @return Whether the fog is currently being modified by a fluid, blindness, not overworld, or active mob effect.
     */
    private static boolean isFogModified(Camera camera)
    {
        return isFluidFog(camera) || isEntityBlind(camera) || !isOverworld(camera) || isMobEffectActive;
    }

    /**
     * Overrides fog in the overworld.
     * @param camera The game's camera.
     * @param fogMode The fog mode currently being rendered.
     */
    public static void setupFog(Camera camera, FogRenderer.FogMode fogMode)
    {
        if (isMobEffectActive)
            isMobEffectActive = false;

        if (!isFogModified(camera))
            renderFog(fogMode);

        if (ModConfig.Candy.disableVoidFog() || !ModConfig.isModEnabled() || !fogMode.equals(FogRenderer.FogMode.FOG_TERRAIN))
            return;

        Void.setFogStart(RenderSystem.getShaderFogStart());
        Void.setFogEnd(RenderSystem.getShaderFogEnd());
        Void.render(camera);
    }

    /**
     * Overrides fog in the nether
     * @param camera The game's camera.
     * @param fogMode The fog mode currently being rendered.
     */
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

    /* Water Fog */

    public static class Water
    {
        /* Interpolation Trackers */

        private static float currentDensity = 0.0F;
        private static final float[] CURRENT_RGB = new float[] { 0.0F, 0.0F, 0.0F };

        /* Water Fog Rendering */

        /**
         * Overrides fog rendering while the camera is in water.
         * @param farPlaneDistance Render distance.
         */
        public static void setupFog(float farPlaneDistance)
        {
            float density = 0.15F;
            float deltaTime = Minecraft.getInstance().getDeltaFrameTime();
            Entity entity = Minecraft.getInstance().getCameraEntity();

            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogStart(-8.0F);

            if (entity instanceof LivingEntity living)
            {
                if (living.hasEffect(MobEffects.WATER_BREATHING))
                    density = 0.8F;
                else
                    density = 0.15F + (float) EnchantmentHelper.getRespiration(living) * 0.1F;
            }

            float target = farPlaneDistance * Mth.clamp(density, 0.0F, 1.0F);

            if (currentDensity == 0.0F)
                currentDensity = target;

            currentDensity = MathUtil.moveTowards(currentDensity, target, deltaTime);

            RenderSystem.setShaderFogEnd(ModConfig.Candy.smoothWaterDensity() ? currentDensity : target);
        }

        /**
         * Gets a red value for water fog.
         * @param brightness The skylight brightness at camera's position.
         * @param respiration A respiration value if the enchantment is enabled.
         * @return A red fog color value (#.#F/255.0F).
         */
        public static float getRed(int brightness, float respiration)
        {
            float red = switch (brightness)
            {
                case 15, 14, 13, 12 -> 9.0F;
                case 11, 10, 9 -> 6.0F;
                case 8, 7, 6, 5, 4, 3 -> 5.0F;
                default -> 4.0F;
            };

            final float TARGET = (respiration > 0.0F ? 19.0F : red) / 255.0F;
            final float SPEED = Minecraft.getInstance().getDeltaFrameTime() * 0.005F;

            if (CURRENT_RGB[0] == 0.0F)
                CURRENT_RGB[0] = TARGET;

            CURRENT_RGB[0] = MathUtil.moveTowards(CURRENT_RGB[0], TARGET, SPEED);

            return ModConfig.Candy.smoothWaterColor() ? CURRENT_RGB[0] : TARGET;
        }

        /**
         * Gets a green value for water fog.
         * @param brightness The skylight brightness at camera's position.
         * @param respiration A respiration value if the enchantment is enabled.
         * @return A green fog color value (#.#F/255.0F).
         */
        public static float getGreen(int brightness, float respiration)
        {
            float green = switch (brightness)
            {
                case 15, 14, 13, 12 -> 16.0F;
                case 11, 10, 9 -> 11.0F;
                case 8, 7, 6 -> 8.0F;
                case 5, 4, 3 -> 7.0F;
                default -> 5.0F;
            };

            final float TARGET = (respiration > 0.0F ? 35.0F : green) / 255.0F;
            final float SPEED = Minecraft.getInstance().getDeltaFrameTime() * 0.005F;

            if (CURRENT_RGB[1] == 0.0F)
                CURRENT_RGB[1] = TARGET;

            CURRENT_RGB[1] = MathUtil.moveTowards(CURRENT_RGB[1], TARGET, SPEED);

            return ModConfig.Candy.smoothWaterColor() ? CURRENT_RGB[1] : TARGET;
        }

        /**
         * Gets a blue value for water fog.
         * @param brightness The skylight brightness at camera's position.
         * @param respiration A respiration value if the enchantment is enabled.
         * @return A blue fog color value (#.#F/255.0F).
         */
        public static float getBlue(int brightness, float respiration)
        {
            float blue = switch (brightness)
            {
                case 15, 14, 13, 12 -> 73.0F;
                case 11, 10, 9 -> 58.0F;
                case 8, 7, 6 -> 50.0F;
                case 5, 4, 3 -> 45.0F;
                default -> 41.0F;
            };

            final float TARGET = (respiration > 0.0F ? 150.0F : blue) / 255.0F;
            final float SPEED = Minecraft.getInstance().getDeltaFrameTime() * 0.005F;

            if (CURRENT_RGB[2] == 0.0F)
                CURRENT_RGB[2] = TARGET;

            CURRENT_RGB[2] = MathUtil.moveTowards(CURRENT_RGB[2], TARGET, SPEED);

            return ModConfig.Candy.smoothWaterColor() ? CURRENT_RGB[2] : TARGET;
        }
    }

    /* Void Fog */

    public static class Void
    {
        /* Interpolation Trackers */

        /**
         * This initializer is changed when entering/exiting a world.
         * An active initialization state signifies that we can start modifying interpolation values.
         */
        private static boolean isInitialized = false;

        /**
         * The fog color when lighten/darken based on the camera's received skylight and y-level.
         */
        private static double currentBrightness;

        /**
         * The fog starting position will shift based on surrounding block light and y-level.
         */
        private static float currentFogStart;

        /**
         * The fog ending position will shift based on surrounding block light and y-level.
         */
        private static float currentFogEnd;

        /**
         * This field will shift the sun/moon and sky fog transparency based on cave/void fog environment changes.
         */
        private static float currentCelestial;

        /**
         * This field will shift the star buffer transparency based on cave/void fog environment changes.
         */
        private static float currentStarAlpha;

        /**
         * This field tells the interpolation function the final transparency value for the star buffer.
         */
        private static float targetStarAlpha;

        /**
         * This field tells the interpolation function the stopping point for where fog should start.
         */
        private static float targetFogStart;

        /**
         * This field tells the interpolation function the stopping point for where fog should end.
         */
        private static float targetFogEnd;

        /**
         * This field tells the interpolation function how fast it should transition the fog start/end positions.
         * Using <code>Float.MAX_VALUE</code> will force an immediate shift.
         */
        private static float fogSpeedShift;

        /**
         * This field tells the interpolation function how fast it should transition star transparency.
         * Using <code>Float.MAX_VALUE</code> will force an immediate shift.
         */
        private static float starSpeedShift;

        /**
         * This field tells the interpolation function how fast it should transition the fog color.
         * Using <code>Float.MAX_VALUE</code> will force an immediate shift.
         */
        private static float colorSpeedShift;

        /**
         * This field tells the interpolation function how fast it should transition sun/moon and sky fog transparency.
         * Using <code>Float.MAX_VALUE</code> will force an immediate shift.
         */
        private static float celestialSpeedShift;

        /**
         * This field tells the interpolation function how fast it should transition fog brightness.
         * Using <code>Float.MAX_VALUE</code> will force an immediate shift.
         */
        private static float brightnessSpeedShift;

        /**
         * This is the ending point for all interpolation speed shifts.
         * The longer a player stays above the ground the faster transition speeds will get.
         */
        private static final float MAX_SHIFT = 1.0e5F;

        /* Void, Fog, and Sky Color Tracking  */

        private static final float[] CURRENT_VOID_RGB = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] CURRENT_FOG_RGB  = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] CURRENT_SKY_RGB  = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] TARGET_SKY_RGB   = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] TARGET_FOG_RGB   = new float[] { 0.0F, 0.0F, 0.0F };
        private static final float[] TARGET_VOID_RGB  = new float[] { 0.0F, 0.0F, 0.0F };

        /* Void, Fog, and Sky Color Setters/Getters */

        public static void setSkyRed(float red)     { TARGET_SKY_RGB[0] = red; }
        public static void setSkyGreen(float green) { TARGET_SKY_RGB[1] = green; }
        public static void setSkyBlue(float blue)   { TARGET_SKY_RGB[2] = blue; }

        public static float getSkyRed()   { return CURRENT_SKY_RGB[0]; }
        public static float getSkyGreen() { return CURRENT_SKY_RGB[1]; }
        public static float getSkyBlue()  { return CURRENT_SKY_RGB[2]; }

        public static void setFogStart(float start) { targetFogStart = start; }
        public static void setFogEnd(float end)     { targetFogEnd = end; }

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

        /* Star & Celestial Setters/Getters */

        public static void setStarAlpha(float value) { targetStarAlpha = value; }
        public static float getStarAlpha() { return currentStarAlpha; }
        public static float getCelestial() { return currentCelestial; }

        /* Altitude Speed Shift */

        /**
         * This enumeration tells the speed function which interpolation speed to shift.
         * Separating out the shift speeds is required to prevent visual artifacts.
         */
        private enum Shift { STAR, COLOR, FOG, CELESTIAL, BRIGHTNESS }

        /**
         * Get how fast interpolation should go.
         * @param modifier Used to slow down or speed up the current delta frame time.
         * @param shift A shift enumeration that tells this function which speed to change.
         * @return The speed of interpolation.
         */
        private static float getSpeed(float modifier, Shift shift)
        {
            Minecraft minecraft = Minecraft.getInstance();
            Camera camera = minecraft.gameRenderer.getMainCamera();

            if (isFogModified(camera))
            {
                fogSpeedShift = Float.MAX_VALUE;
                starSpeedShift = Float.MAX_VALUE;
                colorSpeedShift = Float.MAX_VALUE;
                celestialSpeedShift = Float.MAX_VALUE;
                brightnessSpeedShift = Float.MAX_VALUE;
            }
            else if (getYLevel(camera.getEntity()) > ModConfig.Candy.getVoidFogStart() || !isBelowHorizon())
            {
                switch (shift)
                {
                    case BRIGHTNESS -> brightnessSpeedShift = Mth.clamp(brightnessSpeedShift + 0.002F, 1.0F, MAX_SHIFT);
                    case CELESTIAL -> celestialSpeedShift = Mth.clamp(celestialSpeedShift + 0.001F, 1.0F, MAX_SHIFT);
                    case COLOR -> colorSpeedShift = Mth.clamp(colorSpeedShift + 0.01F, 1.0F, MAX_SHIFT);
                    case STAR -> starSpeedShift = Mth.clamp(starSpeedShift + 0.001F, 1.0F, MAX_SHIFT);
                    case FOG -> fogSpeedShift = Mth.clamp(fogSpeedShift + 0.05F, 1.0F, MAX_SHIFT);
                }
            }
            else
            {
                switch (shift)
                {
                    case BRIGHTNESS -> brightnessSpeedShift = Float.MAX_VALUE == brightnessSpeedShift ? MAX_SHIFT : 1.0F;
                    case CELESTIAL -> celestialSpeedShift = Float.MAX_VALUE == celestialSpeedShift ? MAX_SHIFT : 1.0F;
                    case COLOR -> colorSpeedShift = Float.MAX_VALUE == colorSpeedShift ? MAX_SHIFT : 1.0F;
                    case STAR -> starSpeedShift = Float.MAX_VALUE == starSpeedShift ? MAX_SHIFT : 1.0F;
                    case FOG -> fogSpeedShift = Float.MAX_VALUE == fogSpeedShift ? MAX_SHIFT : 1.0F;
                }
            }

            float speedShift = switch (shift)
            {
                case BRIGHTNESS -> brightnessSpeedShift;
                case CELESTIAL -> celestialSpeedShift;
                case COLOR -> colorSpeedShift;
                case STAR -> starSpeedShift;
                case FOG -> fogSpeedShift;
            };

            return modifier * minecraft.getDeltaFrameTime() * speedShift;
        }

        /* Cave/Void Fog Rendering */

        /**
         * Set the current transparency for the sun/moon and sky fog.
         */
        public static void setCelestialTransparency()
        {
            ClientLevel level = Minecraft.getInstance().level;

            if (level == null)
                return;

            final float[] RGB = RenderSystem.getShaderColor();
            final float RAIN = level.getRainLevel(Minecraft.getInstance().getDeltaFrameTime());
            final float ALPHA = Math.min(1.0F - RAIN, Void.getCelestial());

            if (Void.isRendering())
                RenderSystem.setShaderColor(RGB[0], RGB[1], RGB[2], ALPHA);
        }

        /**
         * This checks if a player is below a level's horizon height.
         * @return Whether the player is currently below the level's horizon height.
         */
        public static boolean isBelowHorizon()
        {
            Player player = Minecraft.getInstance().player;
            ClientLevel level = Minecraft.getInstance().level;
            float partialTick = Minecraft.getInstance().getDeltaFrameTime();

            if (player == null || level == null)
                return false;

            return player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level) < 0.0D;
        }

        /**
         * Get an entity's current y-level position.
         * @param entity The entity to get a y-level from.
         * @return The entity's current y-level as a <code>double</code>.
         */
        public static double getYLevel(Entity entity) { return entity.getY() - entity.level().getMinBuildHeight(); }

        /**
         * Checks if cave/void fog should be rendered. This is controlled by tweaks and other level environmental factors.
         * @param camera The game's camera.
         * @return Whether cave/void fog should render.
         */
        public static boolean isIgnored(Camera camera)
        {
            boolean isVoidFogDisabled = ModConfig.Candy.disableVoidFog();
            boolean isHeightOutOfBounds = ModConfig.Candy.getVoidFogStart() < getYLevel(camera.getEntity()) + 0.5D;
            boolean isNotSurvival = camera.getEntity() instanceof Player player && (player.isCreative() || player.isSpectator());
            boolean isCreativeOverride = !ModConfig.Candy.creativeVoidFog() && isNotSurvival;

            return isVoidFogDisabled || isHeightOutOfBounds || isCreativeOverride || !isBelowHorizon();
        }

        /**
         * Used by various interpolation setters to check if cave/void fog is currently rendering or should render.
         * @return If cave/void fog is enabled and initialized.
         */
        public static boolean isRendering()
        {
            return !ModConfig.Candy.disableVoidFog() && ModConfig.isModEnabled() && isInitialized;
        }

        /**
         * Gets the current skylight above the given entity.
         * @param entity The entity to check.
         * @return The current skylight value (0-15) above the given entity.
         */
        public static int getSkylight(Entity entity)
        {
            return WorldCommonUtil.getBrightness(entity.level(), LightLayer.SKY, entity.blockPosition());
        }

        /**
         * Determines whether the camera can see the sky.
         * @param camera The game's camera.
         * @return Whether the sky is visible (skylight = 15)
         */
        public static boolean canSeeSky(Camera camera)
        {
            return camera.getEntity().level().canSeeSky(camera.getBlockPosition());
        }

        /**
         * Gets the greatest brightness value from block light or skylight.
         * The encroachment tweak will add onto the calculated brightness.
         * @param entity The entity to check.
         * @return The greatest brightness value (0-15) emitted from block light or skylight.
         */
        private static int getLocalBrightness(Entity entity)
        {
            int encroachment = (int) ((1 - (ModConfig.Candy.getVoidFogEncroach() / 100.0F)) * 15);
            int brightness = ModConfig.Candy.shouldLightRemoveVoidFog() ?
                entity.level().getMaxLocalRawBrightness(entity.blockPosition()) :
                getSkylight(entity)
            ;

            return Mth.clamp(brightness + encroachment, 0, 15);
        }

        /**
         * Based on current render distance and modified by local brightness and y-level starting position.
         * @param entity The entity to get brightness and y-level data from.
         * @return A cave/void fog distance value based on local brightness and the player's y-level.
         */
        private static float getDistance(Entity entity)
        {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            double fogStart = ModConfig.Candy.getVoidFogStart();
            double fogDistance = getLocalBrightness(entity) / 16.0D + getYLevel(entity) / (fogStart == 0 ? 1 : fogStart);

            return fogDistance >= 1 ? renderDistance : (float) Mth.clamp(100.0D * Math.pow(Math.max(fogDistance, 0.0D), 2.0D), 5.0D, renderDistance);
        }

        /**
         * The change in cave/void fog distance based on y-level.
         * @param entity The entity to get y-level data from.
         * @return A delta value (0.0F-1.0F) based on where cave/void fog should start and the player's current y-level.
         */
        private static float getDistanceDelta(Entity entity)
        {
            return Math.max(1.0F, Math.min(1.0F, (1.0F - ((float) getYLevel(entity) - ModConfig.Candy.getVoidFogStart() - 15.0F) / 15.0F)));
        }

        /**
         * Checks if the current fog is 'thick' such as the fog generated by a warden.
         * @param camera The game's camera.
         * @return Whether the current fog should be considered 'thick'.
         */
        private static boolean isThick(Camera camera)
        {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level == null)
                return false;

            int x = Mth.floor(camera.getPosition().x());
            int y = Mth.floor(camera.getPosition().y());

            return mc.level.effects().isFoggyAt(x, y) || mc.gui.getBossOverlay().shouldCreateWorldFog();
        }

        /**
         * Gets where cave/void fog should start based on fog thickness and current cave/void fog distance.
         * @param camera The game's camera.
         * @param distance The calculated distance based on local brightness and y-level
         * @return Where cave/void fog should start.
         */
        private static float getFogStart(Camera camera, float distance)
        {
            return isThick(camera) ? distance * 0.05F : distance * Math.max(0.0F, 0.55F * (1.0F - (distance - 5.0F) / 127.0F));
        }

        /**
         * Gets where cave/void fog should end based on fog thickness and current cave/void fog distance.
         * @param camera The game's camera.
         * @param distance The calculated distance based on local brightness and y-level.
         * @return Where cave/void fog should end.
         */
        private static float getFogEnd(Camera camera, float distance)
        {
            return isThick(camera) ? Math.min(distance, 192.0F) / 2.0F : distance;
        }

        /**
         * Gets how bright the cave/void fog color should be based on skylight and y-level.
         * @param entity The entity to get y-level and skylight data from.
         * @return The current interpolation brightness value.
         */
        private static double getBrightness(Entity entity)
        {
            double brightness = Math.max(15.0D - (ModConfig.Candy.getVoidFogStart() - getYLevel(entity)), getSkylight(entity)) / 15.0F;
            currentBrightness = MathUtil.moveClampTowards(currentBrightness, brightness, getSpeed(0.002F, Shift.BRIGHTNESS), 0.0D, 1.0D);

            return currentBrightness;
        }

        /**
         * Changes fog, sky, and void colors.
         * @param camera The game's camera.
         */
        public static void setColor(Camera camera)
        {
            final float SPEED = getSpeed(0.005F, Shift.COLOR);

            if (isIgnored(camera) || isFogModified(camera) || canSeeSky(camera))
            {
                MathUtil.moveTowardsColor(CURRENT_FOG_RGB, TARGET_FOG_RGB, SPEED);
                MathUtil.moveTowardsColor(CURRENT_SKY_RGB, TARGET_SKY_RGB, SPEED);
                MathUtil.moveTowardsColor(CURRENT_VOID_RGB, TARGET_VOID_RGB, SPEED);
            }
            else
            {
                final float[] CURRENT_FOG = RenderSystem.getShaderFogColor();
                final int[] CUSTOM_FOG = ColorUtil.toHexRGBA(ModConfig.Candy.getVoidFogColor());

                final float LIGHT = (float) getBrightness(camera.getEntity());
                final float FOG_R = Mth.clamp(CURRENT_FOG[0] * LIGHT + (CUSTOM_FOG[0] / 255.0F), 0.0F, 1.0F);
                final float FOG_G = Mth.clamp(CURRENT_FOG[1] * LIGHT + (CUSTOM_FOG[1] / 255.0F), 0.0F, 1.0F);
                final float FOG_B = Mth.clamp(CURRENT_FOG[2] * LIGHT + (CUSTOM_FOG[2] / 255.0F), 0.0F, 1.0F);
                final float[] TARGET_RGB = new float[] { FOG_R, FOG_G, FOG_B };

                MathUtil.moveTowardsGrayscale(CURRENT_FOG_RGB, TARGET_RGB, SPEED);
                MathUtil.moveTowardsGrayscale(CURRENT_SKY_RGB, TARGET_RGB, SPEED);
                MathUtil.moveTowardsGrayscale(CURRENT_VOID_RGB, TARGET_RGB, SPEED);
            }

            RenderSystem.clearColor(CURRENT_FOG_RGB[0], CURRENT_FOG_RGB[1], CURRENT_FOG_RGB[2], 1.0F);
            RenderSystem.setShaderFogColor(CURRENT_FOG_RGB[0], CURRENT_FOG_RGB[1], CURRENT_FOG_RGB[2]);
        }

        /**
         * Resets the interpolation tracker initializer.
         * This occurs when the player exits a world.
         */
        public static void reset() { isInitialized = false; }

        /**
         * Initializes interpolation values to an acceptable starting point when joining a world.
         * @param camera The game's camera.
         */
        private static void initialize(Camera camera)
        {
            if (isInitialized)
                return;

            ArrayUtil.copy(TARGET_VOID_RGB, CURRENT_VOID_RGB);
            ArrayUtil.copy(TARGET_FOG_RGB, CURRENT_FOG_RGB);
            ArrayUtil.copy(TARGET_SKY_RGB, CURRENT_SKY_RGB);

            currentBrightness = getBrightness(camera.getEntity());
            currentStarAlpha = targetStarAlpha;
            currentFogStart = 0.0F;
            currentFogEnd = 0.0F;
            fogSpeedShift = MAX_SHIFT;
            starSpeedShift = MAX_SHIFT;
            colorSpeedShift = MAX_SHIFT;
            celestialSpeedShift = MAX_SHIFT;
            brightnessSpeedShift = MAX_SHIFT;
            currentCelestial = 1.0F;

            isInitialized = true;
        }

        /**
         * Renders cave/void fog.
         * @param camera The game's camera.
         */
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

            currentCelestial = MathUtil.moveTowards(currentCelestial, celestialTarget, getSpeed(0.07F, Shift.CELESTIAL));
            currentStarAlpha = MathUtil.moveTowards(currentStarAlpha, starTarget, getSpeed(0.03F, Shift.STAR));
            currentFogStart = MathUtil.moveTowards(currentFogStart, fogStartTarget, speed);
            currentFogEnd = MathUtil.moveTowards(currentFogEnd, fogEndTarget, speed);

            RenderSystem.setShaderFogStart(currentFogStart);
            RenderSystem.setShaderFogEnd(currentFogEnd);
        }
    }
}
