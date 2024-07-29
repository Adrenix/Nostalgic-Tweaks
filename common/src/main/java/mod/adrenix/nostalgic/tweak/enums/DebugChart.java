package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The debug chart enumeration is used by tweaks that change the way the debug FPS chart is rendered. These tweaks do
 * not change the way the TPS chart is rendered.
 */
public enum DebugChart implements EnumTweak
{
    MODERN(Lang.Enum.DEBUG_CHART_MODERN),
    CLASSIC(Lang.Enum.DEBUG_CHART_CLASSIC),
    DISABLED(Lang.Enum.DEBUG_CHART_DISABLED);

    private final Translation title;

    DebugChart(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
