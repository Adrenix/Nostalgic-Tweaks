package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public interface ModPacket extends CustomPacketPayload
{
    /**
     * Register a new packet to the mod's network channel.
     *
     * @param decoder A {@link StreamDecoder} that accepts a {@link FriendlyByteBuf} and returns a packet instance. An
     *                example argument would be {@code TestPacket::new}.
     * @param <T>     The class type of packet.
     */
    static <T extends ModPacket> void register(NetworkManager.Side side, CustomPacketPayload.Type<T> type, StreamDecoder<FriendlyByteBuf, T> decoder)
    {
        NetworkManager.registerReceiver(side, type, CustomPacketPayload.codec(ModPacket::encoder, decoder), ModPacket::receiver);
    }

    /**
     * Get an identifier for a {@link ModPacket} based on its simple class name.
     *
     * @param classType The {@link ModPacket} class type.
     * @return A {@link ResourceLocation} instance to use to identify the packet.
     */
    static <T extends ModPacket> CustomPacketPayload.Type<T> createType(Class<? extends ModPacket> classType)
    {
        String identifier = classType.getSimpleName().toLowerCase(Locale.ROOT);
        ResourceLocation location = new ResourceLocation(NostalgicTweaks.MOD_ID, identifier);

        return new CustomPacketPayload.Type<>(location);
    }

    /**
     * Encode data into the packet.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    void encoder(FriendlyByteBuf buffer);

    /**
     * Receive and process a packet based on network context.
     *
     * @param context The packet context.
     */
    void receiver(NetworkManager.PacketContext context);

    /**
     * Get the server player object from the packet context. It is the responsibility of the caller to ensure the server
     * is handling the packet.
     *
     * @param context The packet context.
     * @return The {@link ServerPlayer} from the packet.
     */
    default ServerPlayer getServerPlayer(NetworkManager.PacketContext context)
    {
        return (ServerPlayer) context.getPlayer();
    }

    /**
     * Reduces the overhead of retrieving the player name stored in the packet context.
     *
     * @param context The packet context.
     * @return The name of the player who sent this packet.
     */
    default String getPlayerName(NetworkManager.PacketContext context)
    {
        Component name = context.getPlayer().getDisplayName();

        if (name == null)
            return "null";

        return name.getString();
    }

    /**
     * Check if a player does not have permission to execute changes on the server. This method also ensures the server
     * is handling the packet before checking player permissions. If the client is handling this packet, then this
     * method will yield {@code true}.
     *
     * @param context The packet context.
     * @return Whether the server player is an operator.
     */
    default boolean isNotFromOperator(NetworkManager.PacketContext context)
    {
        if (this.isClientHandling(context))
            return true;

        ServerPlayer player = (ServerPlayer) context.getPlayer();
        String playerName = this.getPlayerName(context);

        if (PacketUtil.isPlayerOp(player))
            return false;

        NostalgicTweaks.LOGGER.warn("Player (%s) tried changing server data without permission", playerName);
        return true;
    }

    /**
     * Output a message to the server console window for logging. This will check and ensure packet logging is enabled
     * before printing a message.
     *
     * @param message The info message to log.
     * @param args    String formatting arguments.
     */
    default void log(String message, Object... args)
    {
        if (NostalgicTweaks.isServer() && ModTweak.SERVER_LOGGING.get())
            NostalgicTweaks.LOGGER.info(message, args);
    }

    /**
     * Check if the given environment type side is handling this packet.
     *
     * @param context The network packet context.
     * @param envType The environment type.
     * @return Whether the packet is being handled by the given environment type.
     * @see #isClientHandling(NetworkManager.PacketContext)
     * @see #isServerHandling(NetworkManager.PacketContext)
     */
    default boolean isEnvHandling(NetworkManager.PacketContext context, EnvType envType)
    {
        if (context.getEnv() == envType)
        {
            PacketUtil.warn(envType, this.getClass());
            return true;
        }

        return false;
    }

    /**
     * Check if the client is handling this packet.
     *
     * @param context The network packet context.
     * @return Whether the client is handling this packet.
     */
    default boolean isClientHandling(NetworkManager.PacketContext context)
    {
        return this.isEnvHandling(context, EnvType.CLIENT);
    }

    /**
     * Check if the server is handling this packet.
     *
     * @param context The network packet context.
     * @return Whether the server is handling this packet.
     */
    default boolean isServerHandling(NetworkManager.PacketContext context)
    {
        return this.isEnvHandling(context, EnvType.SERVER);
    }
}
