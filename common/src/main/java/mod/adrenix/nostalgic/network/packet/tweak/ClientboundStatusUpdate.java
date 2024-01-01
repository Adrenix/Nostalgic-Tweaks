package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundStatusUpdate implements TweakPacket
{
    /* Fields */

    protected final String poolId;
    protected final TweakStatus status;

    /* Constructors */

    /**
     * Prepare a tweak status update to be sent over the network.
     *
     * @param tweak A {@link Tweak} that had its status updated.
     */
    public ClientboundStatusUpdate(Tweak<?> tweak)
    {
        this.poolId = tweak.getJsonPathId();
        this.status = tweak.getEnvStatus();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundStatusUpdate(FriendlyByteBuf buffer)
    {
        this.poolId = buffer.readUtf();
        this.status = buffer.readEnum(TweakStatus.class);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.poolId);
        buffer.writeEnum(this.status);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
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
}
