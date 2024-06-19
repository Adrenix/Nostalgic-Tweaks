package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Prepare a tweak status update to be sent over the network.
 *
 * @param poolId The tweak pool json path identifier.
 * @param status The {@link TweakStatus} value.
 */
public record ClientboundStatusUpdate(String poolId, TweakStatus status) implements TweakPacket
{
    /* Type */

    public static final Type<ClientboundStatusUpdate> TYPE = ModPacket.createType(ClientboundStatusUpdate.class);

    /* Constructors */

    /**
     * Prepare a tweak status update to be sent over the network.
     *
     * @param tweak A {@link Tweak} that had its status updated.
     */
    public ClientboundStatusUpdate(Tweak<?> tweak)
    {
        this(tweak.getJsonPathId(), tweak.getEnvStatus());
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundStatusUpdate(FriendlyByteBuf buffer)
    {
        this(buffer.readUtf(), buffer.readEnum(TweakStatus.class));
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeEnum(this.status);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        this.findOnClient(this.poolId).ifPresent(tweak -> tweak.setStatus(CacheMode.NETWORK, this.status));

        // Debug information
        String output = "Received tweak update from server: [tweak={jsonId:%s, status:%s}]";
        String jsonId = LogColor.apply(LogColor.LIGHT_PURPLE, this.poolId);
        String status = TweakStatus.toStringWithColor(this.status);

        NostalgicTweaks.LOGGER.debug(output, jsonId, status);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
