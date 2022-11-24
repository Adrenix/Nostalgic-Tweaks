package mod.adrenix.nostalgic.client.config.gui.screen;

import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.network.chat.Component;

/**
 * This enumeration provides options that are used by the default screen radio group widget under menu settings.
 * Depending on the selection, the main menu screen will be redirected to a different screen.
 */

public enum MenuOption
{
    /* Options */

    MAIN_MENU(LangUtil.Gui.GENERAL_CONFIG_SCREEN_MAIN),
    SETTINGS_MENU(LangUtil.Gui.GENERAL_CONFIG_SCREEN_SETTINGS),
    CUSTOM_SWING_MENU(LangUtil.Gui.GENERAL_CONFIG_SCREEN_CUSTOM);

    /* Constructor */

    /**
     * Create a new menu option that will be used by the menu settings default screen radio widget.
     * @param langKey The language key associated with this option.
     */
    MenuOption(String langKey) { this.langKey = langKey; }

    /* Fields */

    private final String langKey;

    /* Utility */

    /**
     * Get the translation of a menu option based on its translation key.
     * @param option The menu option.
     * @return A component that can be used for text rendering.
     */
    public static Component getTranslation(MenuOption option) { return Component.translatable(option.getLangKey()); }

    /* Methods */

    /**
     * Get the language key associated with this enumeration.
     * @return A language file key.
     */
    public String getLangKey() { return this.langKey; }

    /**
     * Overrides the default toString method so that when invoked, returns the option name in title case format.
     * @return An enumeration name in title case format.
     */
    @Override
    public String toString() { return TextUtil.toTitleCase(super.toString()); }
}
