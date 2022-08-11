package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
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

    @Shadow protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, MatrixStack matrixStack, VertexConsumer buffer);
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
    private void NT$onGetGlint(ItemRenderer renderer, BakedModel model, ItemStack itemStack, int combinedLight, int combinedOverlay, MatrixStack poseStack, VertexConsumer consumer, ItemStack unused1, ModelTransformation.Mode transformer, boolean leftHand, MatrixStack unused2, VertexConsumerProvider buffer)
    {
        boolean noGlobalGlint = ModConfig.Candy.oldFlatEnchantment() && ModClientUtil.Item.isLightingFlat();
        boolean noEntityGlint = ModConfig.Candy.oldFloatingItems() && transformer == ModelTransformation.Mode.GROUND;
        boolean noFrameGlint = ModConfig.Candy.oldFlatFrames() && transformer == ModelTransformation.Mode.FIXED;

        if (noGlobalGlint && (noEntityGlint || noFrameGlint))
        {
            Block block;
            boolean isFoilDirect = transformer.isFirstPerson() || !(itemStack.getItem() instanceof BlockItem) || !((block = ((BlockItem) itemStack.getItem()).getBlock()) instanceof TransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            RenderLayer renderType = RenderLayers.getItemLayer(itemStack, isFoilDirect);
            consumer = isFoilDirect ? ItemRenderer.getDirectItemGlintConsumer(buffer, renderType, true, false) : ItemRenderer.getItemGlintConsumer(buffer, renderType, true, false);
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
    private void NT$onRenderGuiItemDecorations(TextRenderer font, ItemStack stack, int x, int y, String text, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldDurabilityColors() && stack.isDamaged())
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();

            Tessellator tesselator = Tessellator.getInstance();
            BufferBuilder builder = tesselator.getBuffer();

            double health = (double) stack.getDamage() / (double) stack.getMaxDamage();
            int width = Math.round(13.0F - (float) health * 13.0F);
            int damage = (int) Math.round(255D - ((double) stack.getDamage() * 255D) / (double) stack.getMaxDamage());

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
