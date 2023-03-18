package mod.adrenix.nostalgic.common.config.v2.container;

/**
 * Any additional categories defined in this utility class need to have container ids that match what will appear in the
 * config JSON file. This is required so reflection can be performed when tweak values need changed.
 */

public abstract class TweakCategory
{
    public static final TweakContainer ROOT = TweakContainer.category("");
    public static final TweakContainer SOUND = TweakContainer.category("sound");
    public static final TweakContainer EYE_CANDY = TweakContainer.category("eyeCandy");
    public static final TweakContainer GAMEPLAY = TweakContainer.category("gameplay");
    public static final TweakContainer ANIMATION = TweakContainer.category("animation");
    public static final TweakContainer SWING = TweakContainer.category("swing");
    public static final TweakContainer MOD = TweakContainer.category("mod");
}
