package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.network.chat.Component;

/**
 * Any enumeration that is used by the server will need to be updated in the serializer.
 * Failing to do so will result in an invalid packet and the tweak can never be changed in multiplayer.
 *
 * @see mod.adrenix.nostalgic.common.config.tweak.TweakSerializer
 */

public abstract class TweakVersion
{
    public enum Generic implements IDisableTweak<Generic>
    {
        ALPHA(NostalgicLang.Gui.SETTINGS_ALPHA),
        BETA(NostalgicLang.Gui.SETTINGS_BETA),
        MODERN(NostalgicLang.Gui.SETTINGS_MODERN);

        private final String langKey;

        Generic(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public String getLangKey() { return this.langKey; }
        public Generic getDisabled() { return MODERN; }
    }

    public enum Overlay implements IDisableTweak<Overlay>
    {
        ALPHA(Generic.ALPHA.getLangKey()),
        BETA(Generic.BETA.getLangKey()),
        RELEASE_ORANGE("§61.0§r - §61.6.4"),
        RELEASE_BLACK("§61.7§r - §61.15"),
        MODERN(Generic.MODERN.getLangKey());

        private final String langKey;

        Overlay(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public Overlay getDisabled() { return MODERN; }
    }

    public enum TitleLayout implements IDisableTweak<TitleLayout>
    {
        ALPHA(Generic.ALPHA.getLangKey()),
        BETA(Generic.BETA.getLangKey()),
        RELEASE_TEXTURE_PACK("§61.0§r - §61.4.7"),
        RELEASE_NO_TEXTURE_PACK("§61.5.2§r - §61.7.9"),
        MODERN(Generic.MODERN.getLangKey());

        private final String langKey;

        TitleLayout(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public TitleLayout getDisabled() { return MODERN; }
    }

    public enum PauseLayout implements IDisableTweak<PauseLayout>
    {
        ALPHA_BETA("§aAlpha§r - §eb1.4_01"),
        ACHIEVE_LOWER("§eb1.5§r - §61.0"),
        ACHIEVE_UPPER("§61.1§r - §61.2.5"),
        LAN("§61.3§r - §61.11"),
        ADVANCEMENT("§61.12§r - §61.13.2"),
        MODERN(Generic.MODERN.getLangKey());

        private final String langKey;

        PauseLayout(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public PauseLayout getDisabled() { return MODERN; }
    }

    public enum GuiBackground implements IDisableTweak<GuiBackground>
    {
        SOLID_BLACK(NostalgicLang.Gui.BACKGROUND_SOLID_BLACK),
        SOLID_BLUE(NostalgicLang.Gui.BACKGROUND_SOLID_BLUE),
        GRADIENT_BLUE(NostalgicLang.Gui.BACKGROUND_GRADIENT_BLUE);

        private final String langKey;

        GuiBackground(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public GuiBackground getDisabled() { return SOLID_BLACK; }
    }

    public enum Hotbar implements IDisableTweak<Hotbar>
    {
        CLASSIC(NostalgicLang.Gui.SETTINGS_CLASSIC),
        BETA(Generic.BETA.getLangKey()),
        MODERN(Generic.MODERN.getLangKey());

        private final String langKey;

        Hotbar(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public Hotbar getDisabled() { return MODERN; }
    }
}
