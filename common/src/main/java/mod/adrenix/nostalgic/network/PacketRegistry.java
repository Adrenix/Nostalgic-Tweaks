package mod.adrenix.nostalgic.network;

import mod.adrenix.nostalgic.network.packet.ClientboundHandshake;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.network.packet.ServerboundSync;
import mod.adrenix.nostalgic.network.packet.backup.*;
import mod.adrenix.nostalgic.network.packet.tweak.*;

public abstract class PacketRegistry
{
    public static void register()
    {
        // Handshake
        ModPacket.register(ClientboundHandshake.class, ClientboundHandshake::new);
        ModPacket.register(ServerboundSync.class, ServerboundSync::new);

        // Config Backups
        ModPacket.register(ServerboundCreateBackup.class, ServerboundCreateBackup::new);
        ModPacket.register(ClientboundMadeBackup.class, ClientboundMadeBackup::new);
        ModPacket.register(ClientboundBackupObjects.class, ClientboundBackupObjects::new);
        ModPacket.register(ServerboundRequestBackups.class, ServerboundRequestBackups::new);
        ModPacket.register(ClientboundBackupDownload.class, ClientboundBackupDownload::new);
        ModPacket.register(ServerboundDownloadRequest.class, ServerboundDownloadRequest::new);
        ModPacket.register(ServerboundDeleteBackup.class, ServerboundDeleteBackup::new);
        ModPacket.register(ClientboundBackupDeleted.class, ClientboundBackupDeleted::new);
        ModPacket.register(ServerboundDeleteAllBackups.class, ServerboundDeleteAllBackups::new);
        ModPacket.register(ClientboundDeletedAllBackups.class, ClientboundDeletedAllBackups::new);
        ModPacket.register(ServerboundApplyBackup.class, ServerboundApplyBackup::new);
        ModPacket.register(ClientboundAppliedBackup.class, ClientboundAppliedBackup::new);
        ModPacket.register(ServerboundReloadConfig.class, ServerboundReloadConfig::new);

        // Tweak Packets
        ModPacket.register(ClientboundRejection.class, ClientboundRejection::new);
        ModPacket.register(ClientboundStatusUpdate.class, ClientboundStatusUpdate::new);
        ModPacket.register(ServerboundTweakFlag.class, ServerboundTweakFlag::new);
        ModPacket.register(ClientboundTweakFlag.class, ClientboundTweakFlag::new);
        ModPacket.register(ServerboundTweakText.class, ServerboundTweakText::new);
        ModPacket.register(ClientboundTweakText.class, ClientboundTweakText::new);
        ModPacket.register(ServerboundTweakEnum.class, ServerboundTweakEnum::new);
        ModPacket.register(ClientboundTweakEnum.class, ClientboundTweakEnum::new);
        ModPacket.register(ServerboundTweakNumber.class, ServerboundTweakNumber::new);
        ModPacket.register(ClientboundTweakNumber.class, ClientboundTweakNumber::new);
        ModPacket.register(ServerboundTweakItemMap.class, ServerboundTweakItemMap::new);
        ModPacket.register(ClientboundTweakItemMap.class, ClientboundTweakItemMap::new);
        ModPacket.register(ServerboundTweakItemSet.class, ServerboundTweakItemSet::new);
        ModPacket.register(ClientboundTweakItemSet.class, ClientboundTweakItemSet::new);
        ModPacket.register(ServerboundTweakStringSet.class, ServerboundTweakStringSet::new);
        ModPacket.register(ClientboundTweakStringSet.class, ClientboundTweakStringSet::new);
    }
}
