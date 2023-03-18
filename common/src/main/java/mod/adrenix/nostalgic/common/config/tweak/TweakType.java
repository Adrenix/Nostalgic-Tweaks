package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.network.chat.Component;

/**
 * This class provides enumeration types that are not considered a "change by Minecraft version". Instead, this utility
 * class is intended for unique enumerations that are specific to a certain tweak.
 *
 * This enumeration should be kept server safe, so keep vanilla client-only code out.
 *
 * For Minecraft version enumerations see {@link mod.adrenix.nostalgic.common.config.tweak.TweakVersion}.
 */

public abstract class TweakType
{
    /**
     * The corner enumeration is used by tweaks that display information to one of the four corners of the game window.
     * This can also be used in other scenarios where corner tracking would be helpful.
     */
    public enum Corner implements DisabledTweak<Corner>
    {
        /* Enumerations */

        TOP_LEFT(LangUtil.Gui.CORNER_TOP_LEFT),
        TOP_RIGHT(LangUtil.Gui.CORNER_TOP_RIGHT),
        BOTTOM_LEFT(LangUtil.Gui.CORNER_BOTTOM_LEFT),
        BOTTOM_RIGHT(LangUtil.Gui.CORNER_BOTTOM_RIGHT);

        /* Fields */

        private final String langKey;

        /* Constructor */

        /**
         * Enumeration constructor.
         * @param langKey A language file key.
         */
        Corner(String langKey) { this.langKey = langKey; }

        /* Methods */

        /**
         * Overrides the toString method so that it returns a translation.
         * @return A translation from a language file.
         */
        @Override
        public String toString() { return Component.translatable(this.langKey).getString(); }

        /**
         * The default corner enumeration is the top left.
         * @return A disabled default corner enumeration value.
         */
        @Override
        public Corner getDisabledValue() { return TOP_LEFT; }
    }

    /**
     * The graphical user interface background enumeration is used by tweaks that change the background of transparent
     * screens. Such as the inventory screen.
     */
    public enum GuiBackground implements DisabledTweak<GuiBackground>
    {
        /* Enumerations */

        SOLID_BLACK(LangUtil.Gui.BACKGROUND_SOLID_BLACK),
        SOLID_BLUE(LangUtil.Gui.BACKGROUND_SOLID_BLUE),
        GRADIENT_BLUE(LangUtil.Gui.BACKGROUND_GRADIENT_BLUE);

        /* Fields */

        private final String langKey;

        /* Constructor */

        /**
         * Enumeration constructor.
         * @param langKey A language file key.
         */
        GuiBackground(String langKey) { this.langKey = langKey; }

        /* Methods */

        /**
         * Overrides the toString method so that it returns a translation.
         * @return A translation from a language file.
         */
        @Override
        public String toString() { return Component.translatable(this.langKey).getString(); }

        /**
         * The default disabled background value is solid black.
         * @return A disabled default background enumeration value.
         */
        @Override
        public GuiBackground getDisabledValue() { return SOLID_BLACK; }
    }

    /**
     * The recipe book enumeration is used by tweaks that change the position of the recipe book button in the inventory
     * or crafting screen.
     */
    public enum RecipeBook implements DisabledTweak<RecipeBook>
    {
        /* Enumerations */

        MODERN(LangUtil.Gui.SETTINGS_MODERN),
        DISABLED(LangUtil.Gui.RECIPE_BOOK_DISABLED),
        LARGE(LangUtil.Gui.RECIPE_BOOK_LARGE),
        SMALL(LangUtil.Gui.RECIPE_BOOK_SMALL);

        /* Fields */

        private final String langKey;

        /* Constructor */

        /**
         * Enumeration constructor.
         * @param langKey A language file key.
         */
        RecipeBook(String langKey) { this.langKey = langKey; }

        /* Methods */

        /**
         * Overrides the toString method so that it returns a translation.
         * @return A translation from a language file.
         */
        @Override
        public String toString() { return Component.translatable(this.langKey).getString(); }

        /**
         * The default disabled recipe book value is modern.
         * @return A disabled default recipe book enumeration value.
         */
        @Override
        public RecipeBook getDisabledValue() { return MODERN; }
    }

    /**
     * The inventory shield enumeration is used by tweaks that change the position of the off-hand slot in user's
     * inventory screen.
     */
    public enum InventoryShield implements DisabledTweak<InventoryShield>
    {
        /* Enumerations */

        MODERN(LangUtil.Gui.SETTINGS_MODERN),
        INVISIBLE(LangUtil.Gui.INVENTORY_SHIELD_INVISIBLE),
        MIDDLE_RIGHT(LangUtil.Gui.INVENTORY_SHIELD_MIDDLE_RIGHT),
        BOTTOM_LEFT(LangUtil.Gui.INVENTORY_SHIELD_BOTTOM_LEFT);

        /* Fields */

        private final String langKey;

        /* Constructor */

        /**
         * Enumeration constructor.
         * @param langKey A language file key.
         */
        InventoryShield(String langKey) { this.langKey = langKey; }

        /* Methods */

        /**
         * Overrides the toString method so that it returns a translation.
         * @return A translation from a language file.
         */
        @Override
        public String toString() { return Component.translatable(this.langKey).getString(); }

        /**
         * The default disabled inventory shield value is modern.
         * @return A disabled default inventory shield enumeration value.
         */
        @Override
        public InventoryShield getDisabledValue() { return MODERN; }
    }

    /**
     * The debug chart enumeration is used by tweaks that change the way the debug FPS chart is rendered. These tweaks
     * do not change the way the TPS chart is rendered.
     */
    public enum DebugChart implements DisabledTweak<DebugChart>
    {
        /* Enumerations */

        MODERN(LangUtil.Gui.DEBUG_CHART_MODERN),
        CLASSIC(LangUtil.Gui.DEBUG_CHART_CLASSIC),
        DISABLED(LangUtil.Gui.DEBUG_CHART_DISABLED);

        /* Fields */

        private final String langKey;

        /* Constructor */

        /**
         * Enumeration constructor.
         * @param langKey A language file key.
         */
        DebugChart(String langKey) { this.langKey = langKey; }

        /* Methods */

        /**
         * Overrides the toString method so that it returns a translation.
         * @return A translation from a language file.
         */
        @Override
        public String toString() { return Component.translatable(this.langKey).getString(); }

        /**
         * The default disabled debug chart value is classic.
         * @return A disabled default debug chart enumeration value.
         */
        @Override
        public DebugChart getDisabledValue() { return CLASSIC; }
    }
}
