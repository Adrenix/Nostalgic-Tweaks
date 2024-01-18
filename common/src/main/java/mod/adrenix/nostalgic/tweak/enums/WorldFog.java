package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The fog terrain tweak uses the world fog enumeration. That tweak changes how the overworld terrain fog is rendered.
 * Other dimensions are not affected.
 */
public enum WorldFog implements EnumTweak
{
    MODERN(Generic.MODERN.getTitle()),
    CLASSIC(Lang.Enum.PLAIN_CLASSIC),
    ALPHA_R164(Lang.Enum.FOG_ALPHA_R164),
    R17_R118(Lang.Enum.FOG_R17_R118);

    private final Translation title;

    WorldFog(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
