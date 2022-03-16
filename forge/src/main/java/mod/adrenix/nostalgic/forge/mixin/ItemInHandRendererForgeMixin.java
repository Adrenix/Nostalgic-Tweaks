package mod.adrenix.nostalgic.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import mod.adrenix.nostalgic.util.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererForgeMixin
{
    @Shadow
    private ItemStack mainHandItem;

    /**
     * Prevents visual bug from flashing the previously held item when pulling an item out of the main hand.
     * Controlled by reequip toggle.
     */
    @ModifyArg(
        remap = false,
        method = "renderHandsWithItems",
        index = 8,
        at = @At
        (
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraftforge/client/ForgeHooksClient;renderSpecificFirstPersonHand(Lnet/minecraft/world/InteractionHand;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFFFLnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    protected ItemStack onRenderItem(InteractionHand hand, PoseStack matrix, MultiBufferSource buffer, int packedLight, float partialTick, float interpolPitch, float swingProgress, float equipProgress, ItemStack itemStack)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return itemStack;

        return MixinInjector.Item.getLastItem(itemStack, this.mainHandItem, player.getMainHandItem(), (IReequipSlot) player);
    }

    /**
     * Forces the ForgeHooksClient.shouldCauseReequipAnimation to return false on the main hand,
     * so we can track what the last held item was.
     *
     * This prevents reequip animation issues when going from an item in the main hand to air.
     * Controlled by reequip toggle.
     */
    @Redirect(
        remap = false,
        method = "tick",
        at = @At
        (
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraftforge/client/ForgeHooksClient;shouldCauseReequipAnimation(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;I)Z"
        )
    )
    protected boolean onMainItemTick(ItemStack from, ItemStack to, int slot)
    {
        if (!MixinConfig.Animation.oldItemReequip())
            return ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);
        return true;
    }
}
