package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;

public interface SwingGroup
{
    Container GLOBAL = Container.group(Category.SWING, "global").color(0x4FDA4C).icon(Icons.CIRCLE_EARTH).build();
    Container CUSTOM = Container.group(Category.SWING, "custom").color(0xBFCCDD).icon(Icons.WRENCH).build();
    Container ITEM = Container.group(Category.SWING, "item").color(0xEAEE57).icon(Items.GOLDEN_SWORD).build();
    Container POTION = Container.group(Category.SWING, "potion").color(0x497AFF).icon(Items.POTION).build();
}
