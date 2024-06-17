package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class ServerUtil
{
    /**
     * Get the server's overworld {@link ServerLevel} instance.
     *
     * @return The {@link ServerLevel} instance for the server overworld.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getOverworldLevel()
    {
        if (NostalgicTweaks.isServer() && NostalgicTweaks.getServer() != null)
            return NostalgicTweaks.getServer().getLevel(Level.OVERWORLD);

        return null;
    }
}
