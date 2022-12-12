package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.tweak.SwingTweak;
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
        Map.Entry<String, Integer> entry = ConfigList.CUSTOM_SWING.getEntryFromItem(item);

        if (isSpeedGlobal())
            return SWING.global;
        else if (entry != null)
            return entry.getValue();
        else if (item instanceof SwordItem)
            return SWING.sword;
        else if (item instanceof BlockItem)
            return SWING.block;
        else if (item instanceof DiggerItem)
            return SWING.tool;
        return SWING.item;
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
     * An override flag for overriding all swing speed options.
     * @return Whether swing speeds should override.
     */
    public static boolean isOverridingSpeeds() { return !ModConfig.isTweakOn(SwingTweak.OVERRIDE_SPEEDS) || SWING.overrideSpeeds; }

    /**
     * An override flag for the mining fatigue potion.
     * @return Whether the swing speed should change when mining fatigue is applied.
     */
    public static boolean isOverridingFatigue() { return ModConfig.isModEnabled() && SWING.fatigue != DefaultConfig.Swing.GLOBAL; }

    /**
     * An override flag for the haste potion.
     * @return Whether the swing speed should change when haste is applied.
     */
    public static boolean isOverridingHaste() { return ModConfig.isModEnabled() && SWING.haste != DefaultConfig.Swing.GLOBAL; }

    /**
     * An override flag that checks if a global swing speed is in effect.
     * @return Whether a global swing speed should be used
     */
    public static boolean isSpeedGlobal() { return SWING.global != DefaultConfig.Swing.GLOBAL; }

    /* Override Values */

    /**
     * @return The global swing speed value.
     */
    public static int getGlobalSpeed() { return SWING.global; }

    /**
     * This only take effect if there is no global swing speed.
     * @return Get the swing speed for the fatigue potion.
     */
    public static int getFatigueSpeed() { return isSpeedGlobal() ? SWING.global : SWING.fatigue; }

    /**
     * This only take effect if there is no global swing speed.
     * @return Get the swing speed for the haste potion.
     */
    public static int getHasteSpeed() { return isSpeedGlobal() ? SWING.global : SWING.haste; }

    /**
     * A simple getter that returns a swing speed based on the game's player instance.
     * @return A swing speed value.
     */
    public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
}
