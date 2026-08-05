package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class ServerUtil
{
    /**
     * Get the {@link ServerLevel} associated with the given dimension {@link ResourceKey<Level>} on the server.
     *
     * @param dimension The dimension's {@link ResourceKey<Level>}.
     * @return A {@link ServerLevel} associated with the given dimension {@link ResourceKey<Level>}, if available.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getLevel(ResourceKey<Level> dimension)
    {
        if (NostalgicTweaks.isServer() && NostalgicTweaks.getServer() != null)
            return NostalgicTweaks.getServer().getLevel(dimension);

        return null;
    }

    /**
     * Get the {@link ServerLevel} associated with the given {@link Level} on the server.
     *
     * @param level The {@link Level} to get dimension data from.
     * @return The {@link ServerLevel} associated with the provided {@link Level}, if available.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getLevel(Level level)
    {
        return getLevel(level.dimension());
    }

    /**
     * Get the server's overworld {@link ServerLevel} instance.
     *
     * @return The {@link ServerLevel} instance for the server overworld, if available.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getOverworldLevel()
    {
        return getLevel(Level.OVERWORLD);
    }
}
