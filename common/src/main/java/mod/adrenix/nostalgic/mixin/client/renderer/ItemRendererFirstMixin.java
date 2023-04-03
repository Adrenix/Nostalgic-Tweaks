package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

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

    /**
     * Simulates the old durability bar colors. Controlled by the old damage colors tweak.
     */
    @Inject(
        method = "renderGuiItemDecorations(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
        at = @At(value = "RETURN")
    )
    private void NT$onRenderGuiItemDecorations(PoseStack poseStack, Font font, ItemStack itemStack, int x, int y, String text, CallbackInfo callback)
    {
        if (itemStack.isEmpty())
            return;

        if (ModConfig.Candy.oldDurabilityColors() && itemStack.isBarVisible())
        {
            RenderSystem.disableDepthTest();

            double health = (double) itemStack.getDamageValue() / (double) itemStack.getMaxDamage();
            double healthRemaining = ((double) itemStack.getDamageValue() * 255.0D) / (double) itemStack.getMaxDamage();

            int width = Math.round(13.0F - (float) health * 13.0F);
            int damage = (int) Math.round(255.0D - healthRemaining);

            Color damageForegroundColor = new Color(255 - damage << 16 | damage << 8);
            Color damageBackgroundColor = new Color((255 - damage) / 4 << 16 | 0x3F00);

            int startX = x + 2;
            int startY = y + 13;

            GuiComponent.fill(poseStack, startX, startY, startX + 13, startY + 2, 0xFF000000);
            GuiComponent.fill(poseStack, startX, startY, startX + 12, startY + 1, damageBackgroundColor.getRGB());
            GuiComponent.fill(poseStack, startX, startY, startX + width, startY + 1, damageForegroundColor.getRGB());

            RenderSystem.enableDepthTest();
        }
    }
}
