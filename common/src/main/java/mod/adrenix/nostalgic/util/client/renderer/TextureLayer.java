package mod.adrenix.nostalgic.util.client.renderer;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;

/**
 * This class follows the concepts of "layers" within a program like Photoshop. A layer with a lower priority number is
 * rendered first.
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
    final LinkedHashMap<ResourceLocation, ArrayDeque<RenderUtil.TextureBuffer>> queueMap;
    final LinkedHashMap<ResourceLocation, ArrayDeque<RenderUtil.TextureBuffer>> brightMap;

    /* Constructor */

    private TextureLayer(int index)
    {
        this.index = index;
        this.queueMap = new LinkedHashMap<>();
        this.brightMap = new LinkedHashMap<>();
    }

    /* Methods */

    /**
     * Add the given buffer to the correct queue map. Textures with brightness values greater than 1 use a separate
     * queue since colored vertices are clamped to colors in a range of 0-255. The shader color system will be used to
     * brighten textures.
     *
     * @param location A {@link ResourceLocation} instance.
     * @param texture  A {@link RenderUtil.TextureBuffer} instance.
     */
    void add(ResourceLocation location, RenderUtil.TextureBuffer texture)
    {
        LinkedHashMap<ResourceLocation, ArrayDeque<RenderUtil.TextureBuffer>> map;

        if (MathUtil.getLargest(texture.rgba()[0], texture.rgba()[1], texture.rgba()[2]) > 1.0F)
            map = this.brightMap;
        else
            map = this.queueMap;

        if (map.containsKey(location))
            map.get(location).add(texture);
        else
        {
            ArrayDeque<RenderUtil.TextureBuffer> buffer = new ArrayDeque<>();
            buffer.add(texture);

            map.put(location, buffer);
        }
    }

    /**
     * Properly clear this texture layer.
     */
    void clear()
    {
        this.queueMap.clear();
        this.brightMap.clear();
    }

    /**
     * @return The ordered priority number for this layer.
     */
    int getIndex()
    {
        return this.index;
    }
}
