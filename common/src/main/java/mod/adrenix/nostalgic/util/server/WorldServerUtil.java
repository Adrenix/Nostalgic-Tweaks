package mod.adrenix.nostalgic.util.server;

/**
 * This utility is used by the server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.WorldClientUtil}.
 */

public abstract class WorldServerUtil
{
    /**
     * Checks if the given chunk x,z and section x,z are within the defined view distance.
     * @param chunkX The x-position of the chunk.
     * @param chunkZ The z-position of the chunk.
     * @param secX The x-position of the section.
     * @param secZ The z-position of the section.
     * @param viewDistance The current viewing distance.
     * @return If the given positions are within the defined chessboard constraints.
     */
    public static boolean isChunkInRange(int chunkX, int chunkZ, int secX, int secZ, int viewDistance)
    {
        return Math.max(Math.abs(chunkX - secX), Math.abs(chunkZ - secZ)) <= viewDistance;
    }
}
