package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.util.LinkedHashSet;

public class StringSet implements DeletableSet<String, StringSet>
{
    /* Fields */

    private final LinkedHashSet<String> list = new LinkedHashSet<>();
    private final transient LinkedHashSet<String> deleted = new LinkedHashSet<>();
    private final transient ListingSuggestion suggestion;
    private boolean disabled;

    /* Constructors */

    /**
     * Gson requires that classes it deserializes to contain a no-args constructor.
     */
    public StringSet()
    {
        this.suggestion = ListingSuggestion.NONE;
    }

    /**
     * Create a new {@link StringSet} instance.
     *
     * @param suggestion The {@link ListingSuggestion} for this set.
     */
    public StringSet(ListingSuggestion suggestion)
    {
        this.suggestion = suggestion;
    }

    /* Building */

    @PublicAPI
    public StringSet startWith(LinkedHashSet<String> set)
    {
        this.list.addAll(set);
        return this;
    }

    /* Methods */

    /**
     * @return The {@link ListingSuggestion} associated with this {@link StringSet}.
     */
    public ListingSuggestion getSuggestion()
    {
        return this.suggestion;
    }

    /**
     * @return The {@link LinkedHashSet} associated with this {@link StringSet}.
     */
    @Override
    public LinkedHashSet<String> getSet()
    {
        return this.list;
    }

    /**
     * @return The {@link LinkedHashSet} of deleted elements within this {@link StringSet}.
     */
    @Override
    public LinkedHashSet<String> getDeleted()
    {
        return this.deleted;
    }

    @Override
    public StringSet create()
    {
        return new StringSet(this.suggestion);
    }

    @Override
    public void copy(StringSet list)
    {
        this.addAll(list.list);
        this.disabled = list.disabled;
    }

    @Override
    public void clear()
    {
        this.list.clear();
        this.deleted.clear();
    }

    @Override
    public boolean matches(StringSet listing)
    {
        return this.list.equals(listing.list) && this.disabled == listing.disabled;
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsKey(Object object)
    {
        return this.list.contains(object);
    }

    @Override
    public boolean validate(TweakValidator validator, TweakListing<String, StringSet> tweak)
    {
        return ListingValidator.set(this, this.list, validator, tweak);
    }

    @Override
    public void setDisabled(boolean state)
    {
        this.disabled = state;
    }

    @Override
    public boolean isDisabled()
    {
        return this.disabled;
    }

    @Override
    public Class<String> genericType()
    {
        return String.class;
    }

    @Override
    public String debugString()
    {
        return String.format("StringSet{size:%s, disabled:%s}", this.list.size(), this.disabled);
    }

    @Override
    public String toString()
    {
        return this.debugString();
    }
}
