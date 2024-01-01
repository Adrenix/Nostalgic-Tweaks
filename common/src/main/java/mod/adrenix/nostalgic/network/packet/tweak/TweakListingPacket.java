package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ListingPackager;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

abstract class TweakListingPacket<T extends TweakListing<?, ?>> implements TweakPacket
{
    /* Fields */

    protected final ListingPackager<T> packager;
    protected final String poolId;

    /* Constructors */

    /**
     * Prepare a listing tweak to be sent over the network.
     *
     * @param tweak A {@link TweakListing} instance.
     */
    TweakListingPacket(T tweak)
    {
        this.poolId = tweak.getJsonPathId();
        this.packager = new ListingPackager<>(tweak);
    }

    /**
     * Decode a buffer received over the network using a known listing tweak class type.
     *
     * @param buffer    A {@link FriendlyByteBuf} instance.
     * @param classType The known {@link TweakListing} class type being used by the packet.
     */
    TweakListingPacket(FriendlyByteBuf buffer, Class<? super T> classType)
    {
        this.poolId = buffer.readUtf();
        this.packager = new ListingPackager<>(this.poolId, classType);
    }

    /* Methods */

    /**
     * @return A {@link TweakListing} instance that will be retrieved from the {@link ListingPackager} instance used by
     * this packet. A {@code null} result will be returned if the packager was unable to find a tweak in the tweak
     * pool.
     */
    @Nullable
    protected T getTweak()
    {
        return this.packager.getTweak();
    }
}
