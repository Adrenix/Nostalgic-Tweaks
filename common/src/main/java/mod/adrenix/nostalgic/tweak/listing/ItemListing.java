package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public abstract class ItemListing<V, L extends Listing<V, L>> implements Listing<V, L>
{
    /* Static */

    public static final String WILDCARD = "*";

    /**
     * Get a set of resource keys to add to a listing for a wildcard entry.
     *
     * @param block A {@link Block} to get resource key data from.
     * @return A {@link Set} containing the block resource key and a wildcard key.
     */
    public static Set<String> getWildcardKeys(Block block)
    {
        String resourceKey = ItemCommonUtil.getResourceKey(block);
        return Set.of(resourceKey, resourceKey + WILDCARD);
    }

    /* Fields */

    protected final transient HashSet<ItemRule> rules = new HashSet<>();

    /* Methods */

    /**
     * If no rules were defined for the item listing, then the listing will allow all item types.
     *
     * @return A {@link HashSet} of {@link ItemRule} enumerations.
     */
    public HashSet<ItemRule> rules()
    {
        if (this.rules.isEmpty())
            this.rules.add(ItemRule.NONE);

        return this.rules;
    }

    /**
     * @return Whether the listing does not have any item rules.
     */
    public boolean areRulesEmpty()
    {
        return this.rules().stream().anyMatch(rule -> rule.equals(ItemRule.NONE));
    }

    /**
     * @return Whether the listing has an item rule.
     */
    public boolean hasItemRule()
    {
        return !this.areRulesEmpty();
    }

    /**
     * @return A {@link Set} of resource keys.
     */
    public abstract Set<String> getResourceKeys();

    /**
     * Add a new wildcard resource key.
     *
     * @param resourceKey An item's resource key.
     * @see #containsWildcard(String)
     */
    public abstract void addWildcard(String resourceKey);

    /**
     * Remove a wildcard resource key.
     *
     * @param resourceKey An item's resource key.
     */
    public abstract void removeWildcard(String resourceKey);

    /**
     * Get a resource key with the wildcard character appended to it.
     *
     * @param resourceKey An item's resource key.
     * @return A wildcard item resource key.
     */
    protected String getWildcard(String resourceKey)
    {
        return resourceKey + WILDCARD;
    }

    /**
     * Check if the given resource key is a wildcard key.
     *
     * @param resourceKey An item's resource key.
     * @return Whether the key is a wildcard.
     */
    public boolean isWildcard(String resourceKey)
    {
        return resourceKey.endsWith(WILDCARD);
    }

    /**
     * Check if the given resource key is considered a wildcard within the {@link ItemListing}. A wildcard key is a key
     * whose item's base class is eligible for selection. For example, an Oak Fence resource key added as a wildcard
     * will allow any fence that extends the {@code FenceBlock} class to be eligible for the {@link ItemListing}.
     *
     * @param resourceKey An item's resource key to check if it is a wildcard.
     * @return Whether the given resource key is a wildcard.
     */
    public boolean containsWildcard(String resourceKey)
    {
        return this.getResourceKeys().contains(resourceKey + WILDCARD);
    }

    /**
     * Check if the given block matches a wildcard.
     *
     * @param block A {@link Block} instance to check.
     * @return Whether the given block matches a wildcard.
     */
    public boolean isBlockWildcard(Block block)
    {
        for (String key : this.getResourceKeys())
        {
            if (!this.isWildcard(key))
                continue;

            Block wildcard = ItemCommonUtil.getBlock(key.replace(WILDCARD, ""));

            if (ClassUtil.isInstanceOf(block, wildcard.getClass()))
                return true;
        }

        return false;
    }

    /**
     * Check if the given item matches a wildcard.
     *
     * @param item An {@link Item} instance to check.
     * @return Whether the given item matches a wildcard.
     */
    public boolean isItemWildcard(Item item)
    {
        for (String key : this.getResourceKeys())
        {
            if (!this.isWildcard(key))
                continue;

            Item wildcard = ItemCommonUtil.getItem(key.replace(WILDCARD, ""));

            if (ClassUtil.isInstanceOf(item, wildcard.getClass()))
                return true;
        }

        return false;
    }

    /**
     * Check if the given block's resource key is within the listing or is a wildcard within the listing.
     *
     * @param block A {@link Block} instance to check.
     * @return Whether this list contains the given block.
     */
    public boolean containsBlock(Block block)
    {
        if (this.containsKey(ItemCommonUtil.getResourceKey(block)))
            return true;

        return this.isBlockWildcard(block);
    }

    /**
     * Check if the given item's resource key is within the listing or is a wildcard within the listing.
     *
     * @param item An {@link Item} instance to check.
     * @return Whether this list contains the given item.
     */
    public boolean containsItem(Item item)
    {
        if (this.containsKey(ItemCommonUtil.getResourceKey(item)))
            return true;

        return this.isItemWildcard(item);
    }
}
