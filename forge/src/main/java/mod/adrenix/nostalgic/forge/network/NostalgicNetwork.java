package mod.adrenix.nostalgic.forge.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

/**
 * Helper class that defines a network channel for Forge.
 * The purpose of this channel is to ensure network protocol matching is enforced.
 */

public abstract class NostalgicNetwork
{
    /**
     * Initializes a network channel builder with the mod's identifier and the mod's network protocol.
     * This is a simple network channel that enforces protocols match between the client and server.
     */
    public static void init()
    {
        NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(NostalgicTweaks.MOD_ID, "nt_forge_verification"))
            .networkProtocolVersion(() -> NostalgicTweaks.PROTOCOL)
            .clientAcceptedVersions(NostalgicTweaks.PROTOCOL::equals)
            .serverAcceptedVersions(NostalgicTweaks.PROTOCOL::equals)
            .simpleChannel()
        ;
    }
}
