package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MobMap<V> extends MobListing<V, MobMap<V>> implements DeletableMap<V, MobMap<V>>
{
    /* Fields */

    protected final LinkedHashMap<String, V> mobs = new LinkedHashMap<>()
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

    protected final transient LinkedHashMap<String, V> deleted = new LinkedHashMap<>();
    protected final transient V defaultValue;

    /* Constructors */

    /**
     * Gson requires that classes it deserializes to contain a no-args constructor, which is why the following "unused"
     * constructor exists.
     */
    @SuppressWarnings("unused")
    public MobMap()
    {
        this.defaultValue = null;
    }

    /**
     * Create a new {@link MobMap} instance.
     *
     * @param defaultValue The default map entry value.
     */
    public MobMap(V defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /* Building */

    /**
     * @param map Provide a starting {@link LinkedHashMap} for the entries.
     * @return The caller instance.
     */
    public MobMap<V> startWith(LinkedHashMap<String, V> map)
    {
        this.mobs.putAll(map);
        return this;
    }

    /* Methods */

    /**
     * Get a value using the given mob.
     *
     * @param mob        The {@link Mob} to check.
     * @param emptyValue The value to use if the mob wasn't found in the map.
     * @return A value associated with the given mob or the given default value.
     */
    @PublicAPI
    public V valueFrom(Mob mob, V emptyValue)
    {
        String childKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();

        if (this.mobs.containsKey(childKey))
            return this.mobs.get(childKey);

        Optional<EntityType<?>> parent = this.getMobTypeFromWildcard(mob);

        if (parent.isEmpty())
            return emptyValue;

        return this.mobs.getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(parent.get()).toString(), emptyValue);
    }

    /**
     * Get a value using the given mob.
     *
     * @param mob The {@link Mob} to check.
     * @return A value associated with the given mob or the default map value.
     */
    @PublicAPI
    public V valueFrom(Mob mob)
    {
        return this.valueFrom(mob, this.defaultValue);
    }

    @Override
    public Set<String> getResourceKeys()
    {
        return this.mobs.keySet();
    }

    @Override
    public void addWildcard(String resourceKey)
    {
        this.mobs.put(this.getWildcard(resourceKey), this.mobs.getOrDefault(resourceKey, this.defaultValue));
    }

    @Override
    public void removeWildcard(String resourceKey)
    {
        this.mobs.remove(this.getWildcard(resourceKey));
    }

    @Override
    public Map<String, V> getDeleted()
    {
        return this.deleted;
    }

    @Override
    public Map<String, V> getMap()
    {
        return this.mobs;
    }

    @Override
    public V getDefaultValue()
    {
        return this.defaultValue;
    }

    @Override
    public MobMap<V> create()
    {
        return new MobMap<>(this.defaultValue);
    }

    @Override
    public void copy(MobMap<V> list)
    {
        this.putAll(list.mobs);
        this.putAll(list.deleted);

        this.disabled = list.disabled;
    }

    @Override
    public void clear()
    {
        this.mobs.clear();
        this.deleted.clear();
    }

    @Override
    public boolean matches(MobMap<V> listing)
    {
        return this.mobs.equals(listing.mobs) && this.disabled == listing.disabled;
    }

    @Override
    public boolean containsKey(Object object)
    {
        return this.mobs.containsKey((String) object);
    }

    @Override
    public boolean validate(TweakValidator validator, TweakListing<V, MobMap<V>> tweak)
    {
        return ListingValidator.map(
            this, this.mobs, validator, tweak, entry -> {
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
    @SuppressWarnings("unchecked")
    public static Optional<TweakListing<Object, MobMap<Object>>> cast(Tweak<?> tweak)
    {
        if (tweak instanceof TweakListing && tweak.fromDisk() instanceof MobMap)
            return Optional.of((TweakListing<Object, MobMap<Object>>) tweak);

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
        int mapSize = this.mobs.size();
        boolean disabled = this.disabled;

        return String.format("StringMap<%s>{mapSize:%s, disabled:%s}", type, mapSize, disabled);
    }
}
