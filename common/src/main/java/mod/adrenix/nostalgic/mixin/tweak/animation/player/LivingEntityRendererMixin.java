package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.helper.animation.ClassicWalkHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    /**
     * Applies the old classic walk bobbing animation.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
        )
    )
    private <T extends LivingEntity> void nt_player_animation$applyClassicWalkBobbing(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        if (AnimationTweak.OLD_CLASSIC_WALK_BOBBING.get() && entity instanceof Player)
            ClassicWalkHelper.applyBobbing(entity, poseStack, partialTick);
    }
}
