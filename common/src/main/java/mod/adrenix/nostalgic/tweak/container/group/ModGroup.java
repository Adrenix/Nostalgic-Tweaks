package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;

// @formatter:off
public interface ModGroup
{
    Container CONFIG = Container.group(Category.MOD, "config").icon(Items.WRITABLE_BOOK).color(0xAF492D).description().build();
    Container BINDING = Container.group(Category.MOD, "binding").icon(Icons.BUTTON).color(0xA9A7A6).build();
    Container TOAST = Container.group(Category.MOD, "toast").icon(Items.BREAD).color(0xBB8926).build();
    Container VISUALS = Container.group(Category.MOD, "visuals").icon(Icons.PAINTBRUSH).color(0x8490FF).build();
    Container TAGS = Container.group(VISUALS, "visuals_tags").icon(Items.NAME_TAG).color(0xE6C78D).build();
    Container TREE = Container.group(VISUALS, "visuals_tree").icon(Icons.COLOR_TREE).color(0xA0FF77).build();
    Container ROWS = Container.group(VISUALS, "visuals_rows").icon(Icons.COLOR_LIST).color(0xE08041).build();
}
