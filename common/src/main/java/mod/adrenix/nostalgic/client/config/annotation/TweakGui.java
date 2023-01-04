package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.client.config.annotation.container.TweakCategory;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakEmbed;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakSubcategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * This annotation helper class is used exclusively by the client.
 * The server should not interface with this.
 */

public abstract class TweakGui
{
    /**
     * Adds a "New" tag to a tweak in the configuration menu.
     * These should be added to new tweaks during development and removed after release.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface New {}

    /**
     * The category a tweak is associated with.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category
    {
        TweakCategory container();
    }

    /**
     * The subcategory a tweak is associated with.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Subcategory
    {
        TweakSubcategory container();
    }

    /**
     * The embedded subcategory a tweak is associated with.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Embed
    {
        TweakEmbed container();
    }

    /**
     * This will prevent the tooltip bubble from appearing in the configuration menu.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NoTooltip {}

    /**
     * This annotation is intended for tweaks that are not meant to have automatic row generation within the config
     * screen's config row list.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotAutomated {}

    /**
     * This will prevent tweaks from getting their states toggled when a user disables/enables all
     * tweaks. This works for all tweak types.
     *
     * If this behavior is not desirable and a special value needs assigned when a tweak is disabled/enabled then see
     * the below special annotations.
     *
     * @see DisabledInteger
     * @see DisabledBoolean
     * @see DisabledString
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IgnoreDisable {}

    /**
     * Used to disable tweaks that hold an Integer value.
     * This is the implementation used for the disable/enable all tweaks feature.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DisabledInteger
    {
        int value();
    }

    /**
     * Used to disable tweaks that hold a Boolean value.
     * This is the implementation used for the disable/enable all tweaks feature.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DisabledBoolean
    {
        boolean value();
    }

    /**
     * Used to disable tweaks that hold a String value.
     * This is the implementation used for the disable/enable all tweaks feature.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DisabledString
    {
        String value();
    }

    /**
     * Adds a "Warning" tag to a tweak in the configuration menu.
     * These tags are designed to pop out to a user since the tweak may produce unexpected results.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Warning {}

    /**
     * Adds a "Restart" tag to a tweak in the configuration menu.
     * These tags inform the user that the game must be restarted for changes to take effect.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Restart {}

    /**
     * Adds an "Optifine" tag to a tweak in the configuration menu.
     * These tags inform the user that there are known problems with this tweak and Optifine.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Optifine
    {
        /**
         * Disable this flag if a custom tooltip is desired.
         * @return Whether to use the default tooltip message regarding incompatibility.
         */
        boolean incompatible() default true;
    }

    /**
     * Adds a "Sodium" tag to a tweak in the configuration menu.
     * These tags inform the user that there are known problems with this tweak and Sodium.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Sodium
    {
        /**
         * Disable this flag if a custom tooltip is desired.
         * @return Whether to use the default tooltip message regarding incompatibility.
         */
        boolean incompatible() default true;
    }

    /**
     * An enumeration that tells the config menu renderer where to order tweaks at.
     * The structure of ordering is typically done like so:
     * <ol>
     * <li> Top (orders tweaks based on their order number from least to greatest 0 -> infinity)</li>
     * <li> Middle (tweaks that don't have a placement annotation - ordered alphabetically)</li>
     * <li> Bottom (orders tweaks based on their order number from least to greatest 0 -> infinity)</li>
     * </ol>
     */
    public enum Position
    {
        TOP,
        BOTTOM
    }

    /**
     * Position a tweak in a specific spot within the configuration menu.
     * The position must be top or bottom, and the order number should be from 0 to infinity.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Placement
    {
        Position pos();
        int order();
    }

    /**
     * An enumeration that tells the config row renderer what type of slider it is rendering.
     * Each slider will be displayed in a different style based on the given Slider enum.
     */
    public enum SliderType
    {
        GENERIC,
        HEARTS,
        SWING,
        CLOUD,
        INTENSITY
    }

    /**
     * Tell the config renderer what type of slider to render.
     * The default is a swing speed styled slider.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Slider
    {
        SliderType type() default SliderType.SWING;
        String langKey() default "";
        String suffix() default "";
    }

    /**
     * Tell the config renderer to render an alert tag when the given conditions are met.
     * Dynamic display is handled by alert enumeration declarations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Alert
    {
        /**
         * An alert enumeration that contains the condition whether an alert tag should be displayed.
         */
        Condition condition();

        /**
         * A language file key reference which will be used when rendering the tooltip for an alert tag.
         */
        String langKey();
    }

    /**
     * Enumeration declarations that define conditions of when to display an alert tag.
     * Used in the client config for tweaks that need attention under certain conditions.
     */
    public enum Condition
    {
        /* Alerts */

        VOID_CONFLICT(AlertCondition::isVoidConflict),
        LIGHT_CONFLICT(AlertCondition::isLightConflict),
        SHIELD_CONFLICT(AlertCondition::isShieldConflict),
        DYNAMIC_FOG_CONFLICT(AlertCondition::isDynamicFogConflict),
        DYNAMIC_SKY_CONFLICT(AlertCondition::isDynamicSkyConflict),
        UNIVERSAL_FOG_CONFLICT(AlertCondition::isUniversalFogConflict),
        UNIVERSAL_SKY_CONFLICT(AlertCondition::isUniversalSkyConflict),
        CUSTOM_FOOD_HEALTH_CONFLICT(AlertCondition::isCustomFoodHealthConflict),
        CUSTOM_FOOD_STACKING_CONFLICT(AlertCondition::isCustomFoodStackingConflict);

        /* Fields */

        private final Supplier<Boolean> condition;

        /* Constructor */

        /**
         * Create a new tweak alert.
         * @param condition A supplier that indicates of the alert condition is met.
         */
        Condition(Supplier<Boolean> condition) { this.condition = condition; }

        /* Methods */

        /**
         * Check if the alert condition is met.
         * @return Whether the alert tag should be active.
         */
        public boolean active() { return this.condition.get(); }
    }
}
