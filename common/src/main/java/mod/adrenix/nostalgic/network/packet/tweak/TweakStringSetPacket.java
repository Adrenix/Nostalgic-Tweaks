package mod.adrenix.nostalgic.network.packet.tweak;

import com.google.common.collect.Sets;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import mod.adrenix.nostalgic.tweak.listing.ListingSet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Set;
import java.util.function.Function;

abstract class TweakStringSetPacket extends TweakListingPacket<TweakStringSet>
{
    /* Fields */

    protected final Set<String> set;

    /* Constructors */

    /**
     * Prepare a string set tweak to be sent over the network.
     *
     * @param tweak  A {@link TweakStringSet} instance.
     * @param reader A {@link Function} that accepts a {@link TweakStringSet} and returns a {@link ListingSet} to get
     *               set data from.
     */
    TweakStringSetPacket(TweakStringSet tweak, Function<TweakStringSet, ListingSet<String, ?>> reader)
    {
        super(tweak);

        this.set = reader.apply(tweak).getSet();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakStringSetPacket(FriendlyByteBuf buffer)
    {
        super(buffer, TweakStringSet.class);

        this.set = buffer.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readUtf);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeCollection(this.set, FriendlyByteBuf::writeUtf);
    }
}
