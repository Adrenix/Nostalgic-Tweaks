package mod.adrenix.nostalgic.mixin.tweak.candy.name_tag;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.helper.candy.SupporterRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    /* Shadows */

    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;

    /* Injections */

    /**
     * Renders the entity's name tag bigger the further away the player is from the entity.
     */
    @WrapOperation(
        method = "renderNameTag",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
        )
    )
    private void nt_name_tag$setScale(PoseStack poseStack, float x, float y, float z, Operation<Void> operation, Entity entity)
    {
        if (CandyTweak.OLD_NAME_TAGS.get())
        {
            double distance = this.entityRenderDispatcher.distanceToSqr(entity);
            float scale = (float) ((double) 0.0267F * (Math.sqrt(Math.sqrt(distance)) / 2.0D));

            x = scale;
            y = -scale;
            z = -scale;
        }

        operation.call(poseStack, x, y, z);
    }

    /**
     * Renders supporter visual effects on top of their name tags.
     */
    @WrapOperation(
        method = "renderNameTag",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"
        )
    )
    private int nt_name_tag$renderSupporter(Font font, Component displayName, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLight, Operation<Integer> operation, Entity entity, Component arg2, PoseStack poseStack)
    {
        if (SupporterRenderer.isNotSupporter(displayName) || entity.isDiscrete())
            return operation.call(font, displayName, x, y, color, dropShadow, matrix, bufferSource, displayMode, backgroundColor, packedLight);

        return SupporterRenderer.render(displayName, x, y, poseStack, bufferSource, backgroundColor, packedLight);
    }
}
