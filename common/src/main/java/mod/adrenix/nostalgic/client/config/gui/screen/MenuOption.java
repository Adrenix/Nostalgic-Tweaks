package mod.adrenix.nostalgic.client.config.gui.screen;

import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.network.chat.Component;

public enum MenuOption
{
    MAIN_MENU(LangUtil.Gui.GENERAL_CONFIG_SCREEN_MAIN),
    SETTINGS_MENU(LangUtil.Gui.GENERAL_CONFIG_SCREEN_SETTINGS),
    CUSTOM_SWING_MENU(LangUtil.Gui.GENERAL_CONFIG_SCREEN_CUSTOM);

    MenuOption(String langKey) { this.langKey = langKey; }

    private final String langKey;
    public String getLangKey() { return this.langKey; }
    public static Component getTranslation(MenuOption screen) { return Component.translatable(screen.getLangKey()); }
    @Override public String toString() { return ModUtil.Text.toTitleCase(super.toString()); }
}
