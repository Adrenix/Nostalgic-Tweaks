package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.MixinUtil;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ExperienceOrbRenderer.class, priority = MixinUtil.PRIORITY)
public abstract class ExperienceOrbRendererMixin
{
    /**
     * Makes the experience orbs render as fully opaque and fully bright.
     * Controlled by the old opaque experience toggle.
     */

    @ModifyArg(method = "vertex", index = 3, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static int onRenderOpaqueOrb(int vanilla)
    {
        return MixinConfig.Candy.oldOpaqueExperience() ? 255 : vanilla;
    }

    @ModifyArg(method = "vertex", index = 0, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;uv2(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static int onRenderFullBrightOrb(int vanilla)
    {
        return MixinConfig.Candy.oldOpaqueExperience() ? 0xF000F0 : vanilla;
    }
}
