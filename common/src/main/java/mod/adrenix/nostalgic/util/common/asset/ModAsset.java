package mod.adrenix.nostalgic.util.common.asset;

import com.google.common.hash.Hashing;
import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.resources.ResourceLocation;

public abstract class ModAsset
{
    /**
     * Get a new resource location from the mod's assets directory.
     *
     * @param path A path in the mod's assets directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as {@code modId:$path} where
     * {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation get(String path)
    {
        return new ResourceLocation(NostalgicTweaks.MOD_ID + ":" + path);
    }

    /**
     * Get a new resource location from the mod's textures directory.
     *
     * @param path A path in the mod's {@code textures} directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as
     * {@code modId:textures/$path} where {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation texture(String path)
    {
        return get("textures/" + path);
    }

    /**
     * Get a new resource location from the mod's icon directory.
     *
     * @param path A path in the mod's {@code icon} directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as
     * {@code modId:textures/icon/$path} where {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation icon(String path)
    {
        return get("textures/icon/" + path);
    }

    /**
     * Get a new resource location from the mod's twemoji directory.
     *
     * @param path A path in the mod's {@code twemoji} directory.
     * @return A new {@link ResourceLocation} location instance where the full-path appears as
     * {@code modId:textures/icon/twemoji/$path} where {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation twemoji(String path)
    {
        return get("textures/icon/twemoji/" + path);
    }

    /**
     * Get a new resource location that references a supporter face icon.
     *
     * @param username The username to lookup.
     * @return A new {@link ResourceLocation}.
     */
    @SuppressWarnings({ "deprecation" })
    public static ResourceLocation supporter(String username)
    {
        return get("supporter_face/" + Hashing.sha1().hashUnencodedChars(username));
    }
}
