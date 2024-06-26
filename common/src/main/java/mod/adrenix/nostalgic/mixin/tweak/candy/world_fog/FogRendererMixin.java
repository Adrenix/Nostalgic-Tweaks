package mod.adrenix.nostalgic.mixin.tweak.candy.world_fog;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = FogRenderer.class,
    priority = MixinPriority.APPLY_FIRST
)
public abstract class FogRendererMixin
{
    /* Shadows */

    @Shadow
    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity entity, float partialTick)
    {
        return null;
    }

    /* Injections */

    /**
     * Tracks when the mob effect fog is active.
     */
    @Inject(
        method = "setupFog",
        at = @At("HEAD")
    )
    private static void nt_world_fog$onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo callback)
    {
        FogRenderer.MobEffectFogFunction fogFunction = getPriorityFogFunction(camera.getEntity(), partialTick);

        if (fogFunction != null && camera.getEntity() instanceof LivingEntity entity)
            GameUtil.MOB_EFFECT_ACTIVE.set(entity.getEffect(fogFunction.getMobEffect()) != null);
        else
            GameUtil.MOB_EFFECT_ACTIVE.disable();
    }

    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     */
    @ModifyExpressionValue(
        method = "setupColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"
        )
    )
    private static float[] nt_world_fog$onSunriseSunsetFogColor(float[] color)
    {
        return CandyTweak.OLD_SUNRISE_SUNSET_FOG.get() ? null : color;
    }

    /**
     * Rotates where the sunrise/sunset fog color is.
     */
    @ModifyArg(
        method = "setupColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector3f;dot(Lorg/joml/Vector3fc;)F"
        )
    )
    private static Vector3fc nt_world_fog$onSetupFogColorDirection(Vector3fc vector)
    {
        return CandyTweak.OLD_SUNRISE_AT_NORTH.get() ? new Vector3f(0.0F, 0.0F, -vector.x()) : vector;
    }
}
