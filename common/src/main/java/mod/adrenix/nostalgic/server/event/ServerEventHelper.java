package mod.adrenix.nostalgic.server.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.network.packet.PacketS2CHandshake;
import mod.adrenix.nostalgic.network.packet.PacketS2CTweakUpdate;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import mod.adrenix.nostalgic.util.server.PlayerServerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class ServerEventHelper
{
    /* Server Network Event Helpers */

    public static void connect(Player player)
    {
        if (player instanceof ServerPlayer)
        {
            PacketUtil.sendToPlayer((ServerPlayer) player, new PacketS2CHandshake());
            TweakServerCache.all().forEach((id, tweak) -> {
                // Syncs server tweaks if singleplayer went to local host session
                if (NostalgicTweaks.isClient())
                {
                    TweakClientCache<?> cache = TweakClientCache.all().get(id);
                    tweak.setValue(cache.getSavedValue());
                    tweak.setServerCache(cache.getSavedValue());

                    if (cache.isSavable())
                        cache.save();
                }

                PacketUtil.sendToPlayer((ServerPlayer) player, new PacketS2CTweakUpdate(tweak));
            });

            PlayerServerUtil.setCreativeHotbar((ServerPlayer) player);
        }
    }

    /* Server Event Helpers */

    public static void instantiate(MinecraftServer instance) { NostalgicTweaks.setServer(instance); }
}
