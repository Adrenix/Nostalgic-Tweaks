package mod.adrenix.nostalgic.network;

import mod.adrenix.nostalgic.network.packet.*;

public abstract class PacketRegistry
{
    public static void init()
    {
        PacketC2SChangeTweak.register();
        PacketS2CTweakUpdate.register();
        PacketS2CHandshake.register();
    }
}
