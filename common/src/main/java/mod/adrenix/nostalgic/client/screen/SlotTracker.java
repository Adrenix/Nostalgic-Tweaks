package mod.adrenix.nostalgic.client.screen;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.inventory.Slot;

/**
 * Tracks specific slots that reside in the player's inventory menu.
 * Slots have had their x, y positions mutable via access widening.
 */

public enum SlotTracker
{
    CRAFT_TOP_LEFT(98, 18),
    CRAFT_TOP_RIGHT(116, 18),
    CRAFT_BOTTOM_LEFT(98, 36),
    CRAFT_BOTTOM_RIGHT(116, 36),
    CRAFT_RESULT(154, 28),
    OFF_HAND(77, 62);

    private int x;
    private int y;
    private final int vanillaX;
    private final int vanillaY;

    SlotTracker(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.vanillaX = x;
        this.vanillaY = y;
    }

    public boolean isEqualTo(Slot slot) { return slot.x == this.x && slot.y == this.y; }
    public void reset(Slot slot) { this.move(slot, this.vanillaX, this.vanillaY); }

    public void move(Slot slot, int x, int y)
    {
        if (isEqualTo(slot))
        {
            this.x = x;
            this.y = y;

            slot.x = x;
            slot.y = y;
        }
    }

    public void moveOrReset(Slot slot, int x, int y)
    {
        if (ModConfig.Candy.oldInventory())
            move(slot, x, y);
        else
            reset(slot);
    }
}
