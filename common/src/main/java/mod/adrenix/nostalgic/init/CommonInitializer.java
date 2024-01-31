package mod.adrenix.nostalgic.init;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ClientboundHandshake;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import mod.adrenix.nostalgic.util.server.ServerTimer;
import mod.adrenix.nostalgic.util.server.world.ServerPlayerUtil;
import net.minecraft.server.level.ServerPlayer;

abstract class CommonInitializer
{
    /**
     * Registers common events.
     */
    static void register()
    {
        LifecycleEvent.SERVER_BEFORE_START.register(NostalgicTweaks::setServer);
        PlayerEvent.PLAYER_JOIN.register(CommonInitializer::onPlayerJoin);
        TickEvent.SERVER_PRE.register(server -> ServerTimer.getInstance().onTick());
    }

    /**
     * This method provides instructions for the mod to perform after a player connects to the server level. The client
     * handles disconnection instructions. The server will verify network protocol and inform the client that it is
     * connected to a server with Nostalgic Tweaks installed.
     *
     * @param player A {@link ServerPlayer} instance.
     */
    private static void onPlayerJoin(ServerPlayer player)
    {
        String loader = TextUtil.toTitleCase(NostalgicTweaks.getLoader());
        String tiny = NostalgicTweaks.getTinyVersion();
        String beta = NostalgicTweaks.getBetaVersion();
        String version = beta.isEmpty() ? tiny : tiny + "-" + beta;
        String protocol = NostalgicTweaks.getProtocol();

        PacketUtil.sendToPlayer(player, new ClientboundHandshake(loader, version, protocol));
        TweakPool.filter(Tweak::isMultiplayerLike).forEach(tweak -> tweak.sendToPlayer(player));

        ServerPlayerUtil.setCreativeHotbar(player);
    }
}
