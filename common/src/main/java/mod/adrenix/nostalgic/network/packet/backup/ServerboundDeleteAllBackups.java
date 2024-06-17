package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ServerboundDeleteAllBackups implements ModPacket
{
    /* Constructors */

    /**
     * Create a new "delete all the backup config files" packet instance. The request will be rejected if the sender of
     * the packet is not an operator.
     */
    public ServerboundDeleteAllBackups()
    {
    }

    /**
     * Decode a packet received over the network.
     *
     * @param ignored A {@link FriendlyByteBuf} instance.
     */
    public ServerboundDeleteAllBackups(FriendlyByteBuf ignored)
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

        NostalgicTweaks.LOGGER.info("Player (%s) has deleted all config backup files", this.getPlayerName(context));

        try
        {
            List<Path> files = PathUtil.getNewestFiles(PathUtil.getBackupPath(), PathUtil::isJsonFile);

            for (Path path : files)
                PathUtil.deleteWithoutCatch(path);

            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundDeletedAllBackups(true));
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("[I/O Error] Could not delete all config backup files\n%s", exception);
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundDeletedAllBackups(false));
        }
    }
}
