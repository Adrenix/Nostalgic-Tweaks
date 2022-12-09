package mod.adrenix.nostalgic.common.config;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.list.ValidateList;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.function.TriFunction;
import mod.adrenix.nostalgic.util.common.log.LogColor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * This class provides common validation methods for ensuring values read from a config value will not cause issues for
 * the client or the server.
 */

public abstract class ValidateConfig
{
    /* Validation Fields */

    /**
     * Each tweak within the client's config file may have a placement annotation attached to it. These annotations
     * tell the config row renderer where to place certain rows within a container. This map tracks those containers
     * by using a key format CONTAINER_ENUM @ POSITION # ORDER where the container enum is some container enumeration
     * value, position is either top or bottom, and order is the order number relative to other placements. The values
     * associated with these keys are the field names tied to the annotation. If another placement annotation contains
     * the same order number within a container, then a runtime exceptions needs to be thrown since that tweak will
     * never appear in the container within the config row list. Duplicate placement order numbers is a developer error
     * so any issues will be caught by the placement validator during development.
     */
    private static final HashMap<String, String> TWEAK_PLACEMENT = new HashMap<>();

    /* Scanning Messages */

    private static final String NO_METADATA = "NO_METADATA";
    private static final String VALIDATE_FIELD = "SHOULD_NOT_APPEAR";
    private static final String LIST_VALIDATED = "LIST_VALIDATED";
    private static final String PASSED_VALIDATION = "PASSED_VALIDATION";

    /**
     * This enumeration indicates whether the validator should continue loading or stop and throw an error.
     * <ol>
     *   <li>A STOP result tells the validator something is irredeemably wrong and an error should stop the game.</li>
     *   <li>A VALIDATE result tells the validator that a config field can be validated even though it is not a
     *       primitive type. A common example is a config field that is an instanceof a String.</li>
     *   <li>A CONTINUE result tells the validator that validation passed or was successfully handled to where the
     *       config value was reset to an acceptable value.</li>
     * </ol>
     * Debugging information is displayed during validation for developers. If a value is reset, the user will be
     * notified of the invalid value and what it was reset to.
     */
    private enum Scan { CONTINUE, VALIDATE, STOP }

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

        validate(fields, config, TweakData.BoundedSlider.class, ValidateConfig::boundedSliders);
        validate(fields, config, TweakData.List.class, ValidateConfig::customLists);
        validate(fields, config, TweakData.Color.class, ValidateConfig::colorStrings);
        validate(fields, config, TweakGui.Placement.class, ValidateConfig::configPlacement);

        TWEAK_PLACEMENT.clear();
    }

    /* Annotation Validations */

    /**
     * Validate metadata that is attached to a config field annotation value.
     * @param fields An array of fields from a config class instance.
     * @param config A config container instance.
     * @param annotation The annotation type class.
     * @param validator A validation tri-function that accepts a field, the class instance containing the field, and an
     *                  annotation type class and returns a data pair. The data pair has a scan code value (first) and a
     *                  reason message for the continuation or stop (second).
     * @param <T> The type class of the annotation.
     * @throws ConfigData.ValidationException When invalid data cannot be reset and requires a panic exit.
     */
    private static <T extends Annotation> void validate
    (
        Field[] fields,
        Object config,
        Class<T> annotation,
        TriFunction<Field, Object, T, Pair<Scan, String>> validator
    )
    throws ConfigData.ValidationException
    {
        NostalgicTweaks.LOGGER.debug("VALIDATING: %s", annotation.getName());

        Pair<Scan, String> result = getValidation(fields, config, annotation, validator);

        Scan resultScan = result.getFirst();
        String resultMessage = result.getSecond();

        if (resultScan == Scan.STOP)
            throw new ConfigData.ValidationException(new Throwable(resultMessage));

        NostalgicTweaks.LOGGER.debug("VALIDATED: %s", annotation.getName());
    }

    /**
     * Validate a config file's bounded sliders.
     * @param field A field that has a bounded slider annotation attached to itself.
     * @param container The class this field is contained in.
     * @param slider Bounded slider metadata.
     * @return A data pair where the first value is a result value and the second is a message.
     * @throws IllegalAccessException When a field could not be set or retrieved.
     */
    private static Pair<Scan, String> boundedSliders(Field field, Object container, TweakData.BoundedSlider slider)
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
            String reason = "[%s %s]: reset is out-of-bounds (min: %s, max: %s, reset: %s)";
            String message = String.format(reason, container.toString(), name, min, max, reset);

            return new Pair<>(Scan.STOP, message);
        }

        if (value < min || value > max)
        {
            String warning = "%s is out-of-bounds (min: %s, max: %s) (had: %s) this tweak has been reset to (%s)";
            String message = String.format(warning, name, min, max, saved, reset);

            NostalgicTweaks.LOGGER.warn(message);

            if (saved instanceof Long)
                field.set(container, reset);
            else
                field.set(container, (int) reset);

            return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.YELLOW, message));
        }

        return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.GREEN, PASSED_VALIDATION));
    }

    /**
     * Validate a config file's color values.
     * @param field A field that has a color annotation attached to itself.
     * @param container The class this field is contained in.
     * @param color Color metadata.
     * @return A data pair where the first value is a result value and the second is a message.
     * @throws IllegalAccessException When a field could not be set or retrieved.
     */
    private static Pair<Scan, String> colorStrings(Field field, Object container, TweakData.Color color)
            throws IllegalAccessException
    {
        String name = field.getName();
        String saved = (String) field.get(container);
        String reset = color.reset();

        if (!TextUtil.isValidHexString(reset))
        {
            String reason = "[%s %s]: reset is an invalid hexadecimal (reset: %s)";
            String message = String.format(reason, container.toString(), name, reset);

            return new Pair<>(Scan.STOP, message);
        }

        if (TextUtil.isValidHexString(saved))
            return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.GREEN, PASSED_VALIDATION));
        else
        {
            String warning = "%s is an invalid hexadecimal (had: %s) this tweak has been reset to (%s)";
            String message = String.format(warning, name, saved, reset);

            NostalgicTweaks.LOGGER.warn(message);
            field.set(container, reset);

            return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.YELLOW, message));
        }
    }

    /**
     * Validate a config file's list data.
     * @param field A field that has a placement annotation attached to itself.
     * @param container The class this field is contained in.
     * @param list List metadata.
     * @return A data pair where the first value is a result value and the second is a message.
     * @throws IllegalAccessException When a field could not be set or retrieved.
     */
    private static Pair<Scan, String> customLists(Field field, Object container, TweakData.List list)
            throws IllegalAccessException
    {
        String fieldId = field.getName();

        if (ValidateList.scan(list.id(), field.get(container)))
            return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.GREEN, LIST_VALIDATED));
        else
        {
            String warning = "list (%s) was invalid so it was modified - please see config and backup config";
            String message = String.format(warning, fieldId);

            NostalgicTweaks.LOGGER.warn(message);
            NostalgicTweaks.LOGGER.debug("(%s@%s) has invalid list data", container, fieldId);

            return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.YELLOW, message));
        }
    }

    /**
     * Validate a client config file's user interface placement data.
     * @param field A field that has a placement annotation attached to itself.
     * @param container The class this field is contained in.
     * @param placement Placement metadata.
     * @return A data pair where the first value is a result value and the second is a message.
     */
    private static Pair<Scan, String> configPlacement(Field field, Object container, TweakGui.Placement placement)
    {
        String fieldId = field.getName();
        TweakGui.Category category = field.getAnnotation(TweakGui.Category.class);
        TweakGui.Subcategory subcategory = field.getAnnotation(TweakGui.Subcategory.class);
        TweakGui.Embed embed = field.getAnnotation(TweakGui.Embed.class);

        if (category == null && subcategory == null && embed == null)
            return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.YELLOW, "NO_CONTAINER"));

        if (category != null)
            return scanContainer(category.container(), placement, fieldId);
        else if (subcategory != null)
            return scanContainer(subcategory.container(), placement, fieldId);
        else
            return scanContainer(embed.container(), placement, fieldId);
    }

    /**
     * Checks if placement data already exists at the position used by the annotation.
     * @param container A category, subcategory, or embed enumeration value.
     * @param placement The placement annotation data.
     * @param fieldId The name of the field associated with this container.
     * @return A data pair where the first value is a result value and the second is a message.
     */
    private static Pair<Scan, String> scanContainer(Enum<?> container, TweakGui.Placement placement, String fieldId)
    {
        String key = String.format("%s@%s#%s", container, placement.pos(), placement.order());

        if (TWEAK_PLACEMENT.containsKey(key) && !TWEAK_PLACEMENT.containsValue(fieldId))
        {
            String holder = TWEAK_PLACEMENT.get(key);
            String warning = "group (%s@%s) already has a tweak (%s) with an order # of %s";
            String message = String.format(warning, container, placement.pos(), holder, placement.order());

            return new Pair<>(Scan.STOP, LogColor.apply(LogColor.RED, message));
        }
        else
            TWEAK_PLACEMENT.put(key, fieldId);

        return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.GREEN, PASSED_VALIDATION));
    }

    /* Validation Methods */

    /**
     * Scan through the list of provided fields and validate any annotation metadata that may be attached to the field.
     * This method handles subclass nesting. If validation was successful, then a data pair will be returned where the
     * first result is <code>true</code> and the second result is an <code>empty</code> string.
     *
     * @param fields A list fields to scan and validate.
     * @param annotation The annotation to check if it is attached.
     * @param validator A validation tri-function that accepts a field, the class instance containing the field, and an
     *                  annotation type class and returns a data pair. The data pair has a scan code value (first) and a
     *                  reason message for the continuation or stop (second).
     * @param <T> The type class of the annotation.
     * @return A data pair with a result boolean and failure message if applicable.
     */
    private static <T extends Annotation> Pair<Scan, String> getValidation
    (
        Field[] fields,
        Object container,
        Class<T> annotation,
        TriFunction<Field, Object, T, Pair<Scan, String>> validator
    )
    {
        if (fields[0] != null && fields[0].getDeclaringClass() == String.class)
            return new Pair<>(Scan.VALIDATE, LogColor.apply(LogColor.RED, VALIDATE_FIELD));

        for (Field field : fields)
        {
            Field[] typeFields = field.getType().getFields();
            Pair<Scan, String> result;

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

                if (result.getFirst() == Scan.VALIDATE)
                    result = getAnnotationAndValidate(field, container, annotation, validator);
            }
            else
                result = getAnnotationAndValidate(field, container, annotation, validator);

            final Scan LOAD_STATE = result.getFirst();
            final String FIELD = LogColor.apply(LogColor.GOLD, field.getName());
            final String RESULT = switch (LOAD_STATE)
            {
                case STOP -> LogColor.apply(LogColor.RED, LOAD_STATE.toString());
                case VALIDATE -> LogColor.apply(LogColor.DARK_RED, LOAD_STATE.toString());
                case CONTINUE -> LogColor.apply(LogColor.GREEN, LOAD_STATE.toString());
            };

            NostalgicTweaks.LOGGER.debug("Field: %s | Result: %s | Message: %s", FIELD, RESULT, result.getSecond());

            if (result.getFirst() == Scan.STOP)
                return result;
        }

        final String CHECKED = LogColor.apply(LogColor.GOLD, container.getClass().getSimpleName());
        final String ANNOTATION = LogColor.apply(LogColor.AQUA, annotation.getSimpleName());
        final String MESSAGE = String.format("CHECKED %s FOR %s", CHECKED, ANNOTATION);

        return new Pair<>(Scan.CONTINUE, MESSAGE);
    }

    /**
     * Get the annotation of a field and validate the metadata.
     * @param field The field to scan.
     * @param annotation The annotation class to try and get.
     * @param validator A validation tri-function that accepts a field, the class instance containing the field, and an
     *                  annotation type class and returns a data pair. The data pair has a scan code value (first) and a
     *                  reason message for the continuation or stop (second).
     * @param <T> The type class of the annotation.
     * @return The data pair from the validation tri-function.
     */
    private static <T extends Annotation> Pair<Scan, String> getAnnotationAndValidate
    (
        Field field,
        Object container,
        Class<T> annotation,
        TriFunction<Field, Object, T, Pair<Scan, String>> validator
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

        return new Pair<>(Scan.CONTINUE, LogColor.apply(LogColor.RED, NO_METADATA));
    }
}
