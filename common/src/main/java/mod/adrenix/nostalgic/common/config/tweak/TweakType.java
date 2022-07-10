package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.network.chat.Component;

/**
 * Any enumeration type that is not considered a "version" enumeration.
 * This enumeration should be kept server safe, so keep vanilla code out.
 *
 * For version enumerations see {@link mod.adrenix.nostalgic.common.config.tweak.TweakVersion}.
 */

public abstract class TweakType
{
    public enum Corner implements IDisableTweak<Corner>
    {
        TOP_LEFT(NostalgicLang.Gui.CORNER_TOP_LEFT),
        TOP_RIGHT(NostalgicLang.Gui.CORNER_TOP_RIGHT),
        BOTTOM_LEFT(NostalgicLang.Gui.CORNER_BOTTOM_LEFT),
        BOTTOM_RIGHT(NostalgicLang.Gui.CORNER_BOTTOM_RIGHT);

        private final String langKey;

        Corner(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public Corner getDisabled() { return TOP_LEFT; }
    }
}
