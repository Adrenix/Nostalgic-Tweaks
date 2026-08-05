package mod.adrenix.nostalgic.util.common.world;

import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.server.ServerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class LevelUtil
{
    /**
     * Get a common {@link ServerLevel} instanced associated with the given {@link Level}.
     *
     * @param level The {@link Level} to get dimension data from.
     * @return The {@link ServerLevel} associated with the given {@link Level}, if it is available.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getLevel(Level level)
    {
        Supplier<ServerLevel> client = () -> GameUtil.getLevel(level);
        Supplier<ServerLevel> server = () -> ServerUtil.getLevel(level);

        return EnvExecutor.getEnvSpecific(() -> client, () -> server);
    }

    /**
     * Get a common {@link ServerLevel} instance for the overworld.
     *
     * @return A {@link ServerLevel} instance, if it is available.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getOverworld()
    {
        return EnvExecutor.getEnvSpecific(() -> GameUtil::getOverworldLevel, () -> ServerUtil::getOverworldLevel);
    }
}
