package mod.adrenix.nostalgic.common.config.v2.container.group;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;

public abstract class ModGroup
{
    public static final TweakContainer MENU = TweakContainer.group(TweakCategory.MOD, "menu");
    public static final TweakContainer CONFIG = TweakContainer.group(TweakCategory.MOD, "config");
    public static final TweakContainer TAGS = TweakContainer.group(TweakCategory.MOD, "tags");
    public static final TweakContainer STATUS = TweakContainer.group(TweakCategory.MOD, "status");
    public static final TweakContainer TREE = TweakContainer.group(TweakCategory.MOD, "tree");
    public static final TweakContainer ROWS = TweakContainer.group(TweakCategory.MOD, "rows");
}
