package mod.adrenix.nostalgic.mixin.util.swing;

import mod.adrenix.nostalgic.mixin.util.animation.PlayerArmMixinHelper;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.config.SwingTweak;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

/**
 * This utility class is used only by the client.
 */
public abstract class SwingMixinHelper
{
    /**
     * @return Whether a global swing speed is in effect.
     */
    public static boolean isSpeedGlobal()
    {
        return switch (PlayerArmMixinHelper.SWING_TYPE.get())
        {
            case ATTACK -> SwingTweak.ATTACK_GLOBAL_SPEED.get() != SwingTweak.DISABLED;
            case USE -> SwingTweak.USE_GLOBAL_SPEED.get() != SwingTweak.DISABLED;
        };
    }

    /**
     * @return The global swing speed value.
     */
    public static int getGlobalSpeed()
    {
        return switch (PlayerArmMixinHelper.SWING_TYPE.get())
        {
            case ATTACK -> SwingTweak.ATTACK_GLOBAL_SPEED.get();
            case USE -> SwingTweak.USE_GLOBAL_SPEED.get();
        };
    }

    /**
     * Get the swing speed from an item.
     *
     * @param item The {@link Item} to get a speed from.
     * @return A swing speed associated with the given item.
     */
    public static int getSpeedFromItem(Item item)
    {
        SwingType swingType = PlayerArmMixinHelper.SWING_TYPE.get();

        boolean containsItem = switch (swingType)
        {
            case ATTACK -> SwingTweak.ATTACK_SWING_SPEEDS.get().containsItem(item);
            case USE -> SwingTweak.USE_SWING_SPEEDS.get().containsItem(item);
        };

        if (isSpeedGlobal())
        {
            return switch (swingType)
            {
                case ATTACK -> SwingTweak.ATTACK_GLOBAL_SPEED.get();
                case USE -> SwingTweak.USE_GLOBAL_SPEED.get();
            };
        }
        else if (containsItem)
        {
            return switch (swingType)
            {
                case ATTACK -> SwingTweak.ATTACK_SWING_SPEEDS.get().valueFrom(item);
                case USE -> SwingTweak.USE_SWING_SPEEDS.get().valueFrom(item);
            };
        }
        else if (item instanceof SwordItem)
        {
            return switch (swingType)
            {
                case ATTACK -> SwingTweak.ATTACK_SWORD_SPEED.get();
                case USE -> SwingTweak.USE_SWORD_SPEED.get();
            };
        }
        else if (item instanceof BlockItem)
        {
            return switch (swingType)
            {
                case ATTACK -> SwingTweak.ATTACK_BLOCK_SPEED.get();
                case USE -> SwingTweak.USE_BLOCK_SPEED.get();
            };
        }
        else if (item instanceof DiggerItem)
        {
            return switch (swingType)
            {
                case ATTACK -> SwingTweak.ATTACK_TOOL_SPEED.get();
                case USE -> SwingTweak.USE_TOOL_SPEED.get();
            };
        }

        return switch (swingType)
        {
            case ATTACK -> SwingTweak.ATTACK_ITEM_SPEED.get();
            case USE -> SwingTweak.USE_ITEM_SPEED.get();
        };
    }

    /**
     * Get the swing speed of a player's held main hand item.
     *
     * @param player The {@link AbstractClientPlayer} to get context from.
     * @return A swing speed value based on what the player is holding in their main hand.
     */
    public static int getSwingSpeed(AbstractClientPlayer player)
    {
        if (ModTweak.ENABLED.get())
            return getSpeedFromItem(player.getMainHandItem().getItem());

        return SwingTweak.NEW_SPEED;
    }

    /**
     * @return Whether the swing speed should change when the mining fatigue effect is applied.
     */
    public static boolean isFatigueOverride()
    {
        return ModTweak.ENABLED.get() && switch (PlayerArmMixinHelper.SWING_TYPE.get())
        {
            case ATTACK -> SwingTweak.ATTACK_FATIGUE_SPEED.get() != SwingTweak.DISABLED;
            case USE -> SwingTweak.USE_FATIGUE_SPEED.get() != SwingTweak.DISABLED;
        };
    }

    /**
     * @return Get the swing speed for the fatigue effect.
     */
    public static int getFatigueSpeed()
    {
        return isSpeedGlobal() ? getGlobalSpeed() : switch (PlayerArmMixinHelper.SWING_TYPE.get())
        {
            case ATTACK -> SwingTweak.ATTACK_FATIGUE_SPEED.get();
            case USE -> SwingTweak.USE_FATIGUE_SPEED.get();
        };
    }

    /**
     * @return Whether the swing speed should change when the haste effect is applied.
     */
    public static boolean isHasteOverride()
    {
        return ModTweak.ENABLED.get() && switch (PlayerArmMixinHelper.SWING_TYPE.get())
        {
            case ATTACK -> SwingTweak.ATTACK_HASTE_SPEED.get() != SwingTweak.DISABLED;
            case USE -> SwingTweak.USE_HASTE_SPEED.get() != SwingTweak.DISABLED;
        };
    }

    /**
     * @return Get the swing speed for the haste effect.
     */
    public static int getHasteSpeed()
    {
        return isSpeedGlobal() ? getGlobalSpeed() : switch (PlayerArmMixinHelper.SWING_TYPE.get())
        {
            case ATTACK -> SwingTweak.ATTACK_HASTE_SPEED.get();
            case USE -> SwingTweak.USE_HASTE_SPEED.get();
        };
    }
}
