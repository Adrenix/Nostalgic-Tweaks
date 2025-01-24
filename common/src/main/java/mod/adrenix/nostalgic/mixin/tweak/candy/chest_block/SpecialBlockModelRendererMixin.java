package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.helper.candy.block.ChestHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpecialBlockModelRenderer.class)
public abstract class SpecialBlockModelRendererMixin
{
    /**
     * Prevents rendering of the animated chest block model if the given block is an old chest.
     */
    @WrapWithCondition(
        method = "renderByBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;render(Ljava/lang/Object;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V"
        )
    )
    private <T> boolean nt_chest_block$shouldRenderSpecial(SpecialModelRenderer<T> renderer, @Nullable T t, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType, @Local(argsOnly = true) Block block)
    {
        return !ChestHelper.isOld(block);
    }
}
