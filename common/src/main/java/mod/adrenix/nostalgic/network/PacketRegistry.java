package mod.adrenix.nostalgic.network;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.network.packet.backup.*;
import mod.adrenix.nostalgic.network.packet.sync.ClientboundHandshake;
import mod.adrenix.nostalgic.network.packet.sync.ServerboundSync;
import mod.adrenix.nostalgic.network.packet.tweak.*;

public abstract class PacketRegistry
{
    public static void register()
    {
        // Handshake
        ModPacket.register(NetworkManager.Side.S2C, ClientboundHandshake.TYPE, ClientboundHandshake::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundSync.TYPE, ServerboundSync::new);

        // Config Backups
        ModPacket.register(NetworkManager.Side.S2C, ClientboundAppliedBackup.TYPE, ClientboundAppliedBackup::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundBackupDeleted.TYPE, ClientboundBackupDeleted::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundBackupDownload.TYPE, ClientboundBackupDownload::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundBackupObjects.TYPE, ClientboundBackupObjects::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundDeletedAllBackups.TYPE, ClientboundDeletedAllBackups::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundMadeBackup.TYPE, ClientboundMadeBackup::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundApplyBackup.TYPE, ServerboundApplyBackup::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundCreateBackup.TYPE, ServerboundCreateBackup::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundDeleteAllBackups.TYPE, ServerboundDeleteAllBackups::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundDeleteBackup.TYPE, ServerboundDeleteBackup::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundDownloadRequest.TYPE, ServerboundDownloadRequest::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundReloadConfig.TYPE, ServerboundReloadConfig::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundRequestBackups.TYPE, ServerboundRequestBackups::new);

        // Tweak Packets
        ModPacket.register(NetworkManager.Side.S2C, ClientboundRejection.TYPE, ClientboundRejection::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundStatusUpdate.TYPE, ClientboundStatusUpdate::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakEnum.TYPE, ClientboundTweakEnum::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakFlag.TYPE, ClientboundTweakFlag::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakItemMap.TYPE, ClientboundTweakItemMap::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakItemSet.TYPE, ClientboundTweakItemSet::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakNumber.TYPE, ClientboundTweakNumber::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakStringSet.TYPE, ClientboundTweakStringSet::new);
        ModPacket.register(NetworkManager.Side.S2C, ClientboundTweakText.TYPE, ClientboundTweakText::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakEnum.TYPE, ServerboundTweakEnum::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakFlag.TYPE, ServerboundTweakFlag::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakItemMap.TYPE, ServerboundTweakItemMap::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakItemSet.TYPE, ServerboundTweakItemSet::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakNumber.TYPE, ServerboundTweakNumber::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakStringSet.TYPE, ServerboundTweakStringSet::new);
        ModPacket.register(NetworkManager.Side.C2S, ServerboundTweakText.TYPE, ServerboundTweakText::new);
    }
}
