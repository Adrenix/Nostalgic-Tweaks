package mod.adrenix.nostalgic.common.config.v2.container.group;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;

public abstract class AnimationGroup
{
    // Arm

    public static final TweakContainer ARM = TweakContainer.group(TweakCategory.ANIMATION, "arm");

    // Item

    public static final TweakContainer ITEM = TweakContainer.group(TweakCategory.ANIMATION, "item");

    // Mob

    public static final TweakContainer MOB = TweakContainer.group(TweakCategory.ANIMATION, "mob");

    // Player

    public static final TweakContainer PLAYER = TweakContainer.group(TweakCategory.ANIMATION, "player");
}
