package mod.adrenix.nostalgic.mixin.tweak.swing;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.config.SwingTweak;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting. Only checks
     * for global photosensitivity since this will break reequip animations if checking by item. Any item that needs to
     * disable their reequip progress, such as instantaneous bows, is done here.
     */
    @ModifyArg(
        index = 6,
        method = "renderHandsWithItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private float nt_swing$modifyEquippedProgress(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack itemStack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight)
    {
        if (!ModTweak.ENABLED.get())
            return equippedProgress;

        if (GameplayTweak.INSTANT_BOW.get() && itemStack.getItem().equals(Items.BOW))
            return 0.0F;

        return SwingTweak.USE_GLOBAL_SPEED.get() == SwingTweak.PHOTOSENSITIVE ? 0.0F : equippedProgress;
    }
}
