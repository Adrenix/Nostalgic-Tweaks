package mod.adrenix.nostalgic.mixin.tweak.swing;

import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.config.SwingTweak;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting. Only checks
     * for global photosensitivity since this will break reequip animations if checking by item.
     */
    @ModifyArg(
        index = 6,
        method = "renderHandsWithItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private float nt_swing$modifyEquippedProgress(float equippedProgress)
    {
        if (!ModTweak.ENABLED.get())
            return equippedProgress;

        return SwingTweak.USE_GLOBAL_SPEED.get() == SwingTweak.PHOTOSENSITIVE ? 0.0F : equippedProgress;
    }
}
