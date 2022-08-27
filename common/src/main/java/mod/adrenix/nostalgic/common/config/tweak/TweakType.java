package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.network.chat.Component;

/**
 * Any enumeration type that is not considered a "change by Minecraft version" enumeration.
 * This enumeration should be kept server safe, so keep vanilla client-only code out.
 *
 * For version enumerations see {@link mod.adrenix.nostalgic.common.config.tweak.TweakVersion}.
 */

public abstract class TweakType
{
    public enum Corner implements IDisableTweak<Corner>
    {
        TOP_LEFT(LangUtil.Gui.CORNER_TOP_LEFT),
        TOP_RIGHT(LangUtil.Gui.CORNER_TOP_RIGHT),
        BOTTOM_LEFT(LangUtil.Gui.CORNER_BOTTOM_LEFT),
        BOTTOM_RIGHT(LangUtil.Gui.CORNER_BOTTOM_RIGHT);

        private final String langKey;

        Corner(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public Corner getDisabled() { return TOP_LEFT; }
    }

    public enum GuiBackground implements IDisableTweak<GuiBackground>
    {
        SOLID_BLACK(LangUtil.Gui.BACKGROUND_SOLID_BLACK),
        SOLID_BLUE(LangUtil.Gui.BACKGROUND_SOLID_BLUE),
        GRADIENT_BLUE(LangUtil.Gui.BACKGROUND_GRADIENT_BLUE);

        private final String langKey;

        GuiBackground(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public GuiBackground getDisabled() { return SOLID_BLACK; }
    }

    public enum RecipeBook implements IDisableTweak<RecipeBook>
    {
        MODERN(LangUtil.Gui.SETTINGS_MODERN),
        DISABLED(LangUtil.Gui.RECIPE_BOOK_DISABLED),
        LARGE(LangUtil.Gui.RECIPE_BOOK_LARGE),
        SMALL(LangUtil.Gui.RECIPE_BOOK_SMALL);

        private final String langKey;

        RecipeBook(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public RecipeBook getDisabled() { return MODERN; }
    }

    public enum InventoryShield implements IDisableTweak<InventoryShield>
    {
        MODERN(LangUtil.Gui.SETTINGS_MODERN),
        INVISIBLE(LangUtil.Gui.INVENTORY_SHIELD_INVISIBLE),
        MIDDLE_RIGHT(LangUtil.Gui.INVENTORY_SHIELD_MIDDLE_RIGHT),
        BOTTOM_LEFT(LangUtil.Gui.INVENTORY_SHIELD_BOTTOM_LEFT);

        private final String langKey;

        InventoryShield(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public InventoryShield getDisabled() { return MODERN; }
    }

    public enum DebugChart implements IDisableTweak<DebugChart>
    {
        MODERN(LangUtil.Gui.DEBUG_CHART_MODERN),
        CLASSIC(LangUtil.Gui.DEBUG_CHART_CLASSIC),
        DISABLED(LangUtil.Gui.DEBUG_CHART_DISABLED);

        private final String langKey;

        DebugChart(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public DebugChart getDisabled() { return CLASSIC; }
    }
}
