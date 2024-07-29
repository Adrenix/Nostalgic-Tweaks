package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.ResourceLocation;

public abstract class GameAsset
{
    /**
     * Get a new resource location from the game's texture directory.
     *
     * @param path A path in the game's {@code textures} directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as
     * {@code minecraft:textures/$path} where {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation texture(String path)
    {
        return new ResourceLocation("textures/" + path);
    }
}
