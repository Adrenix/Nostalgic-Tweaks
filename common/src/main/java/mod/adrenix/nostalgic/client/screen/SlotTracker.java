package mod.adrenix.nostalgic.client.screen;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.inventory.Slot;

/**
 * Tracks specific slots that reside in the player's inventory menu.
 * Slots have had their x, y positions mutable via access widening.
 */

public enum SlotTracker
{
    /* Enumerations */

    CRAFT_TOP_LEFT(98, 18),
    CRAFT_TOP_RIGHT(116, 18),
    CRAFT_BOTTOM_LEFT(98, 36),
    CRAFT_BOTTOM_RIGHT(116, 36),
    CRAFT_RESULT(154, 28),
    OFF_HAND(77, 62);

    /* Fields */

    /*
       Movable Positions

       The (x, y) fields are dynamic and can change during runtime.
       These positions will match their vanilla counterparts after construction.
     */

    private int x;
    private int y;

    /*
       Static Positions

       The (vanillaX, vanillaY) fields are static and cannot change during runtime.
       These positions are used when a slot needs returned to its original position.
     */

    private final int vanillaX;
    private final int vanillaY;

    /* Constructor */

    /**
     * Create a new slot tracker enumeration value.
     * @param x The original x-position of the slot.
     * @param y The original y-position of the slot.
     */
    SlotTracker(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.vanillaX = x;
        this.vanillaY = y;
    }

    /* Methods */

    /**
     * Checks if this slot position is equal to the position of the provided slot.
     * @param slot A slot instance.
     * @return Whether this slot's position matches the given slot's position.
     */
    public boolean isEqualTo(Slot slot)
    {
        return (slot.x == this.x && slot.y == this.y) || (slot.x == this.vanillaX && slot.y == this.vanillaY);
    }

    /**
     * Returns a slot to the vanilla position of this slot.
     * @param slot A slot instance.
     */
    public void reset(Slot slot) { this.move(slot, this.vanillaX, this.vanillaY); }

    /**
     * Move the provided slot to a new (x, y) position.
     * @param slot A slot instance.
     * @param x A new x-position.
     * @param y A new y-position.
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
     * Shortcut method that will move a slot if the old inventory tweak is enabled and will reset the slot to its
     * original position of the old inventory tweak is disabled.
     *
     * @param slot A slot instance.
     * @param x A new x-position.
     * @param y A new y-position.
     */
    public void moveOrReset(Slot slot, int x, int y)
    {
        if (ModConfig.Candy.oldInventory())
            move(slot, x, y);
        else
            reset(slot);
    }
}
