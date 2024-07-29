package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

public enum MusicType implements EnumTweak
{
    ALPHA(Lang.Enum.MUSIC_ALPHA),
    BETA(Lang.Enum.MUSIC_BETA),
    BLENDED(Lang.Enum.MUSIC_BLENDED),
    DISABLED(Lang.Enum.DISABLED),
    MODERN(Generic.MODERN.getTitle());

    private final Translation title;

    MusicType(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
