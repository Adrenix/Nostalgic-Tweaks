package mod.adrenix.nostalgic.helper.candy.level.fog;

import com.mojang.blaze3d.shaders.FogShape;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.WorldFog;
import mod.adrenix.nostalgic.util.client.CameraUtil;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.timer.LerpTimer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This utility class is used only by the client.
 */
public abstract class WaterFogRenderer
{
    /* Fields */

    private static final ArrayList<LerpTimer> LERP_TIMERS = new ArrayList<>();
    private static final LerpTimer DENSITY = makeTimer();
    private static final LerpTimer FOG_RED = makeTimer();
    private static final LerpTimer FOG_GREEN = makeTimer();
    private static final LerpTimer FOG_BLUE = makeTimer();

    /* Methods */

    /**
     * Create and track a new animation timer.
     *
     * @return A new {@link LerpTimer} instance.
     */
    private static LerpTimer makeTimer()
    {
        LerpTimer timer = LerpTimer.create(2L, TimeUnit.SECONDS);
        LERP_TIMERS.add(timer);

        return timer;
    }

    /**
     * Reset the initialization state of water fog.
     */
    public static void reset()
    {
        LERP_TIMERS.forEach(LerpTimer::clear);
    }

    /**
     * Gets the red value for water fog.
     *
     * @param brightness  The skylight brightness at the camera's position.
     * @param respiration A respiration value if the enchantment is enabled.
     * @return A red fog color value (#.#F/255.0F).
     */
    private static float getRed(int brightness, float respiration)
    {
        float red = switch (brightness)
        {
            case 15, 14, 13, 12 -> 9.0F;
            case 11, 10, 9 -> 6.0F;
            case 8, 7, 6, 5, 4, 3 -> 5.0F;
            default -> 4.0F;
        };

        FOG_RED.setTarget((respiration > 0.0F ? 19.0F : red) / 255.0F);

        return CandyTweak.SMOOTH_WATER_COLOR.get() ? FOG_RED.lerpFloat() : FOG_RED.endFloat();
    }

    /**
     * Gets the green value for water fog.
     *
     * @param brightness  The skylight brightness at the camera's position.
     * @param respiration A respiration value if the enchantment is enabled.
     * @return A green fog color value (#.#F/255.0F).
     */
    private static float getGreen(int brightness, float respiration)
    {
        float green = switch (brightness)
        {
            case 15, 14, 13, 12 -> 16.0F;
            case 11, 10, 9 -> 11.0F;
            case 8, 7, 6 -> 8.0F;
            case 5, 4, 3 -> 7.0F;
            default -> 5.0F;
        };

        FOG_GREEN.setTarget((respiration > 0.0F ? 35.0F : green) / 255.0F);

        return CandyTweak.SMOOTH_WATER_COLOR.get() ? FOG_GREEN.lerpFloat() : FOG_GREEN.endFloat();
    }

    /**
     * Gets the blue value for water fog.
     *
     * @param brightness  The skylight brightness at the camera's position.
     * @param respiration A respiration value if the enchantment is enabled.
     * @return A blue fog color value (#.#F/255.0F).
     */
    private static float getBlue(int brightness, float respiration)
    {
        float blue = switch (brightness)
        {
            case 15, 14, 13, 12 -> 73.0F;
            case 11, 10, 9 -> 58.0F;
            case 8, 7, 6 -> 50.0F;
            case 5, 4, 3 -> 45.0F;
            default -> 41.0F;
        };

        FOG_BLUE.setTarget((respiration > 0.0F ? 150.0F : blue) / 255.0F);

        return CandyTweak.SMOOTH_WATER_COLOR.get() ? FOG_BLUE.lerpFloat() : FOG_BLUE.endFloat();
    }

    /**
     * Setup water fog using current tweak context.
     *
     * @param camera   The {@link Camera} instance.
     * @param fogShape A {@link Consumer} that accepts a {@link FogShape} value.
     * @param fogStart A {@link Consumer} that accepts a float where the fog starts.
     * @param fogEnd   A {@link Consumer} that accepts a float where the fog ends.
     * @return Whether the mod changed the water fog.
     */
    public static boolean setupFog(Camera camera, Consumer<FogShape> fogShape, Consumer<Float> fogStart, Consumer<Float> fogEnd)
    {
        if (!CandyTweak.OLD_WATER_FOG_DENSITY.get() || camera.getFluidInCamera() != FogType.WATER)
            return false;

        float density = 16.0F;

        fogShape.accept(FogShape.SPHERE);
        fogStart.accept(-16.0F);

        if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity entity)
        {
            if (MobEffectUtil.hasWaterBreathing(entity))
                density = OverworldFogRenderer.getFarPlaneDistance(CandyTweak.OLD_WORLD_FOG.get()) * 0.3F;

            double respiration = NullableResult.getOrElse(entity.getAttribute(Attributes.OXYGEN_BONUS), 0.0D, AttributeInstance::getValue);

            if (respiration > 0.0D)
                density = Math.max(density, density * (float) respiration * 3.0F);
        }

        DENSITY.setTarget(Math.min(density, OverworldFogRenderer.getFarPlaneDistance(WorldFog.MODERN)));
        fogEnd.accept(CandyTweak.SMOOTH_WATER_DENSITY.get() ? DENSITY.lerpFloat() : DENSITY.endFloat());

        return true;
    }

    /**
     * Compute the water fog color using current tweak context.
     *
     * @param camera The {@link Camera} instance.
     * @param red    A {@link Consumer} that accepts a normalized float for the red fog color component.
     * @param green  A {@link Consumer} that accepts a normalized float for the green fog color component.
     * @param blue   A {@link Consumer} that accepts a normalized float for the blue fog color component.
     * @return Whether the mod changed the water fog color.
     */
    public static boolean setupColor(Camera camera, Consumer<Float> red, Consumer<Float> green, Consumer<Float> blue)
    {
        if (Minecraft.getInstance().level == null || camera.getFluidInCamera() != FogType.WATER || CameraUtil.isBlind(camera))
            return false;

        if (CandyTweak.OLD_WATER_FOG_COLOR.get() && !GameUtil.MOB_EFFECT_ACTIVE.get())
        {
            int brightness = Minecraft.getInstance().level.getBrightness(LightLayer.SKY, camera.getBlockPosition());
            double bonus = NullableResult.getOrElse(((LivingEntity) camera.getEntity()).getAttribute(Attributes.OXYGEN_BONUS), 0.0D, AttributeInstance::getValue);
            float respiration = (float) bonus * 0.2F;

            red.accept(getRed(brightness, respiration));
            green.accept(getGreen(brightness, respiration));
            blue.accept(getBlue(brightness, respiration));

            return true;
        }

        return false;
    }
}
