package mod.adrenix.nostalgic.client.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations that instruct the mod to reload game data.
 */

public abstract class TweakReload
{
    /**
     * Reloads all rendered chunks.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Chunks {}

    /**
     * Reload the game's data resources.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Resources {}
}
