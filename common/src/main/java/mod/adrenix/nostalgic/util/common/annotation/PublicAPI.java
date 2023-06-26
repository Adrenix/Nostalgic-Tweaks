package mod.adrenix.nostalgic.util.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Removes the "unused" warning for target element types that are not used elsewhere in production code. This annotation
 * may be applied if the target element type is part of an API, will be used at a later time, or serves as a utility
 * purpose that may or may not assist production code at a future point in time.
 */

@Target({ TYPE, METHOD, CONSTRUCTOR, FIELD, PACKAGE })
@Retention(RetentionPolicy.SOURCE)
public @interface PublicAPI { }
