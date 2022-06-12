package mod.adrenix.nostalgic.client.config.tweak;

import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class TweakVersion
{
    public interface IDisabled<E extends Enum<E>>
    {
        E getDisabled();
    }

    public enum GENERIC implements IDisabled<GENERIC>
    {
        ALPHA(NostalgicLang.Gui.SETTINGS_ALPHA),
        BETA(NostalgicLang.Gui.SETTINGS_BETA),
        MODERN(NostalgicLang.Gui.SETTINGS_MODERN);

        private final String langKey;

        GENERIC(String langKey) { this.langKey = langKey; }

        public String toString() { return new TranslatableComponent(this.langKey).getString(); }
        public String getLangKey() { return this.langKey; }
        public GENERIC getDisabled() { return MODERN; }
    }

    public enum OVERLAY implements IDisabled<OVERLAY>
    {
        ALPHA(GENERIC.ALPHA.getLangKey()),
        BETA(GENERIC.BETA.getLangKey()),
        RELEASE_ORANGE("§61.0§r - §61.6.4"),
        RELEASE_BLACK("§61.7§r - §61.15"),
        MODERN(GENERIC.MODERN.getLangKey());

        private final String langKey;

        OVERLAY(String langKey) { this.langKey = langKey; }

        public String toString() { return new TranslatableComponent(this.langKey).getString(); }
        public OVERLAY getDisabled() { return MODERN; }
    }

    public enum BUTTON_LAYOUT implements IDisabled<BUTTON_LAYOUT>
    {
        ALPHA(GENERIC.ALPHA.getLangKey()),
        BETA(GENERIC.BETA.getLangKey()),
        RELEASE_TEXTURE_PACK("§61.0§r - §61.4.7"),
        RELEASE_NO_TEXTURE_PACK("§61.5.2§r - §61.7.9"),
        MODERN(GENERIC.MODERN.getLangKey());

        private final String langKey;

        BUTTON_LAYOUT(String langKey) { this.langKey = langKey; }

        public String toString() { return new TranslatableComponent(this.langKey).getString(); }
        public BUTTON_LAYOUT getDisabled() { return MODERN; }
    }

    public enum HOTBAR implements IDisabled<HOTBAR>
    {
        CLASSIC(NostalgicLang.Gui.SETTINGS_CLASSIC),
        BETA(GENERIC.BETA.getLangKey()),
        MODERN(GENERIC.MODERN.getLangKey());

        private final String langKey;

        HOTBAR(String langKey) { this.langKey = langKey; }

        public String toString() { return new TranslatableComponent(this.langKey).getString(); }
        public HOTBAR getDisabled() { return MODERN; }
    }
}
