package mod.adrenix.nostalgic.common.config;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.util.common.function.TriFunction;
import mod.adrenix.nostalgic.util.common.log.LogColor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * This class provides common validation methods for ensuring values read from a config value will not cause issues for
 * the client or the server.
 */

public abstract class ValidateConfig
{
    /* Messages */

    private static final String NO_METADATA = "NO_METADATA";
    private static final String SKIP_STRING = "SKIP_STRING_CONTAINER";
    private static final String PASSED_VALIDATION = "PASSED_VALIDATION";

    /**
     * This enumeration indicates whether the mod should continue loading or stop and throw an error.
     * <ol>
     *   <li>A STOP result tells the mod something is irredeemably wrong and an error should stop the game.</li>
     *   <li>A CONTINUE result tells the mod that validation passed or was successfully handled to where the config
     *       value was reset to an acceptable value.</li>
     * </ol>
     * Debugging information is displayed during validation for developers. If a value is reset, the user will be
     * notified of the invalid value and what it was reset to.
     */
    private enum Loader { CONTINUE, STOP }

    /**
     * Scan a client or server config instance. This method will validate metadata to ensure that all field data is
     * correct according to defined standards. Any invalid data will be reset or throw an exception as needed.
     *
     * @param config A client or server config instance.
     * @throws ConfigData.ValidationException When invalid data cannot be reset and requires a panic exit.
     */
    public static void scan(Object config) throws ConfigData.ValidationException
    {
        Field[] fields = config.getClass().getFields();
        validateAnnotation(fields, config, TweakData.BoundedSlider.class, ValidateConfig::validateBoundedSliders);
    }

    /* Annotation Validations */

    /**
     *
     * @param fields An array of fields from a config class instance.
     * @param config A config container instance.
     * @param annotation The annotation type class.
     * @param validator A validation bi-function that accepts a field and annotation type class and returns a data pair.
     *                  The data pair has a CONTINUE/STOP value and a loader message.
     * @param <T> The type class of the annotation.
     * @throws ConfigData.ValidationException When invalid data cannot be reset and requires a panic exit.
     */
    @SuppressWarnings("SameParameterValue") // Temporary until additional metadata validation is created
    private static <T extends Annotation> void validateAnnotation
    (
        Field[] fields,
        Object config,
        Class<T> annotation,
        TriFunction<Field, Object, T, Pair<Loader, String>> validator
    )
    throws ConfigData.ValidationException
    {
        NostalgicTweaks.LOGGER.debug("START_VALIDATING: %s", annotation.getName());

        Pair<Loader, String> result = getValidation(fields, config, annotation, validator);

        Loader resultLoader = result.getFirst();
        String resultMessage = result.getSecond();

        if (resultLoader == Loader.STOP)
            throw new ConfigData.ValidationException(new Throwable(resultMessage));

        NostalgicTweaks.LOGGER.debug("FINISH_VALIDATING: %s", annotation.getName());
    }

    /**
     * Validate a config file's bounded sliders.
     * @param field A field that has a bounded slider annotation attached to itself.
     * @param container The class this field is contained in.
     * @param slider Bounded slider metadata.
     * @return A data pair where the first value is a result value and the second is a message.
     * @throws IllegalAccessException When a field could not be set or retrieved.
     */
    private static Pair<Loader, String> validateBoundedSliders(Field field, Object container, TweakData.BoundedSlider slider)
            throws IllegalAccessException
    {
        String name = field.getName();
        Object saved = field.get(container); // This can be defined as an int or a long

        long min = slider.min();
        long max = slider.max();
        long reset = slider.reset();
        long value = ((Number) saved).longValue();

        if (reset < min || reset > max)
        {
            String body = "[%s %s]: reset is out-of-bounds (min: %s, max: %s, reset: %s)";
            String reason = String.format(body, container.toString(), name, min, max, reset);

            return new Pair<>(Loader.STOP, reason);
        }

        if (value < min || value > max)
        {
            String warning = "%s is out-of-bounds (min: %s, max: %s) (current: %s) it has been reset to (%s)";
            String message = String.format(warning, name, min, max, saved, reset);

            NostalgicTweaks.LOGGER.warn(message);

            if (saved instanceof Long)
                field.set(container, reset);
            else
                field.set(container, (int) reset);

            return new Pair<>(Loader.CONTINUE, LogColor.apply(LogColor.YELLOW, message));
        }

        return new Pair<>(Loader.CONTINUE, LogColor.apply(LogColor.GREEN, PASSED_VALIDATION));
    }

    /* Validation Methods */

    /**
     * Scan through the list of provided fields and validate any annotation metadata that may be attached to the field.
     * This method handles subclass nesting. If validation was successful, then a data pair will be returned where the
     * first result is <code>true</code> and the second result is an <code>empty</code> string.
     *
     * @param fields A list fields to scan and validate.
     * @param annotation The annotation to check if it is attached.
     * @param validator A validation bi-function that accepts a field and annotation type class and returns a data pair.
     *                  The data pair has a CONTINUE/STOP value and a loader message.
     * @param <T> The type class of the annotation.
     * @return A data pair with a result boolean and failure message if applicable.
     */
    private static <T extends Annotation> Pair<Loader, String> getValidation
    (
        Field[] fields,
        Object container,
        Class<T> annotation,
        TriFunction<Field, Object, T, Pair<Loader, String>> validator
    )
    {
        if (fields[0] != null && fields[0].getDeclaringClass() == String.class)
            return new Pair<>(Loader.CONTINUE, LogColor.apply(LogColor.LIGHT_PURPLE, SKIP_STRING));

        for (Field field : fields)
        {
            Field[] typeFields = field.getType().getFields();
            Pair<Loader, String> result;

            if (typeFields.length > 0 && !field.isSynthetic() && !field.isEnumConstant())
            {
                try
                {
                    result = getValidation(typeFields, field.get(container), annotation, validator);
                }
                catch (IllegalAccessException exception)
                {
                    throw new RuntimeException(exception);
                }
            }
            else
                result = getAnnotationAndValidate(field, container, annotation, validator);

            final Loader LOAD_STATE = result.getFirst();
            final String FIELD = LogColor.apply(LogColor.GOLD, field.getName());
            final String RESULT = switch (LOAD_STATE)
            {
                case STOP -> LogColor.apply(LogColor.RED, LOAD_STATE.toString());
                case CONTINUE -> LogColor.apply(LogColor.GREEN, LOAD_STATE.toString());
            };

            NostalgicTweaks.LOGGER.debug("Field: %s | Result: %s | Message: %s", FIELD, RESULT, result.getSecond());

            if (result.getFirst() == Loader.STOP)
                return result;
        }

        final String CHECKED = LogColor.apply(LogColor.GOLD, container.getClass().getSimpleName());
        final String ANNOTATION = LogColor.apply(LogColor.AQUA, annotation.getSimpleName());
        final String MESSAGE = String.format("CHECKED %s FOR %s", CHECKED, ANNOTATION);

        return new Pair<>(Loader.CONTINUE, MESSAGE);
    }

    /**
     * Get the annotation of a field and validate the metadata.
     * @param field The field to scan.
     * @param annotation The annotation class to try and get.
     * @param validator A validation bi-function that accepts a field and annotation type class and returns a data pair.
     *                  The data pair has a CONTINUE/STOP value and a loader message.
     * @param <T> The type class of the annotation.
     * @return The data pair from the validation bi-function.
     */
    private static <T extends Annotation> Pair<Loader, String> getAnnotationAndValidate
    (
        Field field,
        Object container,
        Class<T> annotation,
        TriFunction<Field, Object, T, Pair<Loader, String>> validator
    )
    {
        T data = field.getAnnotation(annotation);

        if (data != null)
        {
            try
            {
                return validator.apply(field, container, data);
            }
            catch (IllegalAccessException exception)
            {
                throw new RuntimeException(exception);
            }
        }

        return new Pair<>(Loader.CONTINUE, LogColor.apply(LogColor.RED, NO_METADATA));
    }
}
