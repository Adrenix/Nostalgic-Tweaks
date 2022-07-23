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
         * The category a tweak is associated with.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Cat
        {
            Category group();
        }

        /**
         * The subcategory a tweak is associated with.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Sub
        {
            Subcategory group();
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
     * This enumeration creates categories for group types.
     */

    public enum Category
    {
        // Sound Categories
        BLOCK_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_BLOCK),
        DAMAGE_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_DAMAGE),
        EXPERIENCE_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_EXPERIENCE),
        MOB_SOUND(GroupType.SOUND, NostalgicLang.Gui.SOUND_CATEGORY_MOB),

        // Candy Categories
        BLOCK_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_BLOCK),
        INTERFACE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_GUI),
        ITEM_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_ITEM),
        LIGHTING_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_LIGHTING),
        PARTICLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_PARTICLE),
        TITLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_TITLE),
        WORLD_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_WORLD),

        // Gameplay Categories
        COMBAT_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_COMBAT),
        EXPERIENCE_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_EXPERIENCE),
        MECHANICS_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_MECHANICS),
        HUNGER_GAMEPLAY(GroupType.GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_CATEGORY_HUNGER),

        // Animation Categories
        ARM_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_ARM),
        ITEM_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_ITEM),
        MOB_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_MOB),
        PLAYER_ANIMATION(GroupType.ANIMATION, NostalgicLang.Gui.ANIMATION_CATEGORY_PLAYER),

        // Swing Categories
        ITEM_SWING(GroupType.SWING, NostalgicLang.Gui.SWING_CATEGORY_ITEM),
        POTION_SWING(GroupType.SWING, NostalgicLang.Gui.SWING_CATEGORY_POTION);

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

    /**
     * This enumeration creates subcategories for categories of group types.
     */

    public enum Subcategory
    {
        /**
         * IMPORTANT:
         *
         * To prevent issues, subcategory names should not match any category names.
         * There will be GUI rendering issues if this occurs.
         *
         * The best way to prevent this is to invert the naming scheme for subcategories.
         * For example, ITEM_CANDY is already a category name. The inverse would be CANDY_ITEM.
         */

        /* Sound Candy */

        // Block Sound Subcategories

        SOUND_CHEST(Category.BLOCK_SOUND, NostalgicLang.Gui.SOUND_SUBCATEGORY_CHEST),

        /* Eye Candy */

        // Block Candy Subcategories

        CANDY_CHEST(Category.BLOCK_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_CHEST),

        // Interface Candy Subcategories

        CANDY_CHAT(Category.INTERFACE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_CHAT),
        CANDY_ITEM(Category.INTERFACE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_ITEM),
        CANDY_LOADING(Category.INTERFACE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_LOADING),
        CANDY_PAUSE(Category.INTERFACE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_PAUSE),
        CANDY_OVERLAY(Category.INTERFACE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_OVERLAY),
        CANDY_TOOLTIP(Category.INTERFACE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_TOOLTIP),

        // Item Candy Subcategories

        CANDY_FLAT(Category.ITEM_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_FLAT),

        // Lighting Candy Subcategories

        CANDY_LIGHT_BLOCK(Category.LIGHTING_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_BLOCK_LIGHT),
        CANDY_LIGHT_WORLD(Category.LIGHTING_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_WORLD_LIGHT),

        // Particle Candy Subcategories

        CANDY_ATTACK(Category.PARTICLE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_ATTACK),
        CANDY_EXPLOSION(Category.PARTICLE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_EXPLOSION),

        // Title Screen Candy Subcategories

        CANDY_BUTTON(Category.TITLE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_BUTTON),
        CANDY_LOGO(Category.TITLE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_LOGO),
        CANDY_TEXT(Category.TITLE_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_TEXT),

        // World Candy Subcategories

        CANDY_FOG(Category.WORLD_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_FOG),
        CANDY_SKY(Category.WORLD_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_SKY),
        CANDY_VOID(Category.WORLD_CANDY, NostalgicLang.Gui.CANDY_SUBCATEGORY_VOID),

        /* Gameplay */

        // Combat System Subcategories

        GAMEPLAY_BOW(Category.COMBAT_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_BOW),

        // Experience System Subcategories

        GAMEPLAY_EXPERIENCE_BAR(Category.EXPERIENCE_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_BAR),
        GAMEPLAY_EXPERIENCE_ORB(Category.EXPERIENCE_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_ORB),
        GAMEPLAY_EXPERIENCE_BLOCK(Category.EXPERIENCE_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_BLOCK),

        // Game Mechanics Subcategories

        GAMEPLAY_FIRE(Category.MECHANICS_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_FIRE),
        GAMEPLAY_SWIMMING(Category.MECHANICS_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_SWIMMING),

        // Hunger System Subcategories

        GAMEPLAY_HUNGER_BAR(Category.HUNGER_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_HUNGER_BAR),
        GAMEPLAY_HUNGER_FOOD(Category.HUNGER_GAMEPLAY, NostalgicLang.Gui.GAMEPLAY_SUBCATEGORY_FOOD);

        private final String langKey;
        private final Category category;

        Subcategory(Category category, String langKey)
        {
            this.category = category;
            this.langKey = langKey;
        }

        public Category getCategory() { return this.category; }
        public String getLangKey() { return this.langKey; }
    }
}
