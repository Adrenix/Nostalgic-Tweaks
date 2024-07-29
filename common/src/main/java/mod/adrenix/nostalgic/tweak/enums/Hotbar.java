package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The hotbar enumeration is used by the creative hotbar tweak. That tweak changes the default items that are loaded
 * into a player's empty inventory after that player joins a level in creative mode.
 */
public enum Hotbar implements EnumTweak
{
    CLASSIC(Lang.Enum.CLASSIC),
    BETA(Generic.BETA.getTitle()),
    MODERN(Generic.MODERN.getTitle());

    private final Translation title;

    Hotbar(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
