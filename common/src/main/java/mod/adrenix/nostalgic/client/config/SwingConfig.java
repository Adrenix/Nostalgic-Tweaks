package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.SwingTweak;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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
    private static final Map<String, Integer> CUSTOM_LIST = ClientConfigCache.getRoot().customSwingSpeeds;

    /* Static Utility */

    /**
     * Get a configuration map entry from an item instance.
     * @param item An item instance to get a configuration map entry from.
     * @return A configuration map entry (if it exists) that is associated with the given item instance.
     */
    @Nullable
    public static Map.Entry<String, Integer> getEntryFromItem(Item item)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(CUSTOM_LIST.entrySet());

        for (Map.Entry<String, Integer> entry : entries)
        {
            if (entry.getKey().equals(ItemClientUtil.getResourceKey(item)))
                return entry;
        }

        return null;
    }

    /**
     * Get the swing speed from an item instance.
     * @param item An item instance.
     * @return A swing speed associated with the given item instance. This could be custom, categorical, or generic.
     */
    private static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = getEntryFromItem(item);

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
