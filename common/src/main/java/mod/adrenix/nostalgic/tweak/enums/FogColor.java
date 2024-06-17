package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The fog color enumeration is used by the terrain fog color tweak. That tweak changes the fog color in the overworld.
 * Classic used a color of (#E2F0FF). Inf-dev used a fog color of (#B0D0FF). The fog color remained constant through
 * alpha - beta (#C0D8FF). Modern fog uses (#ADCBFF).
 */
public enum FogColor implements EnumTweak
{
    DISABLED(Lang.Enum.DISABLED),
    INF_DEV(Lang.Enum.INF_DEV),
    CLASSIC(Lang.Enum.PLAIN_CLASSIC),
    ALPHA_BETA(Lang.Enum.ALPHA_BETA);

    private final Translation title;

    FogColor(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
