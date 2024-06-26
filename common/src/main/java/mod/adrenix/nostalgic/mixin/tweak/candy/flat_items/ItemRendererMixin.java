package mod.adrenix.nostalgic.mixin.tweak.candy.flat_items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.mixin.util.candy.flatten.FlatItemMixinHelper;
import mod.adrenix.nostalgic.mixin.util.candy.flatten.FlatModel;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = ItemRenderer.class,
    priority = MixinPriority.APPLY_LAST
)
public abstract class ItemRendererMixin
{
    /**
     * Sets up the rendering context so that items appear as 2D.
     */
    @Inject(
        method = "render",
        at = @At(
            ordinal = 0,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"
        )
    )
    private void nt_flat_items$onStartRender(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo callback)
    {
        boolean isValidContext = displayContext == ItemDisplayContext.GROUND || displayContext == ItemDisplayContext.FIXED;

        if (!CandyTweak.OLD_2D_ITEMS.get() || !GameUtil.isModelFlat(model) || !isValidContext)
            return;

        FlatItemMixinHelper.enableFlatRendering();
        FlatItemMixinHelper.flattenScaling(poseStack);
    }

    /**
     * Restores the rendering context as it was before 2D rendering.
     */
    @Inject(
        method = "render",
        at = @At("RETURN")
    )
    private void nt_flat_items$onFinishRender(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo callback)
    {
        if (FlatItemMixinHelper.isRendering2D())
            FlatItemMixinHelper.disableFlatRendering();
    }

    /**
     * Disables the enchantment glint from showing on 2D items in specific display contexts.
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"
        )
    )
    private boolean nt_flat_items$disableItemFoil(boolean hasFoil, ItemStack itemStack, ItemDisplayContext displayContext)
    {
        if (CandyTweak.DISABLE_ENCHANTED_GROUND_ITEMS.get() && displayContext == ItemDisplayContext.GROUND)
            return false;

        if (CandyTweak.DISABLE_ENCHANTED_STATIC_ITEMS.get() && displayContext == ItemDisplayContext.FIXED)
            return false;

        return hasFoil;
    }

    /**
     * Wraps the baked item model with a modded "flat" model. While this approach may seem odd, it is necessary since
     * this supports mods (such as Sodium) that override item model quad rendering.
     */
    @ModifyArg(
        index = 0,
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
        )
    )
    private BakedModel nt_flat_items$setRenderedModel(BakedModel model, ItemStack itemStack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer consumer)
    {
        if (!FlatItemMixinHelper.isRendering2D())
            return model;

        return new FlatModel(model, poseStack);
    }
}
