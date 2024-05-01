package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
    /**
     * Prevents the death fall-over animation for player entities.
     */
    @ModifyExpressionValue(
        method = "setupRotations",
        at = @At(
            ordinal = 0,
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/LivingEntity;deathTime:I"
        )
    )
    private int nt_player_animation$modifyDeathTimeForTopple(int deathTime, LivingEntity entity)
    {
        return AnimationTweak.PREVENT_DEATH_TOPPLE.get() && entity instanceof Player ? 0 : deathTime;
    }
}
