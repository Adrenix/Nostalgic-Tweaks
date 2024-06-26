package mod.adrenix.nostalgic.util.client.renderer;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;

/**
 * This class follows the concepts of "layers" within a program like Photoshop. A layer with a lower priority number is
 * rendered first. The purpose of this utility is to provide a more robust solution to the game's "blit offset"
 * concept.
 */
public class TextureLayer
{
    /* Builders */

    /**
     * Create a new {@link TextureLayer} using the given index number.
     *
     * @param index Layers are rendered using an index from least to greatest.
     * @return A {@link TextureLayer} instance.
     */
    @PublicAPI
    public static TextureLayer withIndex(int index)
    {
        return new TextureLayer(index);
    }

    /* Static */

    public static final TextureLayer DEFAULT = new TextureLayer(Integer.MAX_VALUE);

    /* Fields */

    final int index;
    final LinkedHashMap<TextureLocation, ArrayDeque<RenderUtil.TextureBuffer>> textureMap;
    final LinkedHashMap<TextureLocation, ArrayDeque<RenderUtil.TextureBuffer>> textureLightMap;
    final LinkedHashMap<ResourceLocation, ArrayDeque<RenderUtil.SpriteBuffer>> spriteMap;
    final LinkedHashMap<ResourceLocation, ArrayDeque<RenderUtil.SpriteBuffer>> spriteLightMap;

    /* Constructor */

    private TextureLayer(int index)
    {
        this.index = index;

        this.textureMap = new LinkedHashMap<>();
        this.textureLightMap = new LinkedHashMap<>();

        this.spriteMap = new LinkedHashMap<>();
        this.spriteLightMap = new LinkedHashMap<>();
    }

    /* Methods */

    /**
     * Add the given buffer to the correct queue map. Textures with brightness values greater than 1 use a separate
     * queue since colored vertices are clamped to colors in a range of 0-255. The shader color system will be used to
     * brighten textures.
     *
     * @param texture A {@link TextureLocation} instance.
     * @param buffer  A {@link RenderUtil.TextureBuffer} instance.
     */
    void add(TextureLocation texture, RenderUtil.TextureBuffer buffer)
    {
        LinkedHashMap<TextureLocation, ArrayDeque<RenderUtil.TextureBuffer>> map;

        if (MathUtil.getLargest(buffer.rgba()[0], buffer.rgba()[1], buffer.rgba()[2]) > 1.0F)
            map = this.textureLightMap;
        else
            map = this.textureMap;

        if (map.containsKey(texture))
            map.get(texture).add(buffer);
        else
        {
            ArrayDeque<RenderUtil.TextureBuffer> queue = new ArrayDeque<>();
            queue.add(buffer);

            map.put(texture, queue);
        }
    }

    /**
     * Add the given buffer to the correct queue map. A sprite with a brightness value greater than 1 uses a separate
     * queue since colored vertices are clamped to colors in a range of 0-255. The shader color system will be used to
     * bright textures.
     *
     * @param atlasLocation A {@link ResourceLocation} texture atlas.
     * @param buffer        A {@link RenderUtil.SpriteBuffer} instance.
     */
    void add(ResourceLocation atlasLocation, RenderUtil.SpriteBuffer buffer)
    {
        LinkedHashMap<ResourceLocation, ArrayDeque<RenderUtil.SpriteBuffer>> map;

        if (MathUtil.getLargest(buffer.rgba()[0], buffer.rgba()[1], buffer.rgba()[2]) > 1.0F)
            map = this.spriteLightMap;
        else
            map = this.spriteMap;

        if (map.containsKey(atlasLocation))
            map.get(atlasLocation).add(buffer);
        else
        {
            ArrayDeque<RenderUtil.SpriteBuffer> queue = new ArrayDeque<>();
            queue.add(buffer);

            map.put(atlasLocation, queue);
        }
    }

    /**
     * Properly clear this texture layer.
     */
    void clear()
    {
        this.textureMap.clear();
        this.textureLightMap.clear();

        this.spriteMap.clear();
        this.spriteLightMap.clear();
    }

    /**
     * @return The ordered priority number for this layer.
     */
    int getIndex()
    {
        return this.index;
    }
}
