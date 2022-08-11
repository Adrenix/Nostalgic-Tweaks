package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
    /**
     * Prevents the death fall-over animation for player entities.
     * Controlled by the old death animation tweak.
     */
    @Redirect
    (
        method = "setupRotations",
        at = @At
        (
            ordinal = 0,
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/LivingEntity;deathTime:I"
        )
    )
    private int NT$onSetupRotations(LivingEntity entity)
    {
        return ModConfig.Animation.disablePlayerTopple() && entity instanceof PlayerEntity ? 0 : entity.deathTime;
    }
}
