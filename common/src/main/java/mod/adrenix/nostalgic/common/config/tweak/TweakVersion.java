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
    public interface IDisabled<E extends Enum<E>>
    {
        E getDisabled();
    }

    public enum Generic implements IDisabled<Generic>
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

    public enum Overlay implements IDisabled<Overlay>
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

    public enum ButtonLayout implements IDisabled<ButtonLayout>
    {
        ALPHA(Generic.ALPHA.getLangKey()),
        BETA(Generic.BETA.getLangKey()),
        RELEASE_TEXTURE_PACK("§61.0§r - §61.4.7"),
        RELEASE_NO_TEXTURE_PACK("§61.5.2§r - §61.7.9"),
        MODERN(Generic.MODERN.getLangKey());

        private final String langKey;

        ButtonLayout(String langKey) { this.langKey = langKey; }

        public String toString() { return Component.translatable(this.langKey).getString(); }
        public ButtonLayout getDisabled() { return MODERN; }
    }

    public enum Hotbar implements IDisabled<Hotbar>
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
