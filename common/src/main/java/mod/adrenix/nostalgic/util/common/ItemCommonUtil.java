package mod.adrenix.nostalgic.util.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.*;

/**
 * This utility is used by both client and server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.ItemClientUtil}.
 * For a server only utility use {@link mod.adrenix.nostalgic.util.server.ItemServerUtil}.
 */

public abstract class ItemCommonUtil
{
    /**
     * Get an item instance based on the provided resource location key.
     * @param resourceKey An item's resource location key.
     * @return An item instance from the registry if that item exists.
     */
    public static Item getItem(String resourceKey)
    {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(resourceKey));
    }

    /**
     * Get an item stack instance based on the provided resource location key.
     * @param resourceKey An item's resource location key.
     * @return An item stack instance from the registry if that item exists.
     */
    public static ItemStack getItemStack(String resourceKey) { return getItem(resourceKey).getDefaultInstance(); }

    /**
     * Get a block based on the provided resource location key.
     * @param resourceKey The block's resource location key.
     * @return A block from the registry if it exists.
     */
    public static Block getBlock(String resourceKey)
    {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(resourceKey));
    }

    /**
     * Get a block based on the provided item.
     * @param item An item instance to get block data from.
     * @return A block from the registry if it exists.
     */
    @SuppressWarnings("unused")
    public static Block getBlockFromItem(Item item) { return getBlock(getResourceKey(item)); }

    /**
     * Generates a unique key that will be associated with an item instance. This key is the toString method associated
     * with the item registry.
     *
     * @param item The item instance to get registry key information from.
     * @return A unique item key that can be stored in a configuration file.
     */
    public static String getResourceKey(Item item) { return BuiltInRegistries.ITEM.getKey(item).toString(); }

    /**
     * Generate a set of resource item keys from the given var args list of item instances.
     * @param items The var args item list to get keys from.
     * @return A hash set of item resource keys.
     */
    public static Set<String> getKeysFromItems(Item ...items)
    {
        Set<String> keys = new LinkedHashSet<>();

        for (Item item : items)
            keys.add(getResourceKey(item));

        return keys;
    }

    /**
     * Checks if a resource location key exists within the registry.
     * @param resourceKey An item's resource location key.
     * @return Whether the given resource location key exists.
     */
    public static boolean isValidKey(String resourceKey)
    {
        return getResourceKey(getItem(resourceKey)).equals(resourceKey);
    }

    /**
     * Get a localized item name.
     * @param resourceKey An item's resource location key.
     * @return A localized item name (if it exists) that is associated with the given key.
     */
    public static String getLocalizedItem(String resourceKey)
    {
        String localized = getItem(resourceKey).getDefaultInstance().getHoverName().getString();
        Item item = getItem(resourceKey);

        if (getResourceKey(item).equals("minecraft:air"))
        {
            if (isValidKey(resourceKey))
                return Component.translatable(LangUtil.Gui.SWING_HAND).getString();
            else
                return Component.translatable(LangUtil.Gui.SWING_UNKNOWN).getString();
        }

        return localized;
    }
}
