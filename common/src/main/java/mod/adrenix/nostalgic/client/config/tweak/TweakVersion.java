package mod.adrenix.nostalgic.client.config.tweak;

public abstract class TweakVersion
{
    public interface IDisabled<E extends Enum<E>>
    {
        E getDisabled();
    }

    public enum GENERIC implements IDisabled<GENERIC>
    {
        ALPHA("§aAlpha"),
        BETA("§eBeta"),
        MODERN("§cModern");

        private final String display;

        GENERIC(String display) { this.display = display; }

        public String toString() { return this.display; }
        public GENERIC getDisabled() { return MODERN; }
    }

    public enum OVERLAY implements IDisabled<OVERLAY>
    {
        ALPHA(GENERIC.ALPHA.toString()),
        BETA(GENERIC.BETA.toString()),
        RELEASE_ORANGE("§61.0§r - §61.6.4"),
        RELEASE_BLACK("§61.7§r - §61.15"),
        MODERN(GENERIC.MODERN.toString());

        private final String display;

        OVERLAY(String display) { this.display = display; }

        public String toString() { return this.display; }
        public OVERLAY getDisabled() { return MODERN; }
    }

    public enum BUTTON_LAYOUT implements IDisabled<BUTTON_LAYOUT>
    {
        ALPHA(GENERIC.ALPHA.toString()),
        BETA(GENERIC.BETA.toString()),
        RELEASE_TEXTURE_PACK("§61.0§r - §61.4.7"),
        RELEASE_NO_TEXTURE_PACK("§61.5.2§r - §61.7.9"),
        MODERN(GENERIC.MODERN.toString());

        private final String display;

        BUTTON_LAYOUT(String display) { this.display = display; }

        public String toString() { return this.display; }
        public BUTTON_LAYOUT getDisabled() { return MODERN; }
    }
}
