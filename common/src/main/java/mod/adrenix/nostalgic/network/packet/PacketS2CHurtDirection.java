package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * This packet is sent to the client with the server player's calculated hurt direction angle.
 *
 * If the client never receives this packet, then the mod will play the regular hurt animation.
 * The client-side animation can still calculate a random tilt direction effect.
 */

public class PacketS2CHurtDirection
{
    /**
     * Register this packet to the mod's network channel.
     * Channel registration is handled by Architectury.
     */
    public static void register()
    {
        NostalgicTweaks.NETWORK.register
        (
            PacketS2CHurtDirection.class,
            PacketS2CHurtDirection::encode,
            PacketS2CHurtDirection::new,
            PacketS2CHurtDirection::handle
        );
    }

    /* Fields */

    private final float hurtDir;

    /* Constructors */

    /**
     * Create a new hurt direction packet with hurt direction value.
     * This creates a packet using the provided float.
     *
     * @param hurtDir A hurt direction.
     */
    public PacketS2CHurtDirection(float hurtDir) { this.hurtDir = hurtDir; }

    /**
     * Create a new hurt direction packet with a buffer.
     * This decodes a packet into a hurt direction value.
     *
     * @param buffer A friendly byte buffer instance.
     */
    public PacketS2CHurtDirection(FriendlyByteBuf buffer) { this.hurtDir = buffer.readFloat(); }

    /* Methods */

    /**
     * Encode data into the packet.
     * @param buffer A friendly byte buffer instance.
     */
    public void encode(FriendlyByteBuf buffer) { buffer.writeFloat(this.hurtDir); }

    /**
     * Handle packet data.
     * @param supplier A packet context supplier.
     */
    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Client received packet data

        /*
            WARNING:

            Although the client is handling the received packet data, no client classes can be used here since the
            server will be class loading this packet.
         */

        NetworkManager.PacketContext context = supplier.get();

        context.queue(() ->
        {
            if (context.getEnv() == EnvType.SERVER)
            {
                PacketUtil.warn(EnvType.SERVER, this.getClass());
                return;
            }

            if (context.getPlayer().hurtTime > 0 && this.hurtDir == 0.0F)
                context.getPlayer().hurtDir = (int) (Math.random() * 2.0) * 180;
            else
                context.getPlayer().hurtDir = this.hurtDir;
        });
    }
}
