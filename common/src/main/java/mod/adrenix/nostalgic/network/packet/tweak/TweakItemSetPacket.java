package mod.adrenix.nostalgic.network.packet.tweak;

import com.google.common.collect.Sets;
import mod.adrenix.nostalgic.tweak.factory.TweakItemSet;
import mod.adrenix.nostalgic.tweak.listing.ListingSet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Set;
import java.util.function.Function;

abstract class TweakItemSetPacket extends TweakListingPacket<TweakItemSet>
{
    /* Fields */

    protected final Set<String> set;

    /* Constructors */

    /**
     * Prepare a tweak item set to be sent over the network.
     *
     * @param tweak  A {@link TweakItemSet} instance.
     * @param reader A {@link Function} that accepts a {@link TweakItemSet} and returns a {@link ListingSet} to get set
     *               data from.
     */
    TweakItemSetPacket(TweakItemSet tweak, Function<TweakItemSet, ListingSet<String, ?>> reader)
    {
        super(tweak, reader);

        this.set = reader.apply(tweak).getSet();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakItemSetPacket(FriendlyByteBuf buffer)
    {
        super(buffer, TweakItemSet.class);

        this.set = buffer.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readUtf);
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        super.encoder(buffer);

        buffer.writeCollection(this.set, FriendlyByteBuf::writeUtf);
    }
}
