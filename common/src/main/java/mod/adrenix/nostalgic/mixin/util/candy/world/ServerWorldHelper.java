package mod.adrenix.nostalgic.mixin.util.candy.world;

/**
 * This utility class is used by the client and server.
 */
public abstract class ServerWorldHelper
{
    /**
     * Checks if the given chunk x,z and section x,z are within the defined square view distance.
     *
     * @param chunkX       The x-position of the chunk.
     * @param chunkZ       The z-position of the chunk.
     * @param secX         The x-position of the section.
     * @param secZ         The z-position of the section.
     * @param viewDistance The current viewing distance.
     * @return Whether the given positions are within the defined chessboard constraints.
     */
    public static boolean isChunkInRange(int chunkX, int chunkZ, int secX, int secZ, int viewDistance)
    {
        return Math.max(Math.abs(chunkX - secX), Math.abs(chunkZ - secZ)) <= viewDistance + 1;
    }
}
