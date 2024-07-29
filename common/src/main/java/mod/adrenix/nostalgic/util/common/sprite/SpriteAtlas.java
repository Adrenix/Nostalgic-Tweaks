package mod.adrenix.nostalgic.util.common.sprite;

import mod.adrenix.nostalgic.util.common.asset.GameAsset;
import mod.adrenix.nostalgic.util.common.asset.ModAsset;
import net.minecraft.resources.ResourceLocation;

/**
 * This class needs to be server-safe since common utilities will class-load this.
 */
public class SpriteAtlas
{
    /* Builders */

    /**
     * Get an atlas from the game's gui textures.
     *
     * @param atlasName The name of the gui image file in {@code minecraft:textures/gui/${atlasName}.png}.
     * @return A new {@link SpriteAtlas} instance.
     */
    public static SpriteAtlas fromGui(String atlasName)
    {
        return new SpriteAtlas(GameAsset.texture("gui/" + atlasName + ".png"), 256, 256);
    }

    /**
     * Get an atlas from the mod's sprites.
     *
     * @param spritePath A path in the mod's {@code sprites} directory.
     * @param width      The width of the sprite image.
     * @param height     The height of the sprite image.
     * @return A new {@link SpriteAtlas} instance.
     */
    public static SpriteAtlas fromSprite(String spritePath, int width, int height)
    {
        return new SpriteAtlas(ModAsset.sprite(spritePath), width, height);
    }

    /* Fields */

    private final ResourceLocation resourceLocation;
    private final int width;
    private final int height;

    /* Constructor */

    public SpriteAtlas(ResourceLocation resourceLocation, int width, int height)
    {
        this.resourceLocation = resourceLocation;
        this.width = width;
        this.height = height;
    }

    /* Methods */

    public ResourceLocation getLocation()
    {
        return this.resourceLocation;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }
}
