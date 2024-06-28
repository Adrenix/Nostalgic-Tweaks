package mod.adrenix.nostalgic.mixin.util.animation;

import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NumberHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * This utility class is used only by the client.
 */
public abstract class HeldItemMixinHelper
{
    /**
     * Used to help with old reequipping logic by preventing visual glitches when the player pulls an item out of their
     * hand.
     *
     * @param originalItemStack The original {@link ItemStack}.
     * @param heldItemStack     The item renderer's held {@link ItemStack}.
     * @param playerItemStack   The player's {@link ItemStack}.
     * @param slotTracker       The {@link SlotTracker} instance.
     * @return The {@link ItemStack} that should be considered the last held item.
     */
    public static ItemStack getLastHeldItem(ItemStack originalItemStack, ItemStack heldItemStack, ItemStack playerItemStack, SlotTracker slotTracker)
    {
        boolean isUnequipped = heldItemStack.isEmpty() && playerItemStack.isEmpty();

        if (!AnimationTweak.OLD_ITEM_REEQUIP.get() || !isUnequipped)
            return originalItemStack;

        return slotTracker.nt$getLastItem();
    }

    /**
     * Apply the old item reequip logic.
     *
     * @param heldItem   A {@link Holder} that holds the {@code ItemInHandRenderer} held {@link ItemStack} instance.
     * @param handHeight A {@link NumberHolder} that holds the {@code ItemInHandRenderer} hand height.
     */
    public static void oldReequipLogic(Holder<ItemStack> heldItem, NumberHolder<Float> handHeight)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (!AnimationTweak.OLD_ITEM_REEQUIP.get() || player == null)
            return;

        SlotTracker slotTracker = (SlotTracker) player;
        ItemStack mainHandItem = player.getMainHandItem();
        int selectedSlot = player.getInventory().selected;

        if (mainHandItem.isEmpty() && heldItem.get().isEmpty() && !slotTracker.nt$getReequip())
            slotTracker.nt$setReequip(false);
        else if (selectedSlot != slotTracker.nt$getLastSlot())
        {
            slotTracker.nt$setLastSlot(selectedSlot);
            slotTracker.nt$setReequip(true);
        }

        boolean isUnequipped = heldItem.get().isEmpty() && mainHandItem.isEmpty();
        boolean isItemChanged = !heldItem.get().is(mainHandItem.getItem());
        boolean isSlotUpdated = selectedSlot == slotTracker.nt$getLastSlot() && isItemChanged && !slotTracker.nt$getReequip();
        boolean isHandChanged = isUnequipped && !slotTracker.nt$getLastItem().isEmpty() && !slotTracker.nt$getReequip();

        if (isSlotUpdated || isHandChanged)
            slotTracker.nt$setReequip(true);

        if (isUnequipped)
            heldItem.set(slotTracker.nt$getLastItem());

        if (selectedSlot == slotTracker.nt$getLastSlot() && !slotTracker.nt$getReequip())
            heldItem.set(player.getMainHandItem());

        if (AnimationTweak.OLD_ITEM_COOLDOWN.get())
            handHeight.set(Mth.clamp(handHeight.get() + (slotTracker.nt$getReequip() ? -0.4F : 0.4F), 0.0F, 1.0F));
        else
        {
            float scale = player.getAttackStrengthScale(1.0F);
            float value = (!slotTracker.nt$getReequip() ? scale * scale * scale : 0.0f) - handHeight.get();

            handHeight.set(handHeight.get() + Mth.clamp(value, -0.4F, 0.4F));
        }

        if (handHeight.get() < 0.1F)
            slotTracker.nt$setReequip(false);
    }
}
