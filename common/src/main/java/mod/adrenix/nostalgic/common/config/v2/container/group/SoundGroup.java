package mod.adrenix.nostalgic.common.config.v2.container.group;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;

public abstract class SoundGroup
{
    // Ambient Sound

    public static final TweakContainer AMBIENT = TweakContainer.group(TweakCategory.SOUND, "ambient");

    // Block Sound

    public static final TweakContainer BLOCK = TweakContainer.group(TweakCategory.SOUND, "block");
    public static final TweakContainer BLOCK_BED = TweakContainer.group(BLOCK, "block_bed");
    public static final TweakContainer BLOCK_CHEST = TweakContainer.group(BLOCK, "block_chest");
    public static final TweakContainer BLOCK_LAVA = TweakContainer.group(BLOCK, "block_lava");

    // Damage Sound

    public static final TweakContainer DAMAGE = TweakContainer.group(TweakCategory.SOUND, "damage");

    // Experience Sound

    public static final TweakContainer EXPERIENCE = TweakContainer.group(TweakCategory.SOUND, "experience");

    // Mob Sound

    public static final TweakContainer MOB = TweakContainer.group(TweakCategory.SOUND, "mob");
    public static final TweakContainer MOB_FISH = TweakContainer.group(MOB, "mob_fish");
    public static final TweakContainer MOB_SQUID = TweakContainer.group(MOB, "mob_squid");
    public static final TweakContainer MOB_GENERIC = TweakContainer.group(MOB, "mob_generic");
}
