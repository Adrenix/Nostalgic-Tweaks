package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

public class ItemMap<V> extends ItemListing<V, ItemMap<V>> implements DeletableMap<V, ItemMap<V>>
{
    /* Fields */

    private final LinkedHashMap<String, V> items = new LinkedHashMap<>()
    {
        @Override
        public V remove(Object key)
        {
            super.remove(key.toString() + "*");
            return super.remove(key);
        }

        @Override
        public boolean remove(Object key, Object value)
        {
            super.remove(key.toString() + "*", value);
            return super.remove(key, value);
        }
    };

    private final transient LinkedHashMap<String, V> deleted = new LinkedHashMap<>();
    private final transient V defaultValue;

    /* Constructors */

    /**
     * @param defaultValue A default value that is used when a new entry is added.
     */
    public ItemMap(V defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Gson requires that classes it deserializes to contain a no-args constructor, which is why the following "unused"
     * constructor exists.
     */
    @SuppressWarnings("unused")
    private ItemMap()
    {
        this.defaultValue = null;
    }

    /* Building */

    /**
     * @param map Provide a starting {@link LinkedHashMap} for the values.
     */
    public ItemMap<V> startWith(LinkedHashMap<String, V> map)
    {
        this.items.putAll(map);
        return this;
    }

    /**
     * @param rules A varargs list of {@link ItemRule} enumerations this map will use.
     */
    public ItemMap<V> rules(ItemRule... rules)
    {
        this.rules.addAll(Arrays.asList(rules));
        return this;
    }

    /* Methods */

    /**
     * Get a value using the given item.
     *
     * @param item The {@link Item} to check.
     * @return A value associated with the given item or the default value.
     */
    @PublicAPI
    public V valueFrom(Item item)
    {
        String childKey = ItemUtil.getResourceKey(item);

        if (this.items.containsKey(childKey))
            return this.items.get(childKey);

        Optional<Item> parent = this.getParentItemFromWildcard(item);

        if (parent.isEmpty())
            return this.defaultValue;

        return this.items.getOrDefault(ItemUtil.getResourceKey(parent.get()), this.defaultValue);
    }

    /**
     * Get a value using the given item stack.
     *
     * @param itemStack The {@link ItemStack} to check.
     * @return A value associated with the given item stack or the default value.
     */
    @PublicAPI
    public V valueFrom(ItemStack itemStack)
    {
        return this.valueFrom(itemStack.getItem());
    }

    /**
     * Get a value using the given block.
     *
     * @param block The {@link Block} to check.
     * @return A value associated with the given block or the default value.
     */
    @PublicAPI
    public V valueFrom(Block block)
    {
        return this.valueFrom(block.asItem());
    }

    /**
     * @return The {@link LinkedHashMap} associated within this {@link ItemMap}.
     */
    @Override
    public LinkedHashMap<String, V> getMap()
    {
        return this.items;
    }

    /**
     * @return The {@link LinkedHashMap} of deleted entries within this {@link ItemMap}.
     */
    @Override
    public LinkedHashMap<String, V> getDeleted()
    {
        return this.deleted;
    }

    @Override
    public Set<String> getResourceKeys()
    {
        return this.items.keySet();
    }

    @Override
    public V getDefaultValue()
    {
        return this.defaultValue;
    }

    @Override
    public ItemMap<V> create()
    {
        return new ItemMap<>(this.defaultValue).startWith(this.items).rules(this.rules.toArray(new ItemRule[0]));
    }

    @Override
    public void addWildcard(String resourceKey)
    {
        this.items.put(this.getWildcard(resourceKey), this.items.getOrDefault(resourceKey, this.defaultValue));
    }

    @Override
    public void removeWildcard(String resourceKey)
    {
        this.items.remove(this.getWildcard(resourceKey));
    }

    @Override
    public void copy(ItemMap<V> list)
    {
        this.putAll(list.items);
        this.putAll(list.deleted);

        this.disabled = list.disabled;
    }

    @Override
    public void clear()
    {
        this.items.clear();
        this.deleted.clear();
    }

    @Override
    public boolean matches(ItemMap<V> listing)
    {
        return this.items.equals(listing.items) && this.disabled == listing.disabled;
    }

    @Override
    public boolean containsKey(Object object)
    {
        return this.items.containsKey((String) object);
    }

    @Override
    public boolean validate(TweakValidator validator, TweakListing<V, ItemMap<V>> tweak)
    {
        return ListingValidator.map(this, this.items, validator, tweak, (entry) -> {
            tweak.fromDisk().applySafely(entry.getKey(), this.defaultValue, this::put);
            tweak.fromCache().applySafely(entry.getKey(), this.defaultValue, this::put);
        });
    }

    /**
     * Perform a cast onto the given tweak. This type of casting will be needed in situations where tweaks are in
     * wildcard form and instanceof checking is not sufficient in generating runtime types.
     *
     * @param tweak A wildcard {@link Tweak} instance.
     * @return A {@link Optional} {@link TweakListing} instance.
     */
    @SuppressWarnings("unchecked") // Tweak is class type checked
    public static Optional<TweakListing<Object, ItemMap<Object>>> cast(Tweak<?> tweak)
    {
        if (tweak instanceof TweakListing && tweak.fromDisk() instanceof ItemMap)
            return Optional.of((TweakListing<Object, ItemMap<Object>>) tweak);

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<V> genericType()
    {
        return (Class<V>) this.defaultValue.getClass();
    }

    @Override
    public String debugString()
    {
        String type = this.genericType().getSimpleName();
        int mapSize = this.items.size();
        boolean disabled = this.disabled;

        return String.format("ItemMap<%s>{mapSize:%s, disabled:%s}", type, mapSize, disabled);
    }

    @Override
    public String toString()
    {
        return this.debugString();
    }
}
