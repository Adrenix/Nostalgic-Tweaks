package mod.adrenix.nostalgic.mixin.duck;

import net.minecraft.world.item.ItemStack;

/**
 * Adds the ability to the player class to keep track of previous slot, previous slot item, and reequipping progress.
 */

public interface IReequipSlot extends ICameraPitch
{
    void setLastSlot(int slot);
    void setReequip(boolean state);

    int getLastSlot();
    boolean getReequip();
    ItemStack getLastItem();
}
