package mod.adrenix.nostalgic.mixin.duck;

import net.minecraft.world.item.ItemStack;

public interface SlotTracker extends CameraPitching
{
    void nt$setLastSlot(int slot);

    void nt$setReequip(boolean state);

    int nt$getLastSlot();

    boolean nt$getReequip();

    ItemStack nt$getLastItem();
}
