package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.widget.list.SpeedRowList;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This utility class servers multiple purposes. The first being a validation utility that is used by the config reader
 * when it loads a configuration file.
 *
 * The second purpose is providing utility methods that can be used by config screen methods.
 */

public abstract class CustomSwings
{
    /* Private Static Fields */

    private static final ClientConfig CLIENT_CONFIG = ClientConfigCache.getRoot();

    /* Config Validation */

    /**
     * Checks the validity of the current map stored in the client config.
     *
     * If an entry is invalid (an entry is invalid if its value is out-of-bounds), then its value is reset and a notice
     * is sent to the current logger.
     *
     * If any changes are made to the client config cache then the changes are immediately saved to disk.
     */
    public static void validate()
    {
        boolean isCleaned = false;

        if (CLIENT_CONFIG.custom == null)
        {
            CLIENT_CONFIG.custom = Maps.newHashMap();
            isCleaned = true;
        }

        for (Map.Entry<String, Integer> entry : CLIENT_CONFIG.custom.entrySet())
        {
            if (entry.getValue() < ClientConfig.MIN || entry.getValue() > ClientConfig.MAX)
            {
                NostalgicTweaks.LOGGER.warn(entry.getKey() + " has invalid swing speed: " + entry.getValue());
                NostalgicTweaks.LOGGER.warn(entry.getKey() + " has been updated to speed: " + DefaultConfig.Swing.OLD_SPEED);
                entry.setValue(DefaultConfig.Swing.OLD_SPEED);

                isCleaned = true;
            }
        }

        if (isCleaned)
            AutoConfig.getConfigHolder(ClientConfig.class).save();
    }

    /* Utility Methods */

    /**
     * Sort items within a list of configuration entries.
     * @param addTools Whether tools should be added to the results.
     * @param addBlocks Whether blocks should be added to the results.
     * @param addItems Whether items should be added to the results.
     * @return A sorted list of configuration map entries.
     */
    public static List<Map.Entry<String, Integer>> getSortedItems(boolean addTools, boolean addBlocks, boolean addItems)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(CLIENT_CONFIG.custom.entrySet());
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>();
        List<Map.Entry<String, Integer>> tools = new ArrayList<>();
        List<Map.Entry<String, Integer>> blocks = new ArrayList<>();
        List<Map.Entry<String, Integer>> items = new ArrayList<>();
        List<Map.Entry<String, Integer>> unknown = new ArrayList<>();
        Map.Entry<String, Integer> added = null;

        for (Map.Entry<String, Integer> entry : entries)
        {
            Item item = getItem(entry);

            if (SpeedRowList.added != null && entry.getKey().equals(SpeedRowList.added.getKey()))
            {
                added = entry;
            }
            else if (!isValidEntry(item, entry))
            {
                unknown.add(entry);
            }
            else if (item instanceof DiggerItem || item instanceof SwordItem)
            {
                if (addTools)
                    tools.add(entry);
            }
            else if (item instanceof BlockItem)
            {
                if (addBlocks)
                    blocks.add(entry);
            }
            else
            {
                if (addItems)
                    items.add(entry);
            }
        }

        sorted.addAll(tools);
        sorted.addAll(blocks);
        sorted.addAll(items);
        sorted.addAll(unknown);
        sorted.sort(Comparator.comparing(CustomSwings::getLocalizedItem));

        if (added != null)
            sorted.add(0, added);

        return sorted;
    }

    /**
     * Checks if an item matches the key associated with the given configuration map entry.
     * @param item The item instance to check.
     * @param entry The configuration map entry to check against.
     * @return Whether the given item matches the configuration map entry key.
     */
    public static boolean isValidEntry(Item item, Map.Entry<String, Integer> entry)
    {
        return getItemKey(item).equals(entry.getKey());
    }

    /**
     * Generates a unique key that will be associated with an item instance. This key is the toString method associated
     * with the vanilla item registry.
     *
     * @param item The item instance to get registry key information from.
     * @return A unique item key that can be stored in a configuration file.
     */
    public static String getItemKey(Item item) { return Registry.ITEM.getKey(item).toString(); }

    /**
     * Adds a new item to the custom swing speed map in the configuration file with the default old swing speed.
     * @param item An item instance so that a unique map key can be generated.
     */
    public static void addItem(Item item) { CLIENT_CONFIG.custom.put(getItemKey(item), DefaultConfig.Swing.OLD_SPEED); }

    /**
     * Get an item instance based on the provided configuration map entry.
     * @param entry A configuration map entry to try and parse item key information from.
     * @return An item instance from the vanilla registry if that item exists.
     */
    public static Item getItem(Map.Entry<String, Integer> entry)
    {
        return Registry.ITEM.get(ResourceLocation.tryParse(entry.getKey()));
    }

    /**
     * Get a localized item name from a configuration map entry.
     * @param entry A configuration map entry to get item key information from.
     * @return A localized item name (if it exists) that is associated with a configuration map entry.
     */
    public static String getLocalizedItem(Map.Entry<String, Integer> entry)
    {
        String localized = getItem(entry).getDefaultInstance().getHoverName().getString();
        Item item = getItem(entry);

        if (getItemKey(item).equals("minecraft:air"))
        {
            if (isValidEntry(item, entry))
                return Component.translatable(LangUtil.Gui.SWING_HAND).getString();
            else
                return Component.translatable(LangUtil.Gui.SWING_UNKNOWN).getString();
        }

        return localized;
    }

    /**
     * Get a configuration map entry from an item instance.
     * @param item An item instance to get a configuration map entry from.
     * @return A configuration map entry (if it exists) that is associated with the given item instance.
     */
    @Nullable
    public static Map.Entry<String, Integer> getEntryFromItem(Item item)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(CLIENT_CONFIG.custom.entrySet());

        for (Map.Entry<String, Integer> entry : entries)
        {
            if (entry.getKey().equals(Registry.ITEM.getKey(item).toString()))
                return entry;
        }

        return null;
    }

    /* Tooltip Utility */

    /**
     * Get a list of components that displays information about custom swing speed ranges.
     * Any rendering of this tooltip must be handled separately.
     *
     * @return A list of components that should be used in a tooltip to display swing speed ranges.
     */
    public static List<Component> rangeTooltip()
    {
        List<Component> tooltip = new ArrayList<>();

        Component alpha = Component.translatable(LangUtil.Gui.SWING_RANGE_TOOLTIP_0);
        Component modern = Component.translatable(LangUtil.Gui.SWING_RANGE_TOOLTIP_1);
        Component photo = Component.translatable(LangUtil.Gui.SWING_RANGE_TOOLTIP_2);

        String top = ChatFormatting.GREEN + alpha.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + DefaultConfig.Swing.OLD_SPEED;
        String middle = ChatFormatting.GOLD + modern.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + DefaultConfig.Swing.NEW_SPEED;
        String bottom = ChatFormatting.YELLOW + photo.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + DefaultConfig.Swing.PHOTOSENSITIVE;

        tooltip.add(Component.literal(top));
        tooltip.add(Component.literal(middle));
        tooltip.add(Component.literal(bottom));

        return tooltip;
    }

    /**
     * Get a component that displays information about removing a swing speed row entry.
     * Any rendering of this tooltip must be handled separately.
     *
     * @param entry A configuration map entry.
     * @return A component that should be used in a tooltip.
     */
    public static Component removeTooltip(Map.Entry<String, Integer> entry)
    {
        Component item = Component.literal(getLocalizedItem(entry)).withStyle(ChatFormatting.WHITE);
        return Component.translatable(LangUtil.Gui.SWING_REMOVE_TOOLTIP, item).withStyle(ChatFormatting.RED);
    }

    /**
     * Get a component that displays information about undoing the deletion of a swing speed row entry.
     * Any rendering of this tooltip must be handled separately.
     *
     * @param entry A configuration map entry.
     * @return A component that should be used in a tooltip.
     */
    public static Component undoTooltip(Map.Entry<String, Integer> entry)
    {
        Component item = Component.literal(getLocalizedItem(entry)).withStyle(ChatFormatting.WHITE);
        return Component.translatable(LangUtil.Gui.SWING_UNDO_TOOLTIP, item).withStyle(ChatFormatting.GREEN);
    }
}
