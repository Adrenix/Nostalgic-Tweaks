package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

public enum OldCreativeInventory implements EnumTweak
{
    CLASSIC(Lang.Enum.CLASSIC),
    BETA(Generic.BETA.getTitle()),
    MODERN(Generic.MODERN.getTitle());

    private final Translation title;

    OldCreativeInventory(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
