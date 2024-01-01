package mod.adrenix.nostalgic.tweak.listing;

import java.util.HashSet;

public abstract class ItemListing<V, L extends Listing<V, L>> implements Listing<V, L>
{
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
}
