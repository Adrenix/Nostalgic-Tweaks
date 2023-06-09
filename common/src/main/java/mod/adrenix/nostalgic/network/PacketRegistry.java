package mod.adrenix.nostalgic.network;

import mod.adrenix.nostalgic.network.packet.*;

/**
 * This helper class invokes all packet registration methods.
 * Any newly defined packet classes will need to put their registration call here.
 */

public abstract class PacketRegistry
{
    /**
     * Initialize packet registration.
     * Mod loader registration is handled by Architectury.
     */
    public static void initialize()
    {
        PacketC2SChangeTweak.register();
        PacketS2CTweakUpdate.register();
        PacketS2CHandshake.register();
    }
}
