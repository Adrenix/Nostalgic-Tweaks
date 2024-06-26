package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.tweak.listing.ListingPackager;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

abstract class TweakListingPacket<T extends TweakListing<?, ?>> implements TweakPacket
{
    /* Fields */

    protected final ListingPackager<T> packager;
    protected final String poolId;
    protected final boolean disabled;

    /* Constructors */

    /**
     * Prepare a listing tweak to be sent over the network.
     *
     * @param tweak A {@link TweakListing} instance.
     */
    TweakListingPacket(T tweak, Function<T, ? extends Listing<?, ?>> reader)
    {
        this.poolId = tweak.getJsonPathId();
        this.disabled = reader.apply(tweak).isDisabled();
        this.packager = new ListingPackager<>(tweak, this.disabled);
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
        this.disabled = buffer.readBoolean();
        this.packager = new ListingPackager<>(this.poolId, classType, this.disabled);
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeBoolean(this.disabled);
    }

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
