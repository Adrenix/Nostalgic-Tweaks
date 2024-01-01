package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The missing texture enumeration is used by the missing texture tweak. This tweak was contributed by forkiesassds on
 * GitHub. This tweak changes the missing texture based on the selected version.
 */
public enum MissingTexture implements EnumTweak
{
    MODERN(Generic.MODERN.getTitle()),
    BETA(Generic.BETA.getTitle()),
    R15(Lang.literal("§61.5§r")),
    R16_R112(Lang.literal("§61.6§r - §61.12"));

    private final Translation title;

    MissingTexture(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
