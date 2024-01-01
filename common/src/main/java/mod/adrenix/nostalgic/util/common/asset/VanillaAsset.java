package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.ResourceLocation;

public abstract class VanillaAsset
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

    /**
     * Get a new resource location from the game's sprite widgets texture directory.
     *
     * @param path A path in the game's {@code widgets} directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as
     * {@code minecraft:textures/gui/sprites/widget/$path} where {@code path} is the given {@code path} argument.
     */
    public static ResourceLocation widget(String path)
    {
        return new ResourceLocation("textures/gui/sprites/widget/" + path);
    }

    /**
     * Get a new resource location from the game's sprite heart texture directory.
     *
     * @param path A path in the game's {@code heart} directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as
     * {@code minecraft:textures/gui/sprites/hud/heart/$path} where {@code path} is the given {@code path} argument.
     */
    public static ResourceLocation heart(String path)
    {
        return new ResourceLocation("textures/gui/sprites/hud/heart/" + path);
    }
}
