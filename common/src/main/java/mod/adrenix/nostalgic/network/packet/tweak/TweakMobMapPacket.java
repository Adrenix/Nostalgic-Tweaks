package mod.adrenix.nostalgic.network.packet.tweak;

import mod.adrenix.nostalgic.tweak.factory.TweakMobMap;
import mod.adrenix.nostalgic.tweak.listing.ListingMap;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;
import java.util.function.Function;

abstract class TweakMobMapPacket extends TweakListingPacket<TweakMobMap<?>>
{
    /* Fields */

    protected final Map<String, ?> map;

    /* Constructors */

    /**
     * Prepare a tweak string map to be sent over the network.
     *
     * @param tweak  A {@link TweakMobMap} instance.
     * @param reader A {@link Function} that accepts a {@link TweakMobMap} and returns a {@link ListingMap} to get map
     *               data from.
     */
    TweakMobMapPacket(TweakMobMap<?> tweak, Function<TweakMobMap<?>, ListingMap<?, ?>> reader)
    {
        super(tweak, reader);

        this.map = reader.apply(tweak).getMap();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    TweakMobMapPacket(FriendlyByteBuf buffer)
    {
        super(buffer, TweakMobMap.class);

        this.map = buffer.readMap(FriendlyByteBuf::readUtf, this.packager::readValue);
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        super.encoder(buffer);

        buffer.writeMap(this.map, FriendlyByteBuf::writeUtf, this.packager::writeValue);
    }
}
