package mod.adrenix.nostalgic.client.config.tweak;

public abstract class TweakVersion
{
    public interface IDefault<E extends Enum<E>>
    {
        E getDefault();
    }

    public enum GENERIC implements IDefault<GENERIC>
    {
        ALPHA("§aAlpha"),
        BETA("§eBeta"),
        MODERN("§cModern");

        private final String display;

        GENERIC(String display) { this.display = display; }

        public String toString() { return this.display; }
        public GENERIC getDefault() { return MODERN; }
    }

    public enum OVERLAY implements IDefault<OVERLAY>
    {
        ALPHA(GENERIC.ALPHA.toString()),
        BETA(GENERIC.BETA.toString()),
        RELEASE_1("§61.0§r - §61.6.4"),
        RELEASE_2("§61.7§r - §61.15"),
        MODERN(GENERIC.MODERN.toString());

        private final String display;

        OVERLAY(String display) { this.display = display; }

        public String toString() { return this.display; }
        public OVERLAY getDefault() { return MODERN; }
    }
}
