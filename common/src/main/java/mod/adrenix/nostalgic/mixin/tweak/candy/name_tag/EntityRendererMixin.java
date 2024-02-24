package mod.adrenix.nostalgic.mixin.tweak.candy.name_tag;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
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

            x = -scale;
            y = -scale;
            z = -scale;
        }

        operation.call(poseStack, x, y, z);
    }
}
