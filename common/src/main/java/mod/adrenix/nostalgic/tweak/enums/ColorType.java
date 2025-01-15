package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The color type enumeration is used by tweaks that can use both a solid and gradient color. Using this enumeration
 * prevents the need of having multiple tweak flags to control whether a tweak is using a solid or gradient color.
 */
public enum ColorType implements EnumTweak
{
    SOLID(Lang.Enum.SOLID_COLOR),
    GRADIENT(Lang.Enum.GRADIENT_COLOR);

    private final Translation title;

    ColorType(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
