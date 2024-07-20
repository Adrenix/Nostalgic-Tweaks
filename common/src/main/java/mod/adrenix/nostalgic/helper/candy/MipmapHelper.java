package mod.adrenix.nostalgic.helper.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import net.minecraft.client.Minecraft;

/**
 * This utility class is used only by the client.
 */
public abstract class MipmapHelper
{
    /* Fields */

    /**
     * Caches the value stored by the remove mipmap cache tweak so that the game can be notified that the mipmap needs
     * to reset due to a change in this tweak.
     */
    private static final CacheValue<Boolean> REMOVE_MIPMAP_CACHE = CacheValue.create(CandyTweak.REMOVE_MIPMAP_TEXTURE::get);

    /* Methods */

    /**
     * Initializes the mipmap cache with the value saved in the mod's config.
     */
    public static void init()
    {
        REMOVE_MIPMAP_CACHE.update();
    }

    /**
     * Instructions to perform after the config has been saved.
     */
    public static void runAfterSave()
    {
        if (REMOVE_MIPMAP_CACHE.isExpired())
        {
            Minecraft.getInstance().updateMaxMipLevel(Minecraft.getInstance().options.mipmapLevels().get());
            REMOVE_MIPMAP_CACHE.update();
        }
    }
}
