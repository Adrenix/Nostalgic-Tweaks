package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExperienceOrbRenderer.class, priority = MixinPriority.APPLY_FIRST)
public abstract class ExperienceOrbRendererMixin extends EntityRenderer<ExperienceOrb>
{
    /* Dummy Constructor */

    private ExperienceOrbRendererMixin(EntityRendererProvider.Context context)
    {
        super(context);
    }

    /* Injections */

    /**
     * Disables the rendering of experience orbs.
     * Controlled by the disabled orb rendering tweak.
     */
    @Inject
    (
        method = "render(Lnet/minecraft/world/entity/ExperienceOrb;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onRender(ExperienceOrb entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableOrbRendering())
        {
            this.shadowRadius = 0.0F;
            callback.cancel();
        }
        else
            this.shadowRadius = 0.15F;
    }

    /**
     * The following argument modifications make the experience orbs render as fully opaque and fully bright.
     * Controlled by the old opaque experience tweak.
     */

    // Modify Alpha
    @ModifyArg
    (
        method = "vertex",
        index = 3,
        at = @At
        (
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private static int NT$onRenderOpaqueOrb(int vanilla)
    {
        return ModConfig.Candy.oldOpaqueExperience() ? 255 : vanilla;
    }

    // Override Brightness
    @ModifyArg
    (
        method = "vertex",
        index = 0,
        at = @At
        (
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;uv2(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private static int NT$onRenderFullBrightOrb(int vanilla)
    {
        return ModConfig.Candy.oldOpaqueExperience() ? 0xF000F0 : vanilla;
    }
}
