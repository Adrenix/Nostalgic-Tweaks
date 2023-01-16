package mod.adrenix.nostalgic.util.common;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Helper class that reduces the amount of code rewrite when back-porting 1.19+ translatable component changes to
 * 1.18.2.
 */

public abstract class ComponentBackport
{
    /**
     * Get a translation from a language file.
     * @param key A lang key.
     * @param args Optional arguments defined in the language file.
     * @return A new translatable component instance.
     */
    public static MutableComponent translatable(String key, Object... args)
    {
        return new TranslatableComponent(key, args);
    }

    /**
     * Get a mutable component instance from a literal string of text.
     * @param text The literal text.
     * @return A new text component instance.
     */
    public static MutableComponent literal(String text) { return new TextComponent(text); }

    /**
     * Get a mutable component with an empty literal string.
     * @return A new empty text component instance.
     */
    public static MutableComponent empty() { return new TextComponent(""); }
}
