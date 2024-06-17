package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;

public interface Listing<V, L extends Listing<V, L>>
{
    /**
     * Each implementation of this interface needs to provide this method so that multiple copies of the listing can be
     * created. This is needed for separating what is saved on disk, what is saved in the user interface cache, and what
     * is sent from a server running this mod.
     *
     * @return A new {@link Listing} instance.
     */
    L create();

    /**
     * Copy the contents from another {@link Listing} to this {@link Listing}.
     *
     * @param list The {@link Listing} to copy.
     */
    void copy(L list);

    /**
     * Clears the data stored in the listing.
     */
    void clear();

    /**
     * Perform operations when the cache is being applied to local lists or network lists.
     */
    default void applyCache()
    {
    }

    /**
     * Change the disabled state of a listing.
     *
     * @param state The new disabled boolean flag.
     */
    void setDisabled(boolean state);

    /**
     * @return Whether the listing is in a disabled state.
     */
    boolean isDisabled();

    /**
     * Check if the given {@link Listing} matches up with this {@link Listing}.
     *
     * @param listing The {@link Listing} to check.
     * @return Whether the given {@link Listing} matches up with this {@link Listing}.
     */
    boolean matches(L listing);

    /**
     * Check if the given object is considered a key within a {@link Listing}.
     *
     * @param object An {@link Object} instance.
     * @return Whether the given object was found as a key within a {@link Listing}.
     */
    boolean containsKey(Object object);

    /**
     * Each listing should provide a validation procedure. For example, listings that could manage numeric data that is
     * bounded should perform those bound checks within this method.
     *
     * @param validator A {@link TweakValidator} instance.
     * @param tweak     A {@link Tweak} instance.
     * @return Whether validation was successful.
     */
    boolean validate(TweakValidator validator, TweakListing<V, L> tweak);

    /**
     * Each listing should be tracking the class type of the value it stores. Having this information is useful in many
     * situations, such as validating listing data.
     *
     * @return A class type of the value stored in the listing.
     */
    Class<V> genericType();

    /**
     * When tweak information is displayed in logging output, this method will be used to represent the listing as a
     * string. It is not advised to output the contents of the listing, just important data such as size the class type
     * that is stored.
     *
     * @return A debug string for logging.
     */
    String debugString();
}
