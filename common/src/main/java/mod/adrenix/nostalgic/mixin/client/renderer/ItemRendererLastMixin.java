package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

/**
 * To help with mod compatibility, the mixins defined in this class will be applied last. Mixin injections here will
 * occur after all other mods have finished their required modifications.
 */

@Mixin(
    value = ItemRenderer.class,
    priority = MixinPriority.APPLY_LAST
)
public abstract class ItemRendererLastMixin
{
    /**
     * Used to change the normal matrix on the pose stack depending on the quad we're rendering. Controlled by flat
     * rendering state in the mod utility helper class.
     */
    @ModifyVariable(
        method = "renderQuadList",
        at = @At("LOAD")
    )
    private BakedQuad NT$onRenderQuad(BakedQuad quad, PoseStack poseStack)
    {
        if (ItemClientUtil.isLightingFlat())
            ItemClientUtil.setNormalQuad(poseStack.last(), quad);

        return quad;
    }

    /**
     * Used to change the quads list so that only the front face of the model renders. Controlled by the old 2D
     * rendering tweak.
     */
    @ModifyVariable(
        method = "renderQuadList",
        at = @At("LOAD"),
        argsOnly = true
    )
    private List<BakedQuad> NT$onRenderQuadList(List<BakedQuad> quads)
    {
        return ModConfig.Candy.oldFlatRendering() &&
            !ModTracker.OPTIFINE.isInstalled() ? ItemClientUtil.getSprites(quads) : quads;
    }
}