package mod.adrenix.nostalgic.server.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.network.packet.PacketS2CHandshake;
import mod.adrenix.nostalgic.network.packet.PacketS2CTweakUpdate;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.server.PlayerServerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * This helper class provides instructions for various server events. These methods are used by both mod loader event
 * subscriptions. Any unique instructions are handled by their respective mod loader helpers.
 */

public abstract class ServerEventHelper
{
    /*
       Server Network Helpers

       The following methods are used by the server's networking events.
     */

    /**
     * This method provides instructions for the mod to perform after a player connects to the server level.
     * Disconnection instructions are handled by the client. The server will verify network protocol and inform the
     * client that it is connected to a server with Nostalgic Tweaks installed.
     *
     * @param player A player instance.
     */
    public static void connect(Player player)
    {
        if (player instanceof ServerPlayer)
        {
            String loader = TextUtil.toTitleCase(NostalgicTweaks.getLoader());
            String tiny = NostalgicTweaks.getTinyVersion();
            String beta = NostalgicTweaks.getBetaVersion();
            String version = beta.isEmpty() ? tiny : tiny + "-" + beta;
            String protocol = NostalgicTweaks.getProtocol();

            PacketUtil.sendToPlayer((ServerPlayer) player, new PacketS2CHandshake(loader, version, protocol));

            TweakServerCache.all().forEach((id, tweak) ->
            {
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

    /**
     * Defines the minecraft server instance in the mod's main class.
     * @param instance A minecraft server instance.
     */
    public static void instantiate(MinecraftServer instance) { NostalgicTweaks.setServer(instance); }
}
