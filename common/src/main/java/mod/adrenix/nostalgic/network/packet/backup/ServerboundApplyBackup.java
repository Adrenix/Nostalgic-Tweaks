package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigHandler;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.file.Path;

public class ServerboundApplyBackup implements ModPacket
{
    /* Fields */

    private final BackupObject backup;

    /* Constructors */

    /**
     * Apply the given backup to the server's runtime config. A new backup will be made on the server.
     *
     * @param backup A {@link BackupObject} instance.
     */
    public ServerboundApplyBackup(BackupObject backup)
    {
        this.backup = backup;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundApplyBackup(FriendlyByteBuf buffer)
    {
        this.backup = BackupObject.decode(buffer);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        BackupObject.encode(buffer, this.backup);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        Path path = this.backup.getPath();
        String filename = path.getFileName().toString();
        ConfigHandler<ServerConfig> imported = ConfigBuilder.temp(ServerConfig.class, path);

        this.log("Player (%s) requested to apply config backup (%s)", this.getPlayerName(context), filename);

        if (imported.load())
        {
            ConfigHandler<ServerConfig> handler = ConfigBuilder.getHandler();

            ConfigBuilder.getHandler().backup();
            handler.setLoaded(imported.getLoaded());
            handler.save();

            NostalgicTweaks.LOGGER.info("[Config Import] Imported a new server config using backup (%s)", filename);
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundAppliedBackup(true));

            TweakPool.filter(Tweak::isMultiplayerLike).forEach(Tweak::sendToAll);
        }
        else
        {
            NostalgicTweaks.LOGGER.error("[Config Import] Could not import (%s) due to an invalid config format", filename);
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundAppliedBackup(false));
        }
    }
}
