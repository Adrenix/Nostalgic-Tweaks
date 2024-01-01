package mod.adrenix.nostalgic.mixin.duck;

import net.minecraft.world.item.ItemStack;

public interface SlotTracker extends CameraPitching
{
    void NT$setLastSlot(int slot);

    void NT$setReequip(boolean state);

    int NT$getLastSlot();

    boolean NT$getReequip();

    ItemStack NT$getLastItem();
}
