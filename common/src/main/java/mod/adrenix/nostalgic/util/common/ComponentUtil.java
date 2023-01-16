package mod.adrenix.nostalgic.util.common;

import net.minecraft.network.chat.*;

/**
 * This utility can be used by both the client and server.
 * Provides shortcuts when styling text components.
 */

public abstract class ComponentUtil
{
    /**
     * Color text with a custom color.
     * @param text The text to color.
     * @param color The custom color.
     * @return A mutable component with colored text.
     */
    public static MutableComponent color(String text, int color)
    {
        return ComponentBackport.literal(text).withStyle(style -> style.withColor(color));
    }
}
