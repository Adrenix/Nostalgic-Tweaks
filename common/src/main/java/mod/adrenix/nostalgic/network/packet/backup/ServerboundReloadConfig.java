package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigHandler;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.file.Path;

/**
 * This packet instructs the server to hot swap (live reload) the config file that is saved on disk. The server will
 * check if the sender is an operator before performing the I/O reload.
 */
public class ServerboundReloadConfig implements ModPacket
{
    /* Constructors */

    public ServerboundReloadConfig()
    {
    }

    /**
     * Decode a packet received over the network.
     *
     * @param ignored A friendly byte buffer.
     */
    public ServerboundReloadConfig(FriendlyByteBuf ignored)
    {
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        Path reloadLocation = ConfigBuilder.getHandler().getPath();
        ConfigHandler<ServerConfig> hotSwap = ConfigBuilder.temp(ServerConfig.class, reloadLocation);

        if (hotSwap.load())
        {
            ConfigHandler<ServerConfig> handler = ConfigBuilder.getHandler();

            handler.setLoaded(hotSwap.getLoaded());
            handler.save();

            this.log("[Config HotSwap] Reloaded the contents saved on disk");

            TweakPool.filter(Tweak::isMultiplayerLike).forEach(Tweak::sendToAll);
        }
        else
            NostalgicTweaks.LOGGER.error("[Config HotSwap] Could not reload contents saved on disk");
    }
}
