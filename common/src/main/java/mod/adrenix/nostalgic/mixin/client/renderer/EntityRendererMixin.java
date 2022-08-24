package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    /* Shadows */

    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;
    @Shadow protected abstract void renderNameTag(Entity entity, Component displayName, PoseStack matrixStack, MultiBufferSource buffer, int packedLight);

    /* Injections */

    /**
     * Renders the entity's ID as a renderable name. This is visible when the debug screen is active on a modded world.
     * Controlled by the debug entity ID tweak.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void NT$onRender(Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        if (ModConfig.Candy.debugEntityId() && NostalgicTweaks.isNetworkVerified() && Minecraft.getInstance().options.renderDebug)
        {
            this.renderNameTag(entity, Component.literal(Integer.toString(entity.getId())), poseStack, buffer, packedLight);
            callback.cancel();
        }
    }

    /**
     *
     */
    @Redirect(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void NT$onTagScale(PoseStack instance, float x, float y, float z, Entity entity)
    {
        if (ModConfig.Candy.oldNameTags())
        {
            double distance = this.entityRenderDispatcher.distanceToSqr(entity);
            float scale = (float) ((double) 0.0267F * (Math.sqrt(Math.sqrt(distance)) / 2.0D));

            instance.scale(-scale, -scale, scale);
        }
        else
            instance.scale(x, y, z);
    }
}
