package mod.adrenix.nostalgic.util.common.sprite;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.resources.ResourceLocation;

/**
 * Define a sprite that will be rendered to a graphical user interface. This is a simple backport implementation of the
 * game's new sprite system in 1.20.2+.
 * <p>
 * This class needs to be server-safe since common utilities will class-load this.
 */
public class GuiSprite
{
    /* Builders */

    /**
     * Create a new stretched sprite from an atlas.
     *
     * @param atlas  The {@link SpriteAtlas} instance.
     * @param u0     The u-coordinate of the sprite.
     * @param v0     The v-coordinate of the sprite.
     * @param width  The width of the sprite on the atlas.
     * @param height The height of the sprite on the atlas.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite stretch(SpriteAtlas atlas, int u0, int v0, int width, int height)
    {
        return new GuiSprite(atlas, new GuiSpriteScaling.Stretch(), u0, v0, width, height);
    }

    /**
     * Create a new stretched sprite from a single image.
     *
     * @param atlas The {@link SpriteAtlas} instance.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite stretch(SpriteAtlas atlas)
    {
        return stretch(atlas, 0, 0, atlas.getWidth(), atlas.getHeight());
    }

    /**
     * Create a new tiled sprite from an atlas.
     *
     * @param atlas  The {@link SpriteAtlas} instance.
     * @param u0     The u-coordinate of the sprite.
     * @param v0     The v-coordinate of the sprite.
     * @param width  The width of the sprite on the atlas.
     * @param height The height of the sprite on the atlas.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite tile(SpriteAtlas atlas, int u0, int v0, int width, int height)
    {
        return new GuiSprite(atlas, new GuiSpriteScaling.Tile(width, height), u0, v0, width, height);
    }

    /**
     * Create a new tiled sprite from a single image.
     *
     * @param atlas The {@link SpriteAtlas} instance.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite tile(SpriteAtlas atlas)
    {
        return tile(atlas, 0, 0, atlas.getWidth(), atlas.getHeight());
    }

    /**
     * Create a new nine-slice sprite from an atlas.
     *
     * @param atlas  The {@link SpriteAtlas} instance.
     * @param u0     The u-coordinate of the sprite.
     * @param v0     The v-coordinate of the sprite.
     * @param width  The width of the sprite on the atlas.
     * @param height The height of the sprite on the atlas.
     * @param left   The number of pixels from the left of the sprite for the border.
     * @param top    The number of pixels from the top of the sprite for the border.
     * @param right  The number of pixels from the right of the sprite for the border.
     * @param bottom The number of pixels from the bottom of the sprite for the border.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite nineSlice(SpriteAtlas atlas, int u0, int v0, int width, int height, int left, int top, int right, int bottom)
    {
        GuiSpriteScaling.NineSlice.Border border = new GuiSpriteScaling.NineSlice.Border(left, top, right, bottom);
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(width, height, border);

        return new GuiSprite(atlas, nineSlice, u0, v0, width, height);
    }

    /**
     * Create a new nine-slice sprite from an atlas.
     *
     * @param atlas  The {@link SpriteAtlas} instance.
     * @param u0     The u-coordinate of the sprite.
     * @param v0     The v-coordinate of the sprite.
     * @param width  The width of the sprite on the atlas.
     * @param height The height of the sprite on the atlas.
     * @param border The constant integer for the uniform border size on all sides.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite nineSlice(SpriteAtlas atlas, int u0, int v0, int width, int height, int border)
    {
        return nineSlice(atlas, u0, v0, width, height, border, border, border, border);
    }

    /**
     * Create a new nine-slice sprite from a single image.
     *
     * @param atlas  The {@link SpriteAtlas} instance.
     * @param left   The number of pixels from the left of the sprite for the border.
     * @param top    The number of pixels from the top of the sprite for the border.
     * @param right  The number of pixels from the right of the sprite for the border.
     * @param bottom The number of pixels from the bottom of the sprite for the border.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite nineSlice(SpriteAtlas atlas, int left, int top, int right, int bottom)
    {
        return nineSlice(atlas, 0, 0, atlas.getWidth(), atlas.getHeight(), left, top, right, bottom);
    }

    /**
     * Create a new nine-slice sprite from a single image and uniform border.
     *
     * @param atlas  The {@link SpriteAtlas} instance.
     * @param border The constant integer for the uniform border size on all sides.
     * @return A new {@link GuiSprite} instance.
     */
    @PublicAPI
    public static GuiSprite nineSlice(SpriteAtlas atlas, int border)
    {
        return nineSlice(atlas, 0, 0, atlas.getWidth(), atlas.getHeight(), border, border, border, border);
    }

    /* Fields */

    private final SpriteAtlas atlas;
    private final GuiSpriteScaling scaling;
    private final int width;
    private final int height;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;

    /* Constructor */

    private GuiSprite(SpriteAtlas atlas, GuiSpriteScaling scaling, int u0, int v0, int width, int height)
    {
        this.atlas = atlas;
        this.scaling = scaling;
        this.width = width;
        this.height = height;

        this.u0 = (float) u0 / (float) atlas.getWidth();
        this.v0 = (float) v0 / (float) atlas.getHeight();
        this.u1 = (float) (u0 + width) / (float) atlas.getWidth();
        this.v1 = (float) (v0 + height) / (float) atlas.getHeight();
    }

    /* Methods */

    /**
     * @return The {@link ResourceLocation} atlas instance for this sprite.
     */
    public ResourceLocation atlasLocation()
    {
        return this.getAtlas().getLocation();
    }

    /**
     * @return The {@link SpriteAtlas} instance for this sprite.
     */
    public SpriteAtlas getAtlas()
    {
        return this.atlas;
    }

    /**
     * @return The {@link GuiSpriteScaling} used by this sprite.
     */
    public GuiSpriteScaling getScaling()
    {
        return this.scaling;
    }

    /**
     * @return The width of the sprite texture.
     */
    public int getWidth()
    {
        return this.width;
    }

    /**
     * @return The height of the sprite texture.
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return The minimum U coordinate of this sprite.
     */
    public float getU0()
    {
        return this.u0;
    }

    /**
     * @return The maximum U coordinate of this sprite.
     */
    public float getU1()
    {
        return this.u1;
    }

    /**
     * @return The minimum V coordinate of this sprite.
     */
    public float getV0()
    {
        return this.v0;
    }

    /**
     * @return The maximum V coordinate of this sprite.
     */
    public float getV1()
    {
        return this.v1;
    }

    /**
     * Get a U coordinate.
     *
     * @param u The U scale amount.
     * @return A U coordinate.
     */
    public float getU(float u)
    {
        return this.u0 + (this.u1 - this.u0) * u;
    }

    /**
     * Get a V coordinate.
     *
     * @param v The V scale amount.
     * @return A v coordinate.
     */
    public float getV(float v)
    {
        return this.v0 + (this.v1 - this.v0) * v;
    }
}
