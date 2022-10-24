package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.util.common.LangUtil;

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
         * The embedded subcategory a tweak is associated with.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Emb
        {
            Embedded group();
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
            int value();
        }

        /**
         * Used to disable tweaks that hold a Boolean value.
         * This is the implementation used for the disable/enable all tweaks feature.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface DisabledBoolean
        {
            boolean value();
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
         * Adds an "Optifine" tag to a tweak in the configuration menu.
         * These tags inform the user that there are known problems with this tweak and Optifine.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
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
        @Target({ElementType.FIELD})
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
            GENERIC,
            SWING,
            CLOUD,
            INTENSITY
        }

        /**
         * Tell the config renderer what type of slider to render.
         * The default is a swing speed styled slider.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface SliderType
        {
            Slider slider() default Slider.SWING;
            String langKey() default "";
            String suffix() default "";
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
        BLOCK_SOUND(GroupType.SOUND, LangUtil.Gui.SOUND_CATEGORY_BLOCK),
        DAMAGE_SOUND(GroupType.SOUND, LangUtil.Gui.SOUND_CATEGORY_DAMAGE),
        EXPERIENCE_SOUND(GroupType.SOUND, LangUtil.Gui.SOUND_CATEGORY_EXPERIENCE),
        MOB_SOUND(GroupType.SOUND, LangUtil.Gui.SOUND_CATEGORY_MOB),

        // Candy Categories
        BLOCK_CANDY(GroupType.CANDY, LangUtil.Gui.CANDY_CATEGORY_BLOCK),
        INTERFACE_CANDY(GroupType.CANDY, LangUtil.Gui.CANDY_CATEGORY_GUI),
        ITEM_CANDY(GroupType.CANDY, LangUtil.Gui.CANDY_CATEGORY_ITEM),
        LIGHTING_CANDY(GroupType.CANDY, LangUtil.Gui.CANDY_CATEGORY_LIGHTING),
        PARTICLE_CANDY(GroupType.CANDY, LangUtil.Gui.CANDY_CATEGORY_PARTICLE),
        WORLD_CANDY(GroupType.CANDY, LangUtil.Gui.CANDY_CATEGORY_WORLD),

        // Gameplay Categories
        BUG_GAMEPLAY(GroupType.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_BUG),
        COMBAT_GAMEPLAY(GroupType.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_COMBAT),
        EXPERIENCE_GAMEPLAY(GroupType.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_EXPERIENCE),
        MECHANICS_GAMEPLAY(GroupType.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_MECHANICS),
        HUNGER_GAMEPLAY(GroupType.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_HUNGER),
        MOB_GAMEPLAY(GroupType.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_MOB),

        // Animation Categories
        ARM_ANIMATION(GroupType.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_ARM),
        ITEM_ANIMATION(GroupType.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_ITEM),
        MOB_ANIMATION(GroupType.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_MOB),
        PLAYER_ANIMATION(GroupType.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_PLAYER),

        // Swing Categories
        ITEM_SWING(GroupType.SWING, LangUtil.Gui.SWING_CATEGORY_ITEM),
        POTION_SWING(GroupType.SWING, LangUtil.Gui.SWING_CATEGORY_POTION);

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
         * The best way to prevent this is to prefix the category name to the subcategory name.
         */

        /* Sound Candy */

        // Block Sound Subcategories

        BLOCK_CHEST_SOUND(Category.BLOCK_SOUND, LangUtil.Gui.SOUND_SUBCATEGORY_CHEST),

        /* Eye Candy */

        // Block Candy Subcategories

        BLOCK_CHEST_CANDY(Category.BLOCK_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_CHEST),
        BLOCK_TORCH_CANDY(Category.BLOCK_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_TORCH),

        // Interface Candy Subcategories

        INTERFACE_GUI_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_GUI),
        INTERFACE_CHAT_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_CHAT),
        INTERFACE_CRAFTING_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_CRAFTING),
        INTERFACE_DEBUG_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_DEBUG),
        INTERFACE_FURNACE_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_FURNACE),
        INTERFACE_INVENTORY_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_INVENTORY),
        INTERFACE_LOADING_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_LOADING),
        INTERFACE_PAUSE_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_PAUSE),
        INTERFACE_TITLE_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_TITLE),
        INTERFACE_TOOLTIP_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_TOOLTIP),
        INTERFACE_VERSION_CANDY(Category.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_VERSION),

        // Item Candy Subcategories

        FLAT_ITEM_CANDY(Category.ITEM_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_FLAT),
        DISPLAY_ITEM_CANDY(Category.ITEM_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_ITEM),
        MERGING_ITEM_CANDY(Category.ITEM_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_MERGE),

        // Lighting Candy Subcategories

        LIGHTING_BLOCK_CANDY(Category.LIGHTING_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_BLOCK_LIGHT),
        LIGHTING_WORLD_CANDY(Category.LIGHTING_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_WORLD_LIGHT),

        // Particle Candy Subcategories

        PARTICLE_ATTACK_CANDY(Category.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_ATTACK),
        PARTICLE_BLOCK_CANDY(Category.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_BLOCK_PARTICLES),
        PARTICLE_EXPLOSION_CANDY(Category.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_EXPLOSION),
        PARTICLE_PLAYER_CANDY(Category.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_PLAYER),

        // World Candy Subcategories

        WORLD_FOG_CANDY(Category.WORLD_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_FOG),
        WORLD_SKY_CANDY(Category.WORLD_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_SKY),
        WORLD_VOID_CANDY(Category.WORLD_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_VOID),

        /* Gameplay */

        // Combat System Subcategories

        COMBAT_BOW_GAMEPLAY(Category.COMBAT_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_BOW),

        // Experience System Subcategories

        EXPERIENCE_BAR_GAMEPLAY(Category.EXPERIENCE_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_BAR),
        EXPERIENCE_ORB_GAMEPLAY(Category.EXPERIENCE_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_ORB),
        EXPERIENCE_BLOCK_GAMEPLAY(Category.EXPERIENCE_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_BLOCK),

        // Game Mechanics Subcategories

        MECHANICS_FIRE_GAMEPLAY(Category.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_FIRE),
        MECHANICS_PLAYER_GAMEPLAY(Category.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_PLAYER),
        MECHANICS_FARMING_GAMEPLAY(Category.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_FARMING),
        MECHANICS_SWIMMING_GAMEPLAY(Category.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_SWIMMING),

        // Hunger System Subcategories

        HUNGER_BAR_GAMEPLAY(Category.HUNGER_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_HUNGER_BAR),
        HUNGER_FOOD_GAMEPLAY(Category.HUNGER_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_FOOD);

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

    /**
     * This enumeration creates a sub-subcategory for a category.
     */
    public enum Embedded
    {
        /**
         * IMPORTANT:
         *
         * To prevent issues, embedded subcategory names should not match any category or subcategory names.
         * There will be GUI rendering issues if this occurs.
         *
         * The best way to prevent this is to prefix the naming scheme for embedded subcategories.
         * For example, for the button tweaks for title candy subcategory, the name would be: CANDY_TITLE_BUTTONS.
         */

        // Title Screen (Eye Candy) - Embedded Subcategories

        TITLE_BUTTON_CANDY(Subcategory.INTERFACE_TITLE_CANDY, LangUtil.Gui.CANDY_EMBED_TITLE_BUTTON),
        TITLE_LOGO_CANDY(Subcategory.INTERFACE_TITLE_CANDY, LangUtil.Gui.CANDY_EMBED_TITLE_LOGO),
        TITLE_TEXT_CANDY(Subcategory.INTERFACE_TITLE_CANDY, LangUtil.Gui.CANDY_EMBED_TITLE_TEXT),

        // Interface Tooltips (Eye Candy) - Embedded Subcategories

        TOOLTIP_PARTS_CANDY(Subcategory.INTERFACE_TOOLTIP_CANDY, LangUtil.Gui.CANDY_EMBED_TOOLTIP_PARTS),

        // World (Eye Candy) - Embedded Subcategories

        WATER_FOG_CANDY(Subcategory.WORLD_FOG_CANDY, LangUtil.Gui.CANDY_EMBED_WATER_FOG),
        SHADER_LIGHT(Subcategory.LIGHTING_WORLD_CANDY, LangUtil.Gui.CANDY_EMBED_SHADER_LIGHT),

        // Void (Eye Candy) - Embedded Subcategories

        VOID_FOG_CANDY(Subcategory.WORLD_VOID_CANDY, LangUtil.Gui.CANDY_EMBED_VOID_FOG),
        VOID_SKY_CANDY(Subcategory.WORLD_VOID_CANDY, LangUtil.Gui.CANDY_EMBED_VOID_SKY),

        // Experience Bar (Gameplay) - Embedded Subcategories

        ALT_XP_LEVEL_GAMEPLAY(Subcategory.EXPERIENCE_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_XP_LEVEL),
        ALT_XP_PROGRESS_GAMEPLAY(Subcategory.EXPERIENCE_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_XP_PROGRESS),

        // Hunger Bar (Gameplay) - Embedded Subcategories

        ALT_HUNGER_FOOD_GAMEPLAY(Subcategory.HUNGER_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_HUNGER_FOOD),
        ALT_HUNGER_SATURATION_GAMEPLAY(Subcategory.HUNGER_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_HUNGER_SATURATION);

        private final String langKey;
        private final Subcategory subcategory;

        Embedded(Subcategory subcategory, String langKey)
        {
            this.subcategory = subcategory;
            this.langKey = langKey;
        }

        public Subcategory getSubcategory() { return this.subcategory; }
        public String getLangKey() { return this.langKey; }
    }
}
