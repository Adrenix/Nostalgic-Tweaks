package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;

public interface AnimationGroup
{
    Container ARM = Container.group(Category.ANIMATION, "arm").color(0x9A7B4C).icon(Icons.BREAK_WOOD).build();
    Container ARM_SWAY = Container.group(ARM, "arm_sway").color(0xFFBC9B).icon(Icons.ARM_SWAY).build();
    Container ARM_SWING = Container.group(ARM, "arm_swing").color(0xE0E0E0).icon(Items.IRON_PICKAXE).build();
    Container ITEM = Container.group(Category.ANIMATION, "item").color(0xEAEE57).icon(Items.GOLDEN_SHOVEL).build();
    Container MOB = Container.group(Category.ANIMATION, "mob").color(0x6F955C).icon(Items.ZOMBIE_HEAD).build();
    Container BOAT = Container.group(Category.ANIMATION, "boat").color(0x896727).icon(Items.OAK_BOAT).build();
    Container PLAYER = Container.group(Category.ANIMATION, "player").color(0xB6896C).icon(Items.PLAYER_HEAD).build();
}
