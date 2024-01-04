package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

// @formatter:off
public interface GameplayGroup
{
    // Bugs

    Container BUGS = Container.group(Category.GAMEPLAY, "bugs").color(0xFB4A4C).icon(Icons.BUG).build();

    // Combat

    Container COMBAT = Container.group(Category.GAMEPLAY, "combat").color(0x2BC7AC).icon(Items.DIAMOND_SWORD).build();
    Container COMBAT_BOW = Container.group(COMBAT, "combat_bow").color(0x896727).icon(Items.BOW).build();

    // Experience

    Container EXPERIENCE = Container.group(Category.GAMEPLAY, "experience").color(0xF5FF8F).icon(Items.EXPERIENCE_BOTTLE).build();
    Container EXPERIENCE_BAR = Container.group(EXPERIENCE, "experience_bar").color(0x86C457).icon(Icons.XP_BAR).build();
    Container EXPERIENCE_BAR_ALT_LEVEL = Container.group(EXPERIENCE_BAR, "experience_bar_alt_level").color(0x7EFC20).icon(Icons.XP_LEVEL).build();
    Container EXPERIENCE_BAR_ALT_PROGRESS = Container.group(EXPERIENCE_BAR, "experience_bar_alt_progress").color(0x8FB7A3).icon(Icons.XP_HALF_BAR).build();
    Container EXPERIENCE_ORB = Container.group(EXPERIENCE, "experience_orb").color(0xBBB71B).icon(Icons.EXPERIENCE).build();
    Container EXPERIENCE_BLOCK = Container.group(EXPERIENCE, "experience_block").color(0xA4EDDC).icon(Blocks.ENCHANTING_TABLE).build();

    // Mechanics

    Container MECHANICS = Container.group(Category.GAMEPLAY, "mechanics").color(0xFFDB5C).icon(Icons.MECHANICAL_TOOLS).build();
    Container MECHANICS_FIRE = Container.group(MECHANICS, "mechanics_fire").color(0xCE7806).icon(Icons.FIRE).build();
    Container MECHANICS_CART = Container.group(MECHANICS, "mechanics_cart").color(0x8D8E93).icon(Items.MINECART).build();
    Container MECHANICS_BLOCK = Container.group(MECHANICS, "mechanics_block").color(0x78B74E).icon(Blocks.GRASS_BLOCK).build();
    Container MECHANICS_PLAYER = Container.group(MECHANICS, "mechanics_player").color(0xB6896C).icon(Items.PLAYER_HEAD).build();
    Container MECHANICS_FARMING = Container.group(MECHANICS, "mechanics_farming").color(0x69B038).icon(Items.WHEAT_SEEDS).build();
    Container MECHANICS_SWIMMING = Container.group(MECHANICS, "mechanics_swimming").color(0x375DCA).icon(Items.WATER_BUCKET).build();
    Container MECHANICS_ITEMS = Container.group(MECHANICS, "mechanics_items").color(0xC3C3C3).icon(Items.IRON_PICKAXE).build();

    // Hunger

    Container HUNGER = Container.group(Category.GAMEPLAY, "hunger").color(0xFAB2B3).icon(Items.PORKCHOP).build();
    Container HUNGER_BAR = Container.group(HUNGER, "hunger_bar").color(0xD42A2A).icon(Icons.HUNGER).build();
    Container HUNGER_BAR_ALT_FOOD = Container.group(HUNGER_BAR, "hunger_bar_alt_food").color(0xB79859).icon(Items.OAK_SIGN).build();
    Container HUNGER_BAR_ALT_SATURATION = Container.group(HUNGER_BAR, "hunger_bar_alt_saturation").color(0xE56B7E).icon(Icons.HUNGER_PARTIAL).build();
    Container HUNGER_FOOD = Container.group(HUNGER, "hunger_food").color(0xDA8947).icon(Items.COOKIE).build();

    // Mob

    Container MOB = Container.group(Category.GAMEPLAY, "mob").color(0x208A1E).icon(Items.CREEPER_HEAD).build();
    Container MOB_AI = Container.group(MOB, "mob_ai").color(0xB55088).icon(Icons.BRAIN).build();
    Container MOB_ANIMAL = Container.group(MOB, "mob_animal").color(0xD96927).icon(Items.TROPICAL_FISH).build();
    Container MOB_ANIMAL_SPAWN = Container.group(MOB_ANIMAL, "mob_animal_spawn").color(0xE699E8).icon(Items.PIG_SPAWN_EGG).build();
    Container MOB_ANIMAL_SHEEP = Container.group(MOB_ANIMAL, "mob_animal_sheep").color(0xFFFFFF).icon(Blocks.WHITE_WOOL).build();
    Container MOB_DROPS = Container.group(MOB, "mob_drops").color(0xBC5932).icon(Items.LEATHER).build();
    Container MOB_DROPS_CLASSIC = Container.group(MOB_DROPS, "mob_drops_classic").color(0xCECECE).icon(Items.FEATHER).build();
    Container MOB_DROPS_MODERN = Container.group(MOB_DROPS, "mob_drops_modern").color(0xB44C18).icon(Items.ROTTEN_FLESH).build();
}