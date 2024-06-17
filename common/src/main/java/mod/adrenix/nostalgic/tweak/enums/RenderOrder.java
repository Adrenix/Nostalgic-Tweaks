package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The render order enumeration is used by tweaks that can render in different orders. This is useful in situations
 * where different rendering effects may be desired.
 */
public enum RenderOrder implements EnumTweak
{
    FIRST(Lang.Enum.RENDER_ORDER_FIRST),
    LAST(Lang.Enum.RENDER_ORDER_LAST);

    private final Translation title;

    RenderOrder(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
