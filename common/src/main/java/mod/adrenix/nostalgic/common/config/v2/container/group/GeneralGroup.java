package mod.adrenix.nostalgic.common.config.v2.container.group;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;

public abstract class GeneralGroup
{
    public static final TweakContainer MENU = TweakContainer.group(TweakCategory.GENERAL, "menu");
    public static final TweakContainer CONFIG = TweakContainer.group(TweakCategory.GENERAL, "config");
    public static final TweakContainer TAGS = TweakContainer.group(TweakCategory.GENERAL, "tags");
    public static final TweakContainer STATUS = TweakContainer.group(TweakCategory.GENERAL, "status");
    public static final TweakContainer TREE = TweakContainer.group(TweakCategory.GENERAL, "tree");
    public static final TweakContainer ROWS = TweakContainer.group(TweakCategory.GENERAL, "rows");
}
