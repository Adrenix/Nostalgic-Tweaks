package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.tweak.ClientboundStatusUpdate;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;

/**
 * This utility class is used by both the client and server.
 */
public abstract class TweakPipeline
{
    /**
     * Loads the given tweak and checks if its cached value should be used.
     * <p><br>
     * The server will always use the disk value since there is no global mod state. The client will use the disk value
     * if the mod is enabled, the connection is verified, and the tweak is not dynamic.
     *
     * @param tweak The tweak to load and to check if the disk value should be used.
     * @return Whether the given tweak should be considered {@code enabled}.
     */
    private static boolean isTweakOn(Tweak<?> tweak)
    {
        // If the tweak is set as "ignored" from automation, then it is always on
        if (tweak.isIgnored())
            return true;

        // Code is querying this tweak - load it
        if (tweak.getEnvStatus() != TweakStatus.LOADED)
        {
            tweak.setEnvStatus(TweakStatus.LOADED);

            // Some tweaks will be executing code before the server is started
            if (NostalgicTweaks.isServer() && NostalgicTweaks.getServer() != null)
                PacketUtil.sendToAll(new ClientboundStatusUpdate(tweak));
            else if (NostalgicTweaks.isClient())
            {
                // This if block is nested within the "is client" block to prevent class loading issues in server environments
                if (NetUtil.getIntegratedServer() != null)
                    PacketUtil.sendToAll(NetUtil.getIntegratedPlayers(), new ClientboundStatusUpdate(tweak));
            }
        }

        // If the tweak is conflicting with another mod - block it
        if (tweak.isModConflict())
            return false;

        // The server does not need to use a universal enabled/disable state
        if (NostalgicTweaks.isServer())
            return true;
        else if (NetUtil.isLocalHost())
            return true;

        // Check if the mod is an enabled global state
        boolean isModEnabled = ModTweak.ENABLED.fromDisk();

        // Some tweaks run code before network verification can be performed - this bypasses the verification checkpoint
        if (isModEnabled && tweak.isNetworkCheckIgnored())
            return true;

        // If the tweak is for the server, and we're not connected to an N.T server, disable the tweak
        if (!NostalgicTweaks.isNetworkVerified() && tweak.getEnv() == TweakEnv.SERVER)
            return false;

        return isModEnabled;
    }

    /**
     * Get which value from the tweak cache should be returned based on the environment invoking this method.
     *
     * @param tweak A tweak instance.
     * @param <T>   The value type that is associated with the tweak.
     * @return A local value, or server sent value if the client is connecting to a verified server.
     */
    private static <T> T getValue(Tweak<T> tweak)
    {
        // If the tweak is set as "ignored" from automation, then its value saved on disk is used
        if (tweak.isIgnored())
            return tweak.fromDisk();

        // If this method is invoked by a server or a singleplayer server, then return its disk value
        if (NostalgicTweaks.isServer() || NetUtil.isSingleplayer())
            return tweak.fromDisk();

        // If this method is invoked by a client and is connected to an N.T server, then return the value sent from the server
        if (tweak.getEnv() != TweakEnv.CLIENT && NetUtil.isMultiplayer())
            return NostalgicTweaks.isNetworkVerified() ? tweak.fromServer() : tweak.fromDisk();

        return tweak.fromDisk();
    }

    /**
     * This method should be used <b>at all times</b> when getting the value of a tweak. This method will ensure the
     * correct tweak value is returned, such as checking server network verification, additional tweak conditions, and
     * if the mod is an "enabled" state.
     *
     * @param tweak The tweak to get tweak data from.
     * @param <T>   The class type associated with the given tweak.
     * @return A tweak value that is appropriate for the current game state.
     */
    public static <T> T get(Tweak<T> tweak)
    {
        return isTweakOn(tweak) ? getValue(tweak) : tweak.getDisabled();
    }
}
