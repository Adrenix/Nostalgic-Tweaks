package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The stamina regain enumeration is used by the stamina regain during movement tweak.
 */
public enum StaminaRegain implements EnumTweak
{
    NORMAL(Lang.Enum.STAMINA_REGAIN_NORMAL),
    HALF(Lang.Enum.STAMINA_REGAIN_HALF),
    NONE(Lang.Enum.STAMINA_REGAIN_NONE);

    private final Translation title;

    StaminaRegain(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
