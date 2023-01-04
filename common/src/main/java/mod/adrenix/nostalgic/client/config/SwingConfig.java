package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.tweak.SwingTweak;
import mod.adrenix.nostalgic.util.client.AnimationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;

import java.util.Map;

/**
 * Pulls Minecraft client code out of the {@link ModConfig} class. This utility allows the client to interface with the
 * mod's swing speed configurations.
 *
 * This class is used exclusively by the client. There is no reason for the server to interface with this.
 */

public abstract class SwingConfig
{
    /* Static Fields */

    private static final ClientConfig.Swing SWING = ClientConfigCache.getSwing();

    /* Static Utility */

    /**
     * Get the swing speed from an item instance.
     * @param item An item instance.
     * @return A swing speed associated with the given item instance. This could be custom, categorical, or generic.
     */
    private static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> ConfigList.LEFT_CLICK_SPEEDS.getEntryFromItem(item);
            case RIGHT_CLICK -> ConfigList.RIGHT_CLICK_SPEEDS.getEntryFromItem(item);
        };

        if (isSpeedGlobal())
        {
            return switch (AnimationUtil.swingType)
            {
                case LEFT_CLICK -> SWING.leftGlobalSpeed;
                case RIGHT_CLICK -> SWING.rightGlobalSpeed;
            };
        }
        else if (entry != null)
            return entry.getValue();
        else if (item instanceof SwordItem)
        {
            return switch (AnimationUtil.swingType)
            {
                case LEFT_CLICK -> SWING.leftSwordSpeed;
                case RIGHT_CLICK -> SWING.rightSwordSpeed;
            };
        }
        else if (item instanceof BlockItem)
        {
            return switch (AnimationUtil.swingType)
            {
                case LEFT_CLICK -> SWING.leftBlockSpeed;
                case RIGHT_CLICK -> SWING.rightBlockSpeed;
            };
        }
        else if (item instanceof DiggerItem)
        {
            return switch (AnimationUtil.swingType)
            {
                case LEFT_CLICK -> SWING.leftToolSpeed;
                case RIGHT_CLICK -> SWING.rightToolSpeed;
            };
        }

        return switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftItemSpeed;
            case RIGHT_CLICK -> SWING.rightItemSpeed;
        };
    }

    /**
     * Get the swing speed of a player instance's held main hand item.
     * @param player A player instance.
     * @return A swing speed value based on what the player is hold in their main hand.
     */
    public static int getSwingSpeed(AbstractClientPlayer player)
    {
        if (ModConfig.isModEnabled())
            return getSpeedFromItem(player.getMainHandItem().getItem());

        return DefaultConfig.Swing.NEW_SPEED;
    }

    /* Override Flags */

    /**
     * Checks if the animation utility should switch swing type states when right-clicking and interacting with a block.
     * @return Whether the swing right block interact tweak is active.
     */
    public static boolean isLeftSpeedOnBlockInteract() { return SWING.leftClickSpeedOnBlockInteract; }

    /**
     * An override flag for overriding all swing speed options.
     * @return Whether swing speeds should override.
     */
    public static boolean isOverridingSpeeds()
    {
        return !ModConfig.isTweakOn(SwingTweak.OVERRIDE_SPEEDS) || SWING.overrideSpeeds;
    }

    /**
     * An override flag for the mining fatigue potion.
     * @return Whether the swing speed should change when mining fatigue is applied.
     */
    public static boolean isOverridingFatigue()
    {
        return ModConfig.isModEnabled() && switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftFatigueSpeed != DefaultConfig.Swing.GLOBAL;
            case RIGHT_CLICK -> SWING.rightFatigueSpeed != DefaultConfig.Swing.GLOBAL;
        };
    }

    /**
     * An override flag for the haste potion.
     * @return Whether the swing speed should change when haste is applied.
     */
    public static boolean isOverridingHaste()
    {
        return ModConfig.isModEnabled() && switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftHasteSpeed != DefaultConfig.Swing.GLOBAL;
            case RIGHT_CLICK -> SWING.rightHasteSpeed != DefaultConfig.Swing.GLOBAL;
        };
    }

    /**
     * An override flag that checks if a global swing speed is in effect.
     * @return Whether a global swing speed should be used
     */
    public static boolean isSpeedGlobal()
    {
        return switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftGlobalSpeed != DefaultConfig.Swing.GLOBAL;
            case RIGHT_CLICK -> SWING.rightGlobalSpeed != DefaultConfig.Swing.GLOBAL;
        };
    }

    /* Override Values */

    /**
     * @return The global swing speed value.
     */
    public static int getGlobalSpeed()
    {
        return switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftGlobalSpeed;
            case RIGHT_CLICK -> SWING.rightGlobalSpeed;
        };
    }

    /**
     * This only take effect if there is no global swing speed.
     * @return Get the swing speed for the fatigue potion.
     */
    public static int getFatigueSpeed()
    {
        int global = switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftGlobalSpeed;
            case RIGHT_CLICK -> SWING.rightGlobalSpeed;
        };

        int fatigue = switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftFatigueSpeed;
            case RIGHT_CLICK -> SWING.rightFatigueSpeed;
        };

        return isSpeedGlobal() ? global : fatigue;
    }

    /**
     * This only take effect if there is no global swing speed.
     * @return Get the swing speed for the haste potion.
     */
    public static int getHasteSpeed()
    {
        int global = switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftGlobalSpeed;
            case RIGHT_CLICK -> SWING.rightGlobalSpeed;
        };

        int haste = switch (AnimationUtil.swingType)
        {
            case LEFT_CLICK -> SWING.leftHasteSpeed;
            case RIGHT_CLICK -> SWING.rightHasteSpeed;
        };

        return isSpeedGlobal() ? global : haste;
    }

    /**
     * A simple getter that returns a swing speed based on the game's player instance.
     * @return A swing speed value.
     */
    public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
}
