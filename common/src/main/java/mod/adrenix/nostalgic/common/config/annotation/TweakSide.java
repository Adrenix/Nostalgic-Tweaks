package mod.adrenix.nostalgic.common.config.annotation;

import mod.adrenix.nostalgic.common.config.reflect.StatusType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation class is utilized by both the server and client.
 * Do not import any client code.
 */

public abstract class TweakSide
{
    /**
     * Instructs the mod that this tweak is controlled by the client.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Client {}

    /**
     * Instructs the mod that this tweak is controlled by the server.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Server {}

    /**
     * Instructs the mod that this tweak is controlled by both the client and server.
     * Only servers with Nostalgic Tweaks installed will override the tweak.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Dynamic {}

    /**
     * Instructs the mod that this configuration entry is not a tweak.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Ignore {}

    /**
     * Tells the mod what state a tweak is currently in.
     * @see StatusType
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface EntryStatus
    {
        StatusType status() default StatusType.WAIT;
    }
}
