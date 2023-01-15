package mod.adrenix.nostalgic.client.config.gui.widget.group;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ItemButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowBuild;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Item Groups
 *
 * The following classes and methods are used to handle rendering of item buttons for list screens. The config row list
 * is still used by these screens since it enhances the user experience.
 *
 * New rows will automatically be generated based on the current screen width and the amount of items to render.
 */

public class ItemGroup
{
    /* Fields */

    private final NonNullList<ItemStack> items;
    private final ListScreen screen;
    private final ConfigRowList list;

    /* Constructor */

    /**
     * Create a new item group instance. No positioning information is required. Starting positions and placement of
     * widgets is handled by the config row list instance.
     *
     * @param screen A list screen instance to get items and a config row list instance.
     */
    public ItemGroup(ListScreen screen)
    {
        this.screen = screen;
        this.items = screen.getSelectableItems();
        this.list = screen.getConfigRowList();
    }

    /* Methods */

    /**
     * Generate the rows required to fully display the list of items for this item group.
     * @return An array list of config row list instances.
     */
    public ArrayList<ConfigRowList.Row> generate()
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
        ArrayList<AbstractWidget> buttons = new ArrayList<>();

        int startX = ConfigRowList.getStartX() - 4;
        int maxWidth = this.list.screen.width - ConfigRowList.getStartX() - 24;
        int currentWidth = 0;
        int itemsAdded = 0;

        for (ItemStack item : items)
        {
            currentWidth += ItemButton.WIDTH + 1;
            startX += ItemButton.WIDTH + 1;
            itemsAdded++;

            if (currentWidth < maxWidth)
                buttons.add(new ItemButton(this.screen, item, startX));
            else
            {
                rows.add(new ConfigRowBuild.ManualRow(List.copyOf(buttons)).generate());
                buttons.clear();

                startX = ConfigRowList.getStartX() - 4 + ItemButton.WIDTH + 1;
                currentWidth = ItemButton.WIDTH + 1;

                buttons.add(new ItemButton(this.screen, item, startX));
            }
        }

        rows.add(new ConfigRowBuild.ManualRow(List.copyOf(buttons)).generate());

        if (itemsAdded == 0)
        {
            Component translate = Component.translatable(LangUtil.Gui.LIST_NO_ITEMS);
            Component text = Component.literal(ChatFormatting.RED + translate.getString());

            rows.clear();
            rows.addAll(new TextGroup(text, TextAlign.CENTER).generate());
        }

        return rows;
    }
}
