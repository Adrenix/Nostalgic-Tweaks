package mod.adrenix.nostalgic.forge.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

/**
 * Helper class that defines a network channel for Forge.
 * The purpose of this channel is to ensure network protocol matching is enforced.
 */

public abstract class NostalgicNetwork
{
    /**
     * Checks if the given protocol version matches the sided protocol version.
     * @param protocol A protocol version.
     * @return Whether protocols match or <code>true</code> if SSO mode is enabled.
     */
    public static boolean isProtocolMatched(String protocol)
    {
        if (NostalgicTweaks.isServer() && ServerConfigCache.getRoot().serverSideOnlyMode)
            return true;

        return protocol.equals(NostalgicTweaks.PROTOCOL);
    }

    /**
     * Initializes a network channel builder with the mod's identifier and the mod's network protocol.
     * This is a simple network channel that enforces protocols match between the client and server.
     */
    public static void init()
    {
        NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(NostalgicTweaks.MOD_ID, "nt_forge_verification"))
            .networkProtocolVersion(() -> NostalgicTweaks.PROTOCOL)
            .clientAcceptedVersions(NostalgicNetwork::isProtocolMatched)
            .serverAcceptedVersions(NostalgicNetwork::isProtocolMatched)
            .simpleChannel()
        ;
    }
}
