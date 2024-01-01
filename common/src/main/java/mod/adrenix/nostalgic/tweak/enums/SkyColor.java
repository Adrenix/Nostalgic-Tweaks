package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The sky color tweak uses the sky color enumeration. That tweak universally changes the sky color in the overworld.
 * Classic used a color of (#A6D1FE). Inf-dev used a sky color of (#C6DEFF). The sky color in alpha was (#8BBDFF). The
 * sky color is beta changed depending on the temperature of the biome. The universal color used by this enumeration is
 * (#97A3FF).
 */
public enum SkyColor implements EnumTweak
{
    DISABLED(Lang.Enum.DISABLED),
    INF_DEV(Lang.Enum.BASIC_INF_DEV),
    CLASSIC(Lang.Enum.BASIC_CLASSIC),
    ALPHA(Generic.ALPHA.getTitle()),
    BETA(Generic.BETA.getTitle());

    private final Translation title;

    SkyColor(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
