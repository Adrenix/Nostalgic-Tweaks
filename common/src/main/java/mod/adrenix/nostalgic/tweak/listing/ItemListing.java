package mod.adrenix.nostalgic.tweak.listing;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Optional;
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
        String resourceKey = ItemUtil.getResourceKey(block);
        return Set.of(resourceKey, resourceKey + WILDCARD);
    }

    /* Fields */

    protected final transient HashSet<ItemRule> rules = new HashSet<>();
    protected final transient Object2BooleanMap<Item> itemCache = Object2BooleanMaps.synchronize(new Object2BooleanOpenHashMap<>());
    protected final transient Object2BooleanMap<Block> blockCache = Object2BooleanMaps.synchronize(new Object2BooleanOpenHashMap<>());
    protected boolean disabled = false;

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisabled(boolean state)
    {
        this.disabled = state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisabled()
    {
        return this.disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetTransientCache()
    {
        this.itemCache.clear();
        this.blockCache.clear();

        Listing.super.resetTransientCache();
    }

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
     * Get the parent wildcard block, if possible, from the given block.
     *
     * @param block The child {@link Block} to check.
     * @return An {@link Optional} with the wildcard parent {@link Block}.
     */
    public Optional<Block> getParentBlockFromWildcard(Block block)
    {
        for (String key : this.getResourceKeys())
        {
            if (!this.isWildcard(key))
                continue;

            Block wildcard = ItemUtil.getBlock(key.replace(WILDCARD, ""));

            if (ClassUtil.isInstanceOf(block, wildcard.getClass()))
                return Optional.of(wildcard);
        }

        return Optional.empty();
    }

    /**
     * Get the parent wildcard item, if possible, from the given item.
     *
     * @param item The child {@link Item} to check.
     * @return An {@link Optional} with the wildcard parent {@link Item}.
     */
    public Optional<Item> getParentItemFromWildcard(Item item)
    {
        for (String key : this.getResourceKeys())
        {
            if (!this.isWildcard(key))
                continue;

            Item wildcard = ItemUtil.getItem(key.replace(WILDCARD, ""));

            if (ClassUtil.isInstanceOf(item, wildcard.getClass()))
                return Optional.of(wildcard);
        }

        return Optional.empty();
    }

    /**
     * Check if the given block matches a wildcard.
     *
     * @param block A {@link Block} instance to check.
     * @return Whether the given block matches a wildcard.
     */
    public boolean isBlockWildcard(Block block)
    {
        return this.getParentBlockFromWildcard(block).isPresent();
    }

    /**
     * Check if the given item matches a wildcard.
     *
     * @param item An {@link Item} instance to check.
     * @return Whether the given item matches a wildcard.
     */
    public boolean isItemWildcard(Item item)
    {
        return this.getParentItemFromWildcard(item).isPresent();
    }

    /**
     * Check if the given block's resource key is within the listing or is a wildcard within the listing.
     *
     * @param block A {@link Block} instance to check.
     * @return Whether this list contains the given block.
     */
    public boolean containsBlock(Block block)
    {
        return this.blockCache.computeIfAbsent(block, this::lookupBlock);
    }

    /**
     * Checks if the listing contains the block's explicit resource key or if the block matches a wildcard.
     *
     * @param block A {@link Block} instance to check.
     * @return Whether this listing accepts the given block.
     */
    private boolean lookupBlock(Block block)
    {
        if (this.containsKey(ItemUtil.getResourceKey(block)))
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
        return this.itemCache.computeIfAbsent(item, this::lookupItem);
    }

    /**
     * Check if the given item stack's resource key is within the listing or is a wildcard within the listing.
     *
     * @param itemStack An {@link ItemStack} instance to check.
     * @return Whether this list contains the given item stack.
     */
    public boolean containsItem(ItemStack itemStack)
    {
        return this.containsItem(itemStack.getItem());
    }

    /**
     * Checks if the listing contains the item's explicit resource key or if the item matches a wildcard.
     *
     * @param item An {@link Item} instance to check.
     * @return Whether this listing accepts the given item.
     */
    private boolean lookupItem(Item item)
    {
        if (this.containsKey(ItemUtil.getResourceKey(item)))
            return true;

        return this.isItemWildcard(item);
    }
}
