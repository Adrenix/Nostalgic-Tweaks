package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.widget.CustomizedRowList;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class CustomSwings
{
    private static final ClientConfig config = CommonRegistry.getRoot();

    /* Check Validity of Customized Items */
    public static void validate()
    {
        boolean isCleaned = false;

        if (config.custom == null)
        {
            config.custom = Maps.newHashMap();
            isCleaned = true;
        }

        for (Map.Entry<String, Integer> entry : config.custom.entrySet())
        {
            if (entry.getValue() < ClientConfig.MIN || entry.getValue() > ClientConfig.MAX)
            {
                NostalgicTweaks.LOGGER.warn(entry.getKey() + " has invalid swing speed: " + entry.getValue());
                NostalgicTweaks.LOGGER.info(entry.getKey() + " has been updated to speed: " + DefaultConfig.Swing.OLD_SPEED);

                entry.setValue(DefaultConfig.Swing.OLD_SPEED);
                isCleaned = true;
            }
        }

        if (isCleaned)
            AutoConfig.getConfigHolder(ClientConfig.class).save();
    }

    /* Sort Customized Items */
    public static List<Map.Entry<String, Integer>> getSortedItems(boolean addTools, boolean addBlocks, boolean addItems)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(config.custom.entrySet());
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>();
        List<Map.Entry<String, Integer>> tools = new ArrayList<>();
        List<Map.Entry<String, Integer>> blocks = new ArrayList<>();
        List<Map.Entry<String, Integer>> items = new ArrayList<>();
        List<Map.Entry<String, Integer>> unknown = new ArrayList<>();
        Map.Entry<String, Integer> added = null;

        for (Map.Entry<String, Integer> entry : entries)
        {
            Item item = getItem(entry);

            if (CustomizedRowList.added != null && entry.getKey().equals(CustomizedRowList.added.getKey()))
                added = entry;
            else if (!isValidEntry(item, entry))
                unknown.add(entry);
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

    /* Item <-> Config (Helpers) */

    public static boolean isValidEntry(Item item, Map.Entry<String, Integer> entry)
    {
        return getItemKey(item).equals(entry.getKey());
    }

    public static String getItemKey(Item item) { return Registry.ITEM.getKey(item).toString(); }

    public static void addItem(Item item) { config.custom.put(getItemKey(item), DefaultConfig.Swing.OLD_SPEED); }

    public static Item getItem(Map.Entry<String, Integer> entry)
    {
        return Registry.ITEM.get(ResourceLocation.tryParse(entry.getKey()));
    }

    public static String getLocalizedItem(Map.Entry<String, Integer> entry)
    {
        String localized = getItem(entry).getDefaultInstance().getHoverName().getString();
        Item item = getItem(entry);

        if (getItemKey(item).equals("minecraft:air"))
            if (isValidEntry(item, entry))
                return Component.translatable(NostalgicLang.Gui.CUSTOMIZE_HAND).getString();
            else
                return Component.translatable(NostalgicLang.Gui.CUSTOMIZE_UNKNOWN).getString();

        return localized;
    }

    public static Map.Entry<String, Integer> getEntryFromItem(Item item)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(config.custom.entrySet());
        for (Map.Entry<String, Integer> entry : entries)
            if (entry.getKey().equals(Registry.ITEM.getKey(item).toString()))
                return entry;
        return null;
    }

    /* Config Tooltips */

    public static List<Component> rangeTooltip()
    {
        List<Component> tooltip = new ArrayList<>();

        Component alpha = Component.translatable(NostalgicLang.Gui.CUSTOMIZE_RANGE_TOOLTIP_0);
        Component modern = Component.translatable(NostalgicLang.Gui.CUSTOMIZE_RANGE_TOOLTIP_1);
        Component photo = Component.translatable(NostalgicLang.Gui.CUSTOMIZE_RANGE_TOOLTIP_2);

        String top = ChatFormatting.GREEN + alpha.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + DefaultConfig.Swing.OLD_SPEED;
        String middle = ChatFormatting.GOLD + modern.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + DefaultConfig.Swing.NEW_SPEED;
        String bottom = ChatFormatting.YELLOW + photo.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + DefaultConfig.Swing.PHOTOSENSITIVE;

        tooltip.add(Component.literal(top));
        tooltip.add(Component.literal(middle));
        tooltip.add(Component.literal(bottom));

        return tooltip;
    }

    public static Component removeTooltip(Map.Entry<String, Integer> entry)
    {
        Component item = Component.literal(getLocalizedItem(entry)).withStyle(ChatFormatting.WHITE);
        return Component.translatable(NostalgicLang.Gui.CUSTOMIZE_REMOVE_TOOLTIP, item).withStyle(ChatFormatting.RED);
    }

    public static Component undoTooltip(Map.Entry<String, Integer> entry)
    {
        Component item = Component.literal(getLocalizedItem(entry)).withStyle(ChatFormatting.WHITE);
        return Component.translatable(NostalgicLang.Gui.CUSTOMIZE_UNDO_TOOLTIP, item).withStyle(ChatFormatting.GREEN);
    }
}
