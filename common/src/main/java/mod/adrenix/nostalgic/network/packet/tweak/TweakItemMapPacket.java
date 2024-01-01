package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.TweakItemMap;
import mod.adrenix.nostalgic.tweak.listing.ListingMap;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;
import java.util.function.Function;

abstract class TweakItemMapPacket extends TweakListingPacket<TweakItemMap<?>>
{
    /* Fields */

    protected final Map<String, ?> map;

    /* Constructors */

    /**
     * Prepare a tweak item map to be sent over the network.
     *
     * @param tweak  A {@link TweakItemMap} instance.
     * @param reader A {@link Function} that accepts a {@link TweakItemMap} and returns a {@link ListingMap} to get map
     *               data from.
     */
    TweakItemMapPacket(TweakItemMap<?> tweak, Function<TweakItemMap<?>, ListingMap<?, ?>> reader)
    {
        super(tweak);

        this.map = reader.apply(tweak).getMap();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakItemMapPacket(FriendlyByteBuf buffer)
    {
        super(buffer, TweakItemMap.class);

        this.map = buffer.readMap(FriendlyByteBuf::readUtf, this.packager::readValue);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeMap(this.map, FriendlyByteBuf::writeUtf, this.packager::writeValue);
    }
}
