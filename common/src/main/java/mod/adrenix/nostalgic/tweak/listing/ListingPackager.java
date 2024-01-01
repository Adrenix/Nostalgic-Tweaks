package mod.adrenix.nostalgic.tweak.listing;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Utility class for packing and unpacking {@link TweakListing} tweaks to be sent or received over the network. Any
 * tweak that uses {@link ListingSet} or {@link ListingMap} will be covered by this packager.
 */
public class ListingPackager<T extends TweakListing<?, ?>>
{
    /* Fields */

    @Nullable private final T tweak;

    /* Constructors */

    /**
     * Create a new {@link ListingPackager} instance with a known {@link TweakListing}.
     *
     * @param tweak A {@link TweakListing} instance.
     */
    public ListingPackager(@Nullable T tweak)
    {
        this.tweak = tweak;
    }

    /**
     * Create a new {@link ListingPackager} instance with a tweak's pool identifier. If the tweak could not be found,
     * then a warning is sent to the game's console and the tweak value will be {@code null}.
     *
     * @param poolId    A tweak pool identifier.
     * @param classType The class type of the expected tweak.
     */
    @SuppressWarnings("unchecked") // Tweak class type is checked before setting
    public ListingPackager(String poolId, Class<? super T> classType)
    {
        Tweak<?> found = TweakPool.find(poolId).orElse(null);

        if (found != null)
        {
            if (classType.isAssignableFrom(found.getClass()))
                this.tweak = (T) found;
            else
            {
                NostalgicTweaks.LOGGER.warn("Unable to cast packager [tweak={jsonId:%s, classType:%s}]", poolId, classType);
                this.tweak = null;
            }
        }
        else
        {
            NostalgicTweaks.LOGGER.warn("Unable to find listing using [tweak={jsonId:%s}]", poolId);
            this.tweak = null;
        }
    }

    /* Methods */

    /**
     * Get the tweak that is being used by this packager.
     *
     * @return The tweak instance, or {@code null} if it wasn't found.
     */
    @Nullable
    public T getTweak()
    {
        return this.tweak;
    }

    /**
     * Get a new {@link ListingSet} instance with the given {@link Set} safely applied to it. This should be used when
     * attempting to update a {@link TweakListing} value.
     *
     * @param set A {@link Set} to apply to the {@link ListingSet}.
     * @return A {@link ListingSet}, or {@code null} if the {@link TweakListing} was {@code null} or the {@link Listing}
     * was not a {@link ListingSet}.
     */
    public @Nullable ListingSet<?, ?> getListingSet(Set<?> set)
    {
        if (this.tweak == null)
            return null;

        if (this.tweak.getDefault().create() instanceof ListingSet<?, ?> listing)
        {
            listing.clear();
            listing.acceptSafely(set);

            return listing;
        }

        return null;
    }

    /**
     * Get a new {@link ListingMap} instance with the given {@link Map} safely applied to it. This should be used when
     * attempting to update a {@link TweakListing} value.
     *
     * @param map A {@link Map} to apply to the {@link ListingMap}.
     * @return A {@link ListingMap}, or {@code null} if the {@link TweakListing} was {@code null} or the {@link Listing}
     * was not a {@link ListingMap}.
     */
    public @Nullable ListingMap<?, ?> getListingMap(Map<String, ?> map)
    {
        if (this.tweak == null)
            return null;

        if (this.tweak.getDefault().create() instanceof ListingMap<?, ?> listing)
        {
            listing.clear();
            listing.acceptSafely(map);

            return listing;
        }

        return null;
    }

    /**
     * Uses {@link Gson} to convert a {@code String} to its correct class instance using {@link Listing#genericType()}.
     * If this packager's tweak is {@code null}, then the value will be cast to {@link Object}.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     * @return An {@link Object} with a cast based on {@link Listing#genericType()}.
     */
    public Object readValue(FriendlyByteBuf buffer)
    {
        if (this.tweak == null)
            return new Gson().fromJson(buffer.readUtf(), Object.class);

        try
        {
            return new Gson().fromJson(buffer.readUtf(), this.tweak.getDefault().genericType());
        }
        catch (JsonSyntaxException exception)
        {
            String jsonId = this.tweak.getJsonPathId();
            String genericType = this.tweak.getDefault().genericType().toString();
            NostalgicTweaks.LOGGER.error("Could not parse [tweak={jsonId:%s}] using (%s)", jsonId, genericType);

            return new Gson().fromJson(buffer.readUtf(), Object.class);
        }
    }

    /**
     * Simply converts the given object to a string and writes to buffer in {@code UTF}.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     * @param object An {@link Object} instance.
     */
    public void writeValue(FriendlyByteBuf buffer, Object object)
    {
        buffer.writeUtf(object.toString());
    }
}
