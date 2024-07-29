package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.inventory.Slot;

/**
 * Tracks specific slots that reside in the player's inventory menu. Slots have had their x, y positions mutable via
 * access widening.
 */
public enum SlotTracker
{
    CRAFT_TOP_LEFT(98, 18),
    CRAFT_TOP_RIGHT(116, 18),
    CRAFT_BOTTOM_LEFT(98, 36),
    CRAFT_BOTTOM_RIGHT(116, 36),
    CRAFT_RESULT(154, 28),
    OFF_HAND(77, 62);

    /* Fields */

    private int x;
    private int y;
    private final int originalX;
    private final int originalY;

    /* Constructor */

    /**
     * Create a new slot tracker.
     *
     * @param x The original x-position of the slot.
     * @param y The original y-position of the slot.
     */
    SlotTracker(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
    }

    /* Methods */

    /**
     * Checks if this slot's position is equal to the position of the given slot.
     *
     * @param slot A {@link Slot} instance.
     * @return Whether this slot's position matches the given slot's position.
     */
    public boolean isEqualTo(Slot slot)
    {
        return (slot.x == this.x && slot.y == this.y) || (slot.x == this.originalX && slot.y == this.originalY);
    }

    /**
     * Moves the given slot to the original position of this slot.
     *
     * @param slot A {@link Slot} instance.
     */
    public void reset(Slot slot)
    {
        this.move(slot, this.originalX, this.originalY);
    }

    /**
     * Move the provided slot to the given coordinates.
     *
     * @param slot A {@link Slot} instance.
     * @param x    The new x-coordinate.
     * @param y    The new y-coordinate.
     */
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

    /**
     * Shortcut method that will move a slot if the old inventory tweak is enabled. This will also reset the slot to its
     * original position if the old inventory tweak is disabled.
     *
     * @param slot A {@link Slot} instance.
     * @param x    The new x-coordinate.
     * @param y    The new y-coordinate.
     */
    public void moveOrReset(Slot slot, int x, int y)
    {
        if (CandyTweak.OLD_INVENTORY.get())
            move(slot, x, y);
        else
            reset(slot);
    }
}
