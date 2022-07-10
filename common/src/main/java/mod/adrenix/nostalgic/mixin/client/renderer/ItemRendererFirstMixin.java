package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererFirstMixin
{
    /* Shadows */

    @Shadow protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer);
    @Shadow protected abstract void fillRect(BufferBuilder builder, int x, int y, int w, int h, int r, int g, int b, int alpha);

    /* Injections */

    /**
     * Disables the enchantment glint on floating items.
     * Controlled by the old 2d enchantment tweak.
     */
    @Redirect
    (
        method = "render",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
        )
    )
    private void NT$onGetGlint(ItemRenderer renderer, BakedModel model, ItemStack itemStack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer consumer, ItemStack unused1, ItemTransforms.TransformType transformer, boolean leftHand, PoseStack unused2, MultiBufferSource buffer)
    {
        boolean noGlobalGlint = ModConfig.Candy.oldFlatEnchantment() && ModClientUtil.Item.isLightingFlat();
        boolean noEntityGlint = ModConfig.Candy.oldFloatingItems() && transformer == ItemTransforms.TransformType.GROUND;
        boolean noFrameGlint = ModConfig.Candy.oldFlatFrames() && transformer == ItemTransforms.TransformType.FIXED;

        if (noGlobalGlint && (noEntityGlint || noFrameGlint))
        {
            Block block;
            boolean isFoilDirect = transformer.firstPerson() || !(itemStack.getItem() instanceof BlockItem) || !((block = ((BlockItem) itemStack.getItem()).getBlock()) instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, isFoilDirect);
            consumer = isFoilDirect ? ItemRenderer.getFoilBufferDirect(buffer, renderType, true, false) : ItemRenderer.getFoilBuffer(buffer, renderType, true, false);
        }

        this.renderModelLists(model, itemStack, combinedLight, combinedOverlay, poseStack, consumer);
    }

    /**
     * Simulates the old durability bar colors.
     * Controlled by the old damage colors tweak.
     */
    @Inject
    (
        method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
        at = @At(value = "RETURN")
    )
    private void NT$onRenderGuiItemDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldDurabilityColors() && stack.isDamaged())
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            double health = (double) stack.getDamageValue() / (double) stack.getMaxDamage();
            int width = Math.round(13.0F - (float) health * 13.0F);
            int damage = (int) Math.round(255D - ((double) stack.getDamageValue() * 255D) / (double) stack.getMaxDamage());

            int rgb_fg = 255 - damage << 16 | damage << 8;
            int rgb_bg = (255 - damage) / 4 << 16 | 0x3F00;

            this.fillRect(builder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(builder, x + 2, y + 13, 12, 1, rgb_bg >> 16 & 0x0FF, rgb_bg >> 8 & 0x0FF, rgb_bg & 0x0FF, 255);
            this.fillRect(builder, x + 2, y + 13, width, 1, rgb_fg >> 16 & 0x0FF, rgb_fg >> 8 & 0x0FF, rgb_fg & 0x0FF, 255);

            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
