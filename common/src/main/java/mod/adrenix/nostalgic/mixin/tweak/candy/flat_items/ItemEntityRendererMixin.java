package mod.adrenix.nostalgic.mixin.tweak.candy.flat_items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
    /* Shadows */

    @Shadow @Final private ItemRenderer itemRenderer;

    /* Injections */

    /**
     * Forces the item entity's rotation to always face the player.
     */
    @ModifyExpressionValue(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/math/Axis;rotation(F)Lorg/joml/Quaternionf;"
        )
    )
    private Quaternionf nt_flat_items$setItemRotation(Quaternionf quaternion, ItemEntity entity)
    {
        if (!CandyTweak.OLD_2D_ITEMS.get())
            return quaternion;

        boolean isModelFlat = GameUtil.isModelFlat(this.itemRenderer.getModel(entity.getItem(), null, null, 0));

        if (isModelFlat)
            return Axis.YP.rotationDegrees(180.0F - Minecraft.getInstance().gameRenderer.getMainCamera().getYRot());

        return quaternion;
    }
}
