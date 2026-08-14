package mod.adrenix.nostalgic.tweak.listing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public abstract class MobListing<V, L extends Listing<V, L>> implements Listing<V, L>
{
    public static final String WILDCARD = "*";
    public static final HashMap<EntityType<?>, Class<? extends Entity>> TYPES_TO_CLASS = new HashMap<>();

    /**
     * Get a set of resource keys to add to a listing for a wildcard entry.
     *
     * @param entityType A {@link EntityType} that extends a {@link Mob} to get resource key data from.
     * @return A {@link Set} containing the entity resource key and a wildcard key.
     */
    public static Set<String> getWildcardKeys(EntityType<? extends Mob> entityType)
    {
        String resourceKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
        return Set.of(resourceKey, resourceKey + WILDCARD);
    }

    /**
     * Get the actual class associated with an entity type.
     *
     * @param entityType The {@link EntityType} to determine an entity class for.
     * @param level      The {@link Level} to make a temporary dummy entity with.
     * @return A {@link Class} that extends {@link Entity}.
     */
    public static Class<? extends Entity> getClassForType(EntityType<?> entityType, Level level)
    {
        return TYPES_TO_CLASS.computeIfAbsent(
            entityType, type -> {
                Entity dummy = type.create(level);

                if (dummy == null)
                    return null;

                dummy.discard();

                return dummy.getClass();
            });
    }

    /* Fields */

    protected boolean disabled = false;

    /* Methods */

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

    /**
     * @return A {@link Set} of resource keys.
     */
    public abstract Set<String> getResourceKeys();

    /**
     * Add a new wildcard resource key.
     *
     * @param resourceKey A mob's resource key.
     * @see #containsWildcard(String)
     */
    public abstract void addWildcard(String resourceKey);

    /**
     * Remove a wildcard resource key.
     *
     * @param resourceKey A mob's resource key.
     */
    public abstract void removeWildcard(String resourceKey);

    /**
     * Get a resource key with the wildcard character appended to it.
     *
     * @param resourceKey A mob's resource key.
     * @return A wildcard mob resource key.
     */
    protected String getWildcard(String resourceKey)
    {
        return resourceKey + WILDCARD;
    }

    /**
     * Check if the given resource key is a wildcard key.
     *
     * @param resourceKey A mob's resource key.
     * @return Whether the key is a wildcard.
     */
    public boolean isWildcard(String resourceKey)
    {
        return resourceKey.endsWith(WILDCARD);
    }

    /**
     * Check if the given resource key is considered a wildcard within the {@link MobListing}. A wildcard key is a key
     * whose mob's base class is eligible for selection. For example, a Zombie resource key added as a wildcard will
     * allow any zombie that extends the {@code Zombie} class to be eligible for the {@link MobListing}.
     *
     * @param resourceKey A mob's resource key to check if it is a wildcard.
     * @return Whether the given resource key is a wildcard.
     */
    public boolean containsWildcard(String resourceKey)
    {
        return this.getResourceKeys().contains(resourceKey + WILDCARD);
    }

    /**
     * Get the parent mob type, if possible, from the given mob type.
     *
     * @param mob The {@link Mob} to check.
     * @return An {@link Optional} with the wildcard mob parent {@link EntityType}.
     */
    public Optional<EntityType<?>> getMobTypeFromWildcard(Mob mob)
    {
        for (String key : this.getResourceKeys())
        {
            if (!this.isWildcard(key))
                continue;

            EntityType<?> wildcard = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(key.replace(WILDCARD, "")));

            if (getClassForType(wildcard, mob.level()).isInstance(mob))
                return Optional.of(wildcard);
        }

        return Optional.empty();
    }
}
