package mod.adrenix.nostalgic.server.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.network.packet.PacketS2CHandshake;
import mod.adrenix.nostalgic.network.packet.PacketS2CTweakUpdate;
import mod.adrenix.nostalgic.util.common.PacketUtil;
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
            TweakServerCache.all().forEach((id, tweak) ->
                PacketUtil.sendToPlayer((ServerPlayer) player, new PacketS2CTweakUpdate(tweak))
            );
        }
    }

    /* Server Event Helpers */

    public static void instantiate(MinecraftServer instance) { NostalgicTweaks.setServer(instance); }
}
