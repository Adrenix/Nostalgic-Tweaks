package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.util.NostalgicLang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation class is used exclusively by the client.
 * The server should not interface with this.
 */

public abstract class TweakClient
{
    /**
     * Gui related annotations.
     */

    public abstract static class Gui
    {
        /**
         * The group a tweak is associated with.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Sub
        {
            Category group();
        }

        /**
         * This will prevent the tooltip bubble from appearing in the configuration menu.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface NoTooltip {}

        /**
         * This will prevent tweaks from getting their states toggled when a user disables/enables all
         * tweaks. This works for all tweak types. For explicit default states see below.
         *
         * @see DisabledInteger
         * @see DisabledBoolean
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface IgnoreDisable {}

        /**
         * Used to disable tweaks that hold an Integer value.
         * This is the implementation used for the disable/enable all tweaks feature.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface DisabledInteger
        {
            int disabled();
        }

        /**
         * Used to disable tweaks that hold a Boolean value.
         * This is the implementation used for the disable/enable all tweaks feature.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface DisabledBoolean
        {
            boolean disabled();
        }

        /**
         * Adds a "New" tag to a tweak in the configuration menu.
         * These should be added to new tweaks during development and removed after release.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface New {}

        /**
         * Adds a "Warning" tag to a tweak in the configuration menu.
         * These tags are designed to pop out to a user since the tweak may produce unexpected results.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Warning {}

        /**
         * Adds a "Restart" tag to a tweak in the configuration menu.
         * These tags inform the user that the game must be restarted for changes to take effect.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Restart {}

        /**
         * An enumeration that tells the config menu renderer where to order tweaks at.
         * The structure of ordering is typically done like so:
         * Top (orders tweaks based on their order number from least to greatest 0 -> infinity)
         * Middle (tweaks that don't have a placement annotation - ordered alphabetically)
         * Bottom (orders tweaks based on their order number from least to greatest 0 -> infinity)
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
        @Target({ElementType.FIELD})
        public @interface Placement
        {
            Position pos();
            int order();
        }

        /**
         * An enumeration that tells the config row renderer what type of slider it is rendering.
         * Each slider will be displayed in a different style based on the given Slider enum.
         */
        public enum Slider
        {
            SWING_SLIDER,
            CLOUD_SLIDER,
            INTENSITY_SLIDER
        }

        /**
         * Tell the config renderer what type of slider to render.
         * The default is a swing speed styled slider.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface SliderType
        {
            Slider slider() default Slider.SWING_SLIDER;
        }
    }

    /**
     * Annotations that instruct the mod to run client code.
     */

    public abstract static class Run
    {
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface ReloadChunks {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface ReloadResources {}
    }

    /**
     * The category enumeration stores subcategories of group types.
     * There is currently no implementation available for subcategories of subcategories.
     */

    public enum Category
    {
        // Sound Subcategories
        BLOCK_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_BLOCK),
        DAMAGE_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_DAMAGE),
        EXPERIENCE_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_EXPERIENCE),
        MOB_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_MOB),

        // Candy Subcategories
        BLOCK_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_BLOCK),
        INTERFACE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_GUI),
        ITEM_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_ITEM),
        LIGHTING_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_LIGHTING),
        PARTICLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_PARTICLE),
        TITLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_TITLE),
        WORLD_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_WORLD),

        // Gameplay Subcategories
        COMBAT_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_COMBAT),
        EXPERIENCE_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_EXPERIENCE),
        MECHANICS_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_MECHANICS),
        HUNGER_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_HUNGER),

        // Animation Subcategories
        ARM_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_ARM),
        ITEM_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_ITEM),
        MOB_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_MOB),
        PLAYER_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_PLAYER);

        private final String langKey;
        private final GroupType groupType;

        Category(GroupType groupType, String langKey)
        {
            this.groupType = groupType;
            this.langKey = langKey;
        }

        public GroupType getGroup() { return this.groupType; }
        public String getLangKey() { return this.langKey; }
    }
}
