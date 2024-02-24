package mod.adrenix.nostalgic.mixin.tweak.candy.debug_entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.util.candy.DebugMixinHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    /**
     * Shows an entity's name tag if the entity's debug id should be shown.
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;shouldShowName(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    private boolean nt_debug_entity$shouldShowId(boolean shouldShowName, Entity entity)
    {
        return DebugMixinHelper.shouldShowDebugId(entity) || shouldShowName;
    }

    /**
     * Changes an entity's rendered name tag to the entity's id number when an entity's debug id should be shown.
     */
    @ModifyArg(
        index = 1,
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private Component nt_debug_entity$setNameTag(Entity entity, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
    {
        if (DebugMixinHelper.shouldShowDebugId(entity))
            return Component.literal(Integer.toString(entity.getId()));

        return displayName;
    }
}
