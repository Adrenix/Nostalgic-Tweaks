package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    /* Shadows */

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
}
