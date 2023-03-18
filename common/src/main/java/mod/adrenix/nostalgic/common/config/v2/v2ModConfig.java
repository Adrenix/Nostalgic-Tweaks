package mod.adrenix.nostalgic.common.config.v2;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.v2.cache.ConfigCache;
import mod.adrenix.nostalgic.common.config.v2.network.packet.v2PacketS2CTweakUpdate;
import mod.adrenix.nostalgic.common.config.v2.tweak.TweakSide;
import mod.adrenix.nostalgic.common.config.v2.tweak.Tweak;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;

/**
 * The mod config class acts as the interface for parts of the mod that want to get a value from tweaks in certain game
 * state contexts. Such as returning server sent tweak values when the client is connected to a server running Nostalgic
 * Tweaks.
 *
 * This utility class is used by both the client and server.
 */

public abstract class v2ModConfig
{
    /**
     * Loads the given tweak and checks if its cached value should be used.
     *
     * The server will always use the disk value since there is no global mod state.
     * The client will use the disk value if the mod is enabled, the connection is verified, and the tweak is not dynamic.
     *
     * @param tweak The tweak to load and to check if the disk value should be used.
     * @return Whether the given tweak should be considered "on" or "enabled".
     */
    private static <T> boolean isTweakOn(Tweak<T> tweak)
    {
        // If the tweak is set as "ignored" from automation then it is always on
        if (tweak.isIgnored())
            return true;

        // Code is querying this tweak - load it
        if (tweak.getStatus() != TweakStatus.LOADED)
        {
            tweak.setStatus(TweakStatus.LOADED);

            // Some tweaks will be executing code before the server is started
            if (NostalgicTweaks.isServer() && NostalgicTweaks.getServer() != null)
                PacketUtil.sendToAll(new v2PacketS2CTweakUpdate(tweak));
            else if (NostalgicTweaks.isClient())
            {
                // This if block is nested within the "is client" block to prevent class loading issues in server environments
                if (NetUtil.getIntegratedServer() != null)
                    PacketUtil.sendToAll(NetUtil.getIntegratedServer().getPlayerList().getPlayers(), new v2PacketS2CTweakUpdate(tweak));
            }
        }

        // If the tweak is conflicting with another mod - block it
        if (tweak.isConflicted())
            return false;

        // The server does not need to use a universal enabled/disable state
        if (NostalgicTweaks.isServer())
            return true;
        else if (NetUtil.isLocalHost())
            return true;

        boolean isModEnabled = ConfigCache.client().isModEnabled;

        // Some tweaks run code before network verification can be performed - this bypasses the verification checkpoint
        if (isModEnabled && tweak.isNetworkCheckIgnored())
            return true;

        // If the tweak is server side, and we're not connected to an N.T server, disable the tweak
        if (!NostalgicTweaks.isNetworkVerified() && tweak.getSide() != TweakSide.DYNAMIC)
            return false;

        return isModEnabled;
    }

    /**
     * Get which value from the tweak should be returned based on logical side invoking this method.
     * @param tweak A tweak instance.
     * @param <T> The value type that is associated with the tweak.
     * @return A sided value or server sent value if the client is connecting to a verified server.
     */
    private static <T> T getSidedValue(Tweak<T> tweak)
    {
        // If the tweak is set as "ignored" from automation then its value saved on disk is used
        if (tweak.isIgnored())
            return tweak.getValue();

        // If this method is invoked by a server or a singleplayer server then return its disk value
        if (NostalgicTweaks.isServer() || NetUtil.isSingleplayer())
            return tweak.getValue();

        // If this method is invoked by a client and is connected to an N.T server then return the value sent from the server
        if (tweak.getSide() != TweakSide.CLIENT && NetUtil.isMultiplayer())
            return NostalgicTweaks.isNetworkVerified() ? tweak.getSentValue() : tweak.getValue();

        return tweak.getValue();
    }

    /**
     * This method should be used <b>at all times</b> when getting the value of a tweak. This method will ensure the
     * correct tweak value is returned; such as checking server network verification, additional tweak conditions, and
     * if the mod is an "enabled" state.
     *
     * @param tweak The tweak to get a value from.
     * @param <T> The class type associated with the given tweak.
     * @return A tweak value that is appropriate for the current game state.
     */
    public static <T> T get(Tweak<T> tweak)
    {
        if (!tweak.isExtraConditionMet())
            return tweak.getDisabledValue();

        return isTweakOn(tweak) ? getSidedValue(tweak) : tweak.getDisabledValue();
    }
}
