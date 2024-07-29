package mod.adrenix.nostalgic.mixin.tweak.candy.block_bed;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
    /**
     * Hides the player's model when the player is sleeping in first-person.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            ordinal = 0,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
        )
    )
    private <T extends LivingEntity> void nt_block_bed$hidePlayerInBed(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
        boolean isPlayer = entity.equals(Minecraft.getInstance().player);

        if (CandyTweak.HIDE_PLAYER_IN_BED.get() && isFirstPerson && isPlayer)
            poseStack.scale(0.0F, 0.0F, 0.0F);
    }
}
