package mod.adrenix.nostalgic.mixin.tweak.candy.experience_particles;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = ExperienceOrbRenderer.class,
    priority = MixinPriority.APPLY_FIRST
)
public abstract class ExperienceOrbRendererMixin extends EntityRenderer<ExperienceOrb>
{
    /* Fake Constructor */

    private ExperienceOrbRendererMixin(EntityRendererProvider.Context context)
    {
        super(context);
    }

    /* Unique */

    @Unique private float nt$originalShadowRadius;

    /* Injections */

    /**
     * Copies the original shadow radius, so it can be restored when the disable orb rendering tweak changes state.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_experience_particles$onMakeOrbRenderer(CallbackInfo callback)
    {
        this.nt$originalShadowRadius = this.shadowRadius;
    }

    /**
     * Disables rendering of an experience orb.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/ExperienceOrb;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
        )
    )
    private void nt_experience_particles$setOrbInvisible(ExperienceOrb orb, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        if (GameplayTweak.DISABLE_ORB_RENDERING.get())
        {
            poseStack.scale(0.0F, 0.0F, 0.0F);
            this.shadowRadius = 0.0F;
        }
        else
            this.shadowRadius = this.nt$originalShadowRadius;
    }

    /**
     * Makes the experience orb fully opaque.
     */
    @ModifyArg(
        index = 3,
        method = "vertex",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private static int nt_experience_particles$setOpaqueOrb(int alpha)
    {
        return CandyTweak.OLD_OPAQUE_EXPERIENCE.get() ? 255 : alpha;
    }

    /**
     * Makes the experience orb full bright.
     */
    @ModifyArg(
        index = 0,
        method = "vertex",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setLight(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private static int nt_experience_particles$setFullBrightOrb(int packedLight)
    {
        return CandyTweak.OLD_OPAQUE_EXPERIENCE.get() ? LightTexture.FULL_BRIGHT : packedLight;
    }
}
