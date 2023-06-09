package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererFirstMixin
{
    /* Shadows */

    @Shadow
    protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer vertexConsumer);

    /* Injections */

    /**
     * Disables the enchantment glint on floating items. Controlled by the old 2d enchantment tweak.
     */
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
        )
    )
    private void NT$onGetGlint(ItemRenderer renderer, BakedModel model, ItemStack itemStack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer vertexConsumer, ItemStack copiedItemStack, ItemDisplayContext context, boolean leftHand, PoseStack copiedPoseStack, MultiBufferSource buffer)
    {
        boolean noGlobalGlint = ModConfig.Candy.oldFlatEnchantment() && ItemClientUtil.isLightingFlat();
        boolean noEntityGlint = ModConfig.Candy.oldFloatingItems() && context == ItemDisplayContext.GROUND;
        boolean noFrameGlint = ModConfig.Candy.oldFlatFrames() && context == ItemDisplayContext.FIXED;

        if (noGlobalGlint && (noEntityGlint || noFrameGlint))
        {
            Item item = itemStack.getItem();

            boolean isNotBlockItem = ClassUtil.isNotInstanceOf(item, BlockItem.class);
            boolean isNotHalfTransparentBlock = false;
            boolean isNotStainedGlassPaneBlock = false;

            if (item instanceof BlockItem blockItem)
            {
                Block block = blockItem.getBlock();

                isNotHalfTransparentBlock = ClassUtil.isNotInstanceOf(block, HalfTransparentBlock.class);
                isNotStainedGlassPaneBlock = ClassUtil.isNotInstanceOf(block, StainedGlassPaneBlock.class);
            }

            boolean isNotIgnoredState = isNotBlockItem || isNotHalfTransparentBlock && isNotStainedGlassPaneBlock;
            boolean isFoilDirect = context.firstPerson() || isNotIgnoredState;

            RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, isFoilDirect);
            vertexConsumer = isFoilDirect ? ItemRenderer.getFoilBufferDirect(buffer, renderType, true, false) : ItemRenderer.getFoilBuffer(buffer, renderType, true, false);
        }

        this.renderModelLists(model, itemStack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
    }
}
