package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.PacketByteBuf;
import java.util.function.Supplier;

/**
 * This packet is sent to the client with the server player's calculated hurt direction angle.
 *
 * If the client never receives this packet, then the mod will play the regular hurt animation.
 * The client-side animation can still calculate a random tilt direction effect.
 */

public class PacketS2CHurtDirection
{
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

    private final float hurtDir;

    public PacketS2CHurtDirection(float hurtDir)
    {
        // Packet creation
        this.hurtDir = hurtDir;
    }

    public PacketS2CHurtDirection(PacketByteBuf buffer)
    {
        // Decode packet into Data
        this.hurtDir = buffer.readFloat();
    }

    public void encode(PacketByteBuf buffer)
    {
        // Encode data into packet
        buffer.writeFloat(this.hurtDir);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Client received packet data
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

            if (context.getPlayer().hurtTime > 0 && this.hurtDir == 0.0F)
                context.getPlayer().knockbackVelocity = (int) (Math.random() * 2.0) * 180;
            else
                context.getPlayer().knockbackVelocity = this.hurtDir;
        });
    }
}
