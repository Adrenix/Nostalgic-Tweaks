package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * This packet is sent to the client with details about the mod's current network state.
 * If the client never receives this packet after joining the world, then the mod must assume the server is not
 * running an instance of the mod.
 */

public class PacketS2CHandshake
{
    public static void register()
    {
        NostalgicTweaks.NETWORK.register(
            PacketS2CHandshake.class,
            PacketS2CHandshake::encode,
            PacketS2CHandshake::new,
            PacketS2CHandshake::handle
        );
    }

    private final String protocol;

    public PacketS2CHandshake()
    {
        // Packet Creation

        this.protocol = NostalgicTweaks.PROTOCOL;
    }

    public PacketS2CHandshake(FriendlyByteBuf buffer)
    {
        // Decode Packet into Data

        this.protocol = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        // Encode Data into Packet

        buffer.writeUtf(this.protocol);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Client Received Packet Data
        /*
            WARNING:

            Although the client is handling the received packet data, no client classes can be used here since the
            server will be class loading this packet.
         */

        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            if (context.getEnv() == EnvType.SERVER)
            {
                PacketUtil.warn(EnvType.SERVER, this.getClass());
                return;
            }

            if (this.protocol.equals(NostalgicTweaks.PROTOCOL))
            {
                NostalgicTweaks.setNetworkVerification(true);
                NostalgicTweaks.LOGGER.info(String.format("Successfully connected to N.T supported server with protocol (%s).", NostalgicTweaks.PROTOCOL));
            }
            else
            {
                NostalgicTweaks.setNetworkVerification(false);
                NostalgicTweaks.LOGGER.warn("Connected to a server with N.T but received incorrect protocol.");
                NostalgicTweaks.LOGGER.warn(String.format("Received (%s) :: Expected (%s)", this.protocol, NostalgicTweaks.PROTOCOL));
            }
        });
    }
}
