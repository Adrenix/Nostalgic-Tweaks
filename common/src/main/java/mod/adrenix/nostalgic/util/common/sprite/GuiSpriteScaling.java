package mod.adrenix.nostalgic.util.common.sprite;

/**
 * Simple sprite scaling backport from 1.20.2+. This class needs to be server-safe since common utilities will
 * class-load this.
 */
public interface GuiSpriteScaling
{
    /**
     * The sprite will be stretched across the desired space.
     */
    record Stretch() implements GuiSpriteScaling
    {
    }

    /**
     * The sprite will be sliced into four corners, four edges, and one center slice, which will be tiled across the
     * desired space.
     *
     * @param width  The number of pixels for this sprite to cover on-screen across its width.
     * @param height The number of pixels for this sprite to cover on-screen across its height.
     * @param border The {@link NineSlice.Border} slices that should cover on-screen.
     */
    record NineSlice(int width, int height, NineSlice.Border border) implements GuiSpriteScaling
    {
        /**
         * Define on-screen slices.
         *
         * @param left   The number of pixels from the left of the sprite.
         * @param top    The number of pixels from the top of the sprite.
         * @param right  The number of pixels from the right of the sprite.
         * @param bottom The number of pixels from the bottom of the sprite.
         */
        public record Border(int left, int top, int right, int bottom)
        {
        }
    }

    /**
     * The sprite will be repeated across the desired space, starting from the top-left.
     *
     * @param width  The number of pixels for this sprite to cover on-screen across its width.
     * @param height The number of pixels for this sprite to cover on-screen across its height.
     */
    record Tile(int width, int height) implements GuiSpriteScaling
    {
    }
}
