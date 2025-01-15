package mod.adrenix.nostalgic.mixin.tweak.animation.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
    /**
     * Brings back the old mob death topple animation where the entity still renders its walking animation during the
     * death animation.
     */
    @ModifyExpressionValue(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isAlive()Z"
        )
    )
    private boolean nt_animation_entity$modifyIsAliveOnWalk(boolean isAlive)
    {
        return AnimationTweak.OLD_MOB_DEATH_TOPPLE.get() || isAlive;
    }
}
