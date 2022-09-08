package mod.adrenix.nostalgic.util.server;

/**
 * This utility is used by the server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.WorldClientUtil}.
 */

public abstract class WorldServerUtil
{
    /**
     * Checks if a chunk is on the edge of a square border render distance.
     * @param chunkX The x-position of the chunk.
     * @param chunkZ The y-position of the chunk.
     * @param secX The x-position of the section.
     * @param secZ The y-position of the section.
     * @return If the given positions are within the defined chessboard.
     */
    public static int squareDistance(int chunkX, int chunkZ, int secX, int secZ)
    {
        int diffX = chunkX - secX;
        int diffY = chunkZ - secZ;
        return Math.max(Math.abs(diffX), Math.abs(diffY));
    }
}
