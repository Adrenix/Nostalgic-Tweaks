package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.widget.list.CustomizedRowList;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class CustomSwings
{
    private static final ClientConfig config = ClientConfigCache.getRoot();

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
            else if (item instanceof MiningToolItem || item instanceof SwordItem)
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

    public static String getItemKey(Item item) { return Registry.ITEM.getId(item).toString(); }

    public static void addItem(Item item) { config.custom.put(getItemKey(item), DefaultConfig.Swing.OLD_SPEED); }

    public static Item getItem(Map.Entry<String, Integer> entry)
    {
        return Registry.ITEM.get(Identifier.tryParse(entry.getKey()));
    }

    public static String getLocalizedItem(Map.Entry<String, Integer> entry)
    {
        String localized = getItem(entry).getDefaultStack().getName().getString();
        Item item = getItem(entry);

        if (getItemKey(item).equals("minecraft:air"))
            if (isValidEntry(item, entry))
                return Text.translatable(NostalgicLang.Gui.CUSTOMIZE_HAND).getString();
            else
                return Text.translatable(NostalgicLang.Gui.CUSTOMIZE_UNKNOWN).getString();

        return localized;
    }

    public static Map.Entry<String, Integer> getEntryFromItem(Item item)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(config.custom.entrySet());
        for (Map.Entry<String, Integer> entry : entries)
            if (entry.getKey().equals(Registry.ITEM.getId(item).toString()))
                return entry;
        return null;
    }

    /* Config Tooltips */

    public static List<Text> rangeTooltip()
    {
        List<Text> tooltip = new ArrayList<>();

        Text alpha = Text.translatable(NostalgicLang.Gui.CUSTOMIZE_RANGE_TOOLTIP_0);
        Text modern = Text.translatable(NostalgicLang.Gui.CUSTOMIZE_RANGE_TOOLTIP_1);
        Text photo = Text.translatable(NostalgicLang.Gui.CUSTOMIZE_RANGE_TOOLTIP_2);

        String top = Formatting.GREEN + alpha.getString() + Formatting.WHITE + ": " + Formatting.AQUA + DefaultConfig.Swing.OLD_SPEED;
        String middle = Formatting.GOLD + modern.getString() + Formatting.WHITE + ": " + Formatting.AQUA + DefaultConfig.Swing.NEW_SPEED;
        String bottom = Formatting.YELLOW + photo.getString() + Formatting.WHITE + ": " + Formatting.AQUA + DefaultConfig.Swing.PHOTOSENSITIVE;

        tooltip.add(Text.literal(top));
        tooltip.add(Text.literal(middle));
        tooltip.add(Text.literal(bottom));

        return tooltip;
    }

    public static Text removeTooltip(Map.Entry<String, Integer> entry)
    {
        Text item = Text.literal(getLocalizedItem(entry)).withStyle(Formatting.WHITE);
        return Text.translatable(NostalgicLang.Gui.CUSTOMIZE_REMOVE_TOOLTIP, item).withStyle(Formatting.RED);
    }

    public static Text undoTooltip(Map.Entry<String, Integer> entry)
    {
        Text item = Text.literal(getLocalizedItem(entry)).withStyle(Formatting.WHITE);
        return Text.translatable(NostalgicLang.Gui.CUSTOMIZE_UNDO_TOOLTIP, item).withStyle(Formatting.GREEN);
    }
}
