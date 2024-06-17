package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockMixinHelper;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockRenderer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin<T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel>
    extends ItemInHandLayer<T, M>
{
    /* Fake Constructor */

    private PlayerItemInHandLayerMixin(RenderLayerParent<T, M> renderer, ItemInHandRenderer itemInHandRenderer)
    {
        super(renderer, itemInHandRenderer);
    }

    /* Injections */

    /**
     * Applies third-person view sword blocking animation.
     */
    @WrapWithCondition(
        method = "renderArmWithItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/ItemInHandLayer;renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private boolean nt_combat_player$modifyRenderArmWithSwordBlocking(ItemInHandLayer<?, ?> layer, LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
    {
        if (entity instanceof Player player && player.getUseItem() == itemStack && SwordBlockMixinHelper.isBlocking(player))
        {
            SwordBlockRenderer.renderModelBlockingInHand(poseStack, itemStack, displayContext, player, arm, this.getParentModel(), buffer, packedLight);

            return false;
        }

        return true;
    }
}
