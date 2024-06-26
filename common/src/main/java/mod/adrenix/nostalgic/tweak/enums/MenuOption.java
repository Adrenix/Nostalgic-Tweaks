package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * This enumeration provides options that are used to change the default that is opened when the settings hotkey is
 * pressed.
 */
public enum MenuOption implements EnumTweak
{
    HOME_SCREEN(Lang.Enum.SCREEN_HOME),
    CONFIG_SCREEN(Lang.Enum.SCREEN_CONFIG),
    PACKS_SCREEN(Lang.Enum.SCREEN_PACKS);

    private final Translation title;

    MenuOption(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
