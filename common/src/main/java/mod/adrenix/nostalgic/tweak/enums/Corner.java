package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The corner enumeration is used by tweaks that display information to one of the four corners of the game window. This
 * can also be used in other scenarios where text corner tracking would be helpful.
 */
public enum Corner implements EnumTweak
{
    TOP_LEFT(Lang.Enum.CORNER_TOP_LEFT),
    TOP_RIGHT(Lang.Enum.CORNER_TOP_RIGHT),
    BOTTOM_LEFT(Lang.Enum.CORNER_BOTTOM_LEFT),
    BOTTOM_RIGHT(Lang.Enum.CORNER_BOTTOM_RIGHT);

    private final Translation title;

    Corner(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
