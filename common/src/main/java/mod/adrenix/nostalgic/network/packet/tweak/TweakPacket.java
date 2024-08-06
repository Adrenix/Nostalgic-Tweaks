package mod.adrenix.nostalgic.network.packet.tweak;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakMeta;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.ClientTimer;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import mod.adrenix.nostalgic.util.server.ServerTimer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface TweakPacket extends ModPacket
{
    /**
     * Find a tweak in the client's tweak pool.
     *
     * @param poolId A tweak's pool identifier.
     * @return An {@link Optional} containing a {@link Tweak}.
     */
    default Optional<Tweak<?>> findOnClient(String poolId)
    {
        Optional<Tweak<?>> found = TweakPool.find(poolId);

        if (found.isEmpty())
        {
            String message = "Server tried changing [tweak={jsonId:%s}] but it doesn't exist in the client config";
            NostalgicTweaks.LOGGER.warn(message, poolId);
        }

        return found;
    }

    /**
     * Change the tweak value client-side. This method does check that the client is handling this packet. If a LAN
     * session is currently in progress, then changes will be saved to disk since the host player became a server
     * player. All operations will be applied safely, and any attempted invalid operations will be logged to the game's
     * console. If this receives a {@code null} value, then the change will be aborted.
     *
     * @param context       The packet context.
     * @param poolId        A tweak's pool identifier.
     * @param receivedValue The value received from the server.
     */
    default void changeOnClient(NetworkManager.PacketContext context, String poolId, Object receivedValue)
    {
        // Check that the server is not handling this
        if (this.isServerHandling(context) || receivedValue == null)
            return;

        // Retrieve client tweak data
        Optional<Tweak<?>> found = this.findOnClient(poolId);

        if (found.isEmpty())
            return;

        // Found tweak
        Tweak<Object> tweak = TweakMeta.wildcard(found.get());

        // Debug information
        String output = "Received tweak update from %s: [tweak={jsonId:%s, new:%s, old:%s}]";
        String jsonId = LogColor.apply(LogColor.LIGHT_PURPLE, poolId);
        String newValue = LogColor.apply(LogColor.BLUE, receivedValue.toString());
        String oldValue = LogColor.apply(LogColor.BLUE, tweak.fromServer().toString());

        // Update the client config if this is a LAN session
        if (NetUtil.isLocalHost())
        {
            // Log changes
            NostalgicTweaks.LOGGER.debug(output, "LAN player", jsonId, newValue, oldValue);

            // Check applications
            boolean diskAppliedSafely = tweak.applySafely(receivedValue, tweak::setDisk);
            boolean localAppliedSafely = tweak.applySafely(receivedValue, tweak::setLocal);

            if (!diskAppliedSafely || !localAppliedSafely)
            {
                String message = "Unable to change [tweak={jsonId:%s}] from (%s) to (%s) - nothing was saved";
                NostalgicTweaks.LOGGER.warn(message, jsonId, oldValue, newValue);

                return;
            }

            ClientTimer.getInstance().runAfter(1L, TimeUnit.SECONDS, ConfigCache::save);
        }
        else
            NostalgicTweaks.LOGGER.debug(output, "server", jsonId, newValue, oldValue);

        // Update the client tweak value with data sent from the server
        boolean appliedSafely = tweak.applySafely(receivedValue, tweak::setReceived);

        // Abort notification if unable to apply safely
        if (!appliedSafely)
        {
            String message = "Received data that could not be applied [tweak={jsonId:%s}] from (%s) to (%s)";
            NostalgicTweaks.LOGGER.warn(message, jsonId, oldValue, newValue);

            return;
        }
        else
            tweak.connect();

        // Notify the client of sent changes
        if (NostalgicTweaks.isNetworkVerified())
        {
            if (NetUtil.isLocalHost())
                ToastNotification.changeOnLan();
            else
                ToastNotification.changeOnClient();
        }
    }

    /**
     * Find a tweak in the server's tweak pool.
     *
     * @param context The packet context.
     * @param poolId  A tweak's pool identifier.
     * @return An {@link Optional} containing a {@link Tweak}.
     */
    default Optional<Tweak<?>> findOnServer(NetworkManager.PacketContext context, String poolId)
    {
        Optional<Tweak<?>> found = TweakPool.find(poolId);

        if (found.isEmpty())
        {
            String message = "Operator (%s) tried changing [tweak={jsonId:%s}] but it doesn't exist in the server config";
            NostalgicTweaks.LOGGER.warn(message, this.getPlayerName(context), poolId);
        }

        return found;
    }

    /**
     * Change the tweak value server-side. This method does enforce that the player is an operator. All operations will
     * be applied safely, and any attempted invalid operations will be logged to the game's console. If this receives a
     * {@code null} value, then the change will be aborted.
     *
     * @param context       The packet context.
     * @param poolId        A tweak's pool identifier.
     * @param receivedValue The value received from an operator.
     */
    default void changeOnServer(NetworkManager.PacketContext context, String poolId, Object receivedValue)
    {
        // Don't trust data sent by a player
        if (receivedValue == null)
            return;

        // Retrieve server tweak data
        Optional<Tweak<?>> found = this.findOnServer(context, poolId);

        if (found.isEmpty())
            return;

        // Found tweak
        Tweak<Object> tweak = TweakMeta.wildcard(found.get());

        // Check that the sender is an operator
        if (this.isNotFromOperator(context))
        {
            tweak.sendToPlayer(this.getServerPlayer(context));
            return;
        }

        // Check if value changed
        boolean hasChanged = tweak.hasChanged(receivedValue);

        // Log any changes
        String update = "Received tweak update request from (%s): [tweak={jsonId:%s, new:%s, old:%s}]";
        String jsonId = LogColor.apply(LogColor.LIGHT_PURPLE, poolId);
        String newValue = LogColor.apply(LogColor.BLUE, receivedValue.toString());
        String oldValue = LogColor.apply(LogColor.BLUE, tweak.fromDisk().toString());

        this.log(update, this.getPlayerName(context), jsonId, newValue, oldValue);

        // Check if LAN change is allowed
        if (NostalgicTweaks.isClient() && NetUtil.isLocalHost() && ModTweak.RESTRICTED_LAN.get())
        {
            String rejected = "LAN player (%s) tried changing tweak(s) but was rejected";
            NostalgicTweaks.LOGGER.debug(rejected, this.getPlayerName(context));

            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundRejection());
            tweak.sendToPlayer(this.getServerPlayer(context));

            return;
        }

        // Update server tweak value with data sent from an operator
        boolean appliedSafely = tweak.applySafely(receivedValue, tweak::setDisk);

        // Abort saving and updating if the server was unable to change the value
        if (!appliedSafely)
        {
            String message = "Unable to change [tweak={jsonId:%s}] from (%s) to (%s) - tweak change and config save aborted";
            NostalgicTweaks.LOGGER.warn(message, jsonId, oldValue, newValue);

            return;
        }

        // Validate the tweak before saving and sending
        TweakValidator.inspect(tweak);

        // An integrated server can handle this packet, so we don't want LAN sessions to save a server config
        if (NostalgicTweaks.isServer() && hasChanged)
            ServerTimer.getInstance().runAfter(1L, TimeUnit.SECONDS, ConfigCache::save);

        // Send tweak update to all connected players
        tweak.sendToAll();
    }
}
