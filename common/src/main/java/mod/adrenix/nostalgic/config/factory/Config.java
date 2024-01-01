package mod.adrenix.nostalgic.config.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config
{
    /**
     * This is the filename of the config. It will be used when saving a new config json file to disk in the mod
     * loader's config folder.
     *
     * @return The filename of the config this annotation is attached to.
     */
    String filename();
}
