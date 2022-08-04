package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
    /**
     * Prevents the death animation from playing for player entities.
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
        return ModConfig.Animation.oldDeath() && entity instanceof Player ? 0 : entity.deathTime;
    }
}
