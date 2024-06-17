package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The overlay enumeration is used by the overlay tweak. That tweak makes changes to the game's loading overlay. This
 * overlay appears during the game's startup, and when applying resource pack changes.
 */
public enum Overlay implements EnumTweak
{
    ALPHA(Generic.ALPHA.getTitle()),
    BETA(Generic.BETA.getTitle()),
    RELEASE_ORANGE(Lang.literal("§61.0§r - §61.6.4")),
    RELEASE_BLACK(Lang.literal("§61.7§r - §61.15")),
    MODERN(Generic.MODERN.getTitle());

    private final Translation title;

    Overlay(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
