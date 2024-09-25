package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicCreativeModeItemHelper {


    public static Collection<ItemStack> GetItems() {
        var ver = VersionInt(CandyTweak.OLD_CREATIVE_INVENTORY_VERSION.get());
        return GetItemsFromVersion(ver);
    }

    // ABCDEFG
    // A = Release State: 0 (Pre-Classic), 1 (Classic/Indev), 2 (Alpha), 3 (Beta), 4 (Release). 4 by default (Release 1.2.5)
    // BC DE FG = Version: 01 02 05 by default (Release 1.2.5)
    public static int ShowAsVersion = 4010205;


    // Cursed, I'm so sorry.
    public static int VersionInt(String string) {
        int b = 0;
        int state = 1000000;
        int major = 10000;
        int minor = 100;
        int patch = 1;

             if (string.startsWith("p")) {b += state * 0;} // starts with p, we're pre classic.
        else if (string.startsWith("c")) {b += state * 1;} // starts with c, we're classic.
        else if (string.startsWith("i")) {b += state * 1;} // starts with i, we're indev.
        else if (string.startsWith("a")) {b += state * 2;} // starts with a, we're alpha.
        else if (string.startsWith("b")) {b += state * 3;} // starts with b, we're beta.
        else if (string.startsWith("r")) {b += state * 4;} // starts with r, we're release.
        else {string = "r"+string; b += state * 4;} // Assume release

        String[] splits = string.substring(1).split("[.]");

        if (splits.length >= 1) b += Integer.valueOf(splits[0]) * major;
        if (splits.length >= 2) b += Integer.valueOf(splits[1]) * minor;
        if (splits.length >= 3) b += Integer.valueOf(splits[2]) * patch;

        return b;

    }
    public static Collection<ItemStack> GetItemsFromVersion(int ver) {
        var items = new ArrayList<Item>();

        // This looks really horrific but the old minecraft versions manually did the order of blocks in the inventory

        //BLOCKS
        if (ver >= VersionInt("p0.0.1")) items.add(Items.COBBLESTONE); // Pre-classic build 2
        if (ver >= VersionInt("p0.0.0")) items.add(Items.STONE); // Pre-classic build 1
        if (ver >= VersionInt("i0.31")) items.add(Items.DIAMOND_ORE); // Indev 0.31
        if (ver >= VersionInt("c0.0.14")) items.add(Items.GOLD_ORE); // c0.0.14a
        if (ver >= VersionInt("c0.0.14")) items.add(Items.IRON_ORE); // c0.0.14a
        if (ver >= VersionInt("c0.0.14")) items.add(Items.COAL_ORE); // c0.0.14a
        if (ver >= VersionInt("1.3.1")) items.add(Items.EMERALD_ORE);
        if (ver >= VersionInt("b1.2")) items.add(Items.LAPIS_ORE); // b1.2
        if (ver >= VersionInt("a1.0.1")) items.add(Items.REDSTONE_ORE); // a1.0.1
        if (ver >= VersionInt("b1.8")) items.add(Items.STONE_BRICKS); // b1.8
        if (ver >= VersionInt("b1.8")) items.add(Items.MOSSY_STONE_BRICKS); // b1.8
        if (ver >= VersionInt("b1.8")) items.add(Items.CRACKED_STONE_BRICKS);
        if (ver >= VersionInt("1.2")) items.add(Items.CHISELED_STONE_BRICKS);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.CLAY);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.DIAMOND_BLOCK);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.GOLD_BLOCK);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.IRON_BLOCK);
        if (ver >= VersionInt("1.6.1")) items.add(Items.COAL_BLOCK);
        if (ver >= VersionInt("1.3.1")) items.add(Items.EMERALD_BLOCK);
        if (ver >= VersionInt("c0.0.12")) items.add(Items.BEDROCK);
        if (ver >= VersionInt("b1.2")) items.add(Items.LAPIS_BLOCK);
        if (ver >= VersionInt("1.5")) items.add(Items.REDSTONE_BLOCK);
        if (ver >= VersionInt("c0.26")) items.add(Items.BRICKS);
        if (ver >= VersionInt("c0.26")) items.add(Items.MOSSY_COBBLESTONE);
        if (ver >= VersionInt("c0.26")) items.add(Items.SMOOTH_STONE_SLAB);
        if (ver >= VersionInt("b1.3")) items.add(Items.SANDSTONE_SLAB);
        if (ver >= VersionInt("b1.3")) items.add(Items.OAK_SLAB);
        if (ver >= VersionInt("b1.3")) items.add(Items.COBBLESTONE_SLAB);
        if (ver >= VersionInt("b1.8")) items.add(Items.BRICK_SLAB);
        if (ver >= VersionInt("b1.8")) items.add(Items.STONE_BRICK_SLAB);
        if (ver >= VersionInt("c0.28")) items.add(Items.OBSIDIAN);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.NETHERRACK);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.SOUL_SAND);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.GLOWSTONE);
        if (ver >= VersionInt("c0.0.14")) items.add(Items.OAK_LOG);
        if (ver >= VersionInt("b1.2")) items.add(Items.SPRUCE_LOG);
        if (ver >= VersionInt("b1.2")) items.add(Items.BIRCH_LOG);
        if (ver >= VersionInt("1.2")) items.add(Items.JUNGLE_LOG);
        if (ver >= VersionInt("1.16")) items.add(Items.WARPED_STEM);
        if (ver >= VersionInt("1.16")) items.add(Items.CRIMSON_STEM);
        if (ver >= VersionInt("c0.0.14")) items.add(Items.OAK_LEAVES);
        if (ver >= VersionInt("b1.2")) items.add(Items.SPRUCE_LEAVES);
        if (ver >= VersionInt("b1.2")) items.add(Items.BIRCH_LEAVES);
        if (ver >= VersionInt("1.2")) items.add(Items.JUNGLE_LEAVES);
        if (ver >= VersionInt("1.7.1")) items.add(Items.DARK_OAK_LEAVES);
        if (ver >= VersionInt("1.7.1")) items.add(Items.ACACIA_LEAVES);
        if (ver >= VersionInt("1.16")) items.add(Items.WARPED_WART_BLOCK);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHER_WART_BLOCK);
        if (ver >= VersionInt("p0.0.1")) items.add(Items.DIRT);
        if (ver >= VersionInt("p0.0.0")) items.add(Items.GRASS_BLOCK);
        if (ver >= VersionInt("c0.0.14")) items.add(Items.SAND);
        if (ver >= VersionInt("b1.2")) items.add(Items.SANDSTONE);
        if (ver >= VersionInt("1.2.4")) items.add(Items.CHISELED_SANDSTONE);
        if (ver >= VersionInt("1.2.4")) items.add(Items.CUT_SANDSTONE);
        if (ver >= VersionInt("c0.0.14")) items.add(Items.GRAVEL);
        if (ver >= VersionInt("b1.5")) items.add(Items.COBWEB);
        if (ver >= VersionInt("p0.0.1")) items.add(Items.OAK_PLANKS);
        if (ver >= VersionInt("1.2.4")) items.add(Items.SPRUCE_PLANKS);
        if (ver >= VersionInt("1.2.4")) items.add(Items.BIRCH_PLANKS);
        if (ver >= VersionInt("1.2.4")) items.add(Items.JUNGLE_PLANKS);
        if (ver >= VersionInt("1.7.1")) items.add(Items.DARK_OAK_PLANKS);
        if (ver >= VersionInt("1.7.1")) items.add(Items.ACACIA_PLANKS);
        if (ver >= VersionInt("1.16")) items.add(Items.WARPED_PLANKS);
        if (ver >= VersionInt("1.16")) items.add(Items.CRIMSON_PLANKS);
        if (ver >= VersionInt("p0.0.2")) items.add(Items.OAK_SAPLING);
        if (ver >= VersionInt("b1.5")) items.add(Items.SPRUCE_SAPLING);
        if (ver >= VersionInt("b1.5")) items.add(Items.BIRCH_SAPLING);
        if (ver >= VersionInt("1.2")) items.add(Items.JUNGLE_SAPLING);
        if (ver >= VersionInt("1.7.1")) items.add(Items.DARK_OAK_SAPLING);
        if (ver >= VersionInt("1.7.1")) items.add(Items.ACACIA_SAPLING);
        if (ver >= VersionInt("1.16")) items.add(Items.WARPED_FUNGUS);
        if (ver >= VersionInt("1.16")) items.add(Items.CRIMSON_FUNGUS);
        if (ver >= VersionInt("b1.6")) items.add(Items.DEAD_BUSH);
        if (ver >= VersionInt("c0.0.19")) items.add(Items.SPONGE);
        if (ver >= VersionInt("a1.0.4")) items.add(Items.ICE);
        if (ver >= VersionInt("a1.0.5")) items.add(Items.SNOW_BLOCK);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.DANDELION);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.POPPY);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.BROWN_MUSHROOM);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.RED_MUSHROOM);
        if (ver >= VersionInt("a1.0.11") && ver < VersionInt("1.0.0")) items.add(Items.SUGAR_CANE);
        if (ver >= VersionInt("a1.0.6")) items.add(Items.CACTUS);
        if (ver >= VersionInt("b1.8")) items.add(Items.MELON);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.CARVED_PUMPKIN);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.JACK_O_LANTERN);
        if (ver >= VersionInt("b1.8")) items.add(Items.VINE);
        if (ver >= VersionInt("b1.8")) items.add(Items.IRON_BARS);
        if (ver >= VersionInt("b1.8")) items.add(Items.GLASS_PANE);
        if (ver >= VersionInt("b1.9")) items.add(Items.NETHER_BRICKS);
        if (ver >= VersionInt("b1.9")) items.add(Items.NETHER_BRICK_FENCE);
        if (ver >= VersionInt("b1.9")) items.add(Items.NETHER_BRICK_STAIRS);
        if (ver >= VersionInt("b1.9.4")) items.add(Items.END_STONE);
        if (ver >= VersionInt("b1.9")) items.add(Items.MYCELIUM);
        if (ver >= VersionInt("b1.9")) items.add(Items.LILY_PAD);
        if (ver >= VersionInt("b1.6")) items.add(Items.SHORT_GRASS);
        if (ver >= VersionInt("b1.6")) items.add(Items.FERN);
        if (ver >= VersionInt("i0.31.04")) items.add(Items.CHEST);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.CRAFTING_TABLE);
        if (ver >= VersionInt("c0.0.19")) items.add(Items.GLASS);
        if (ver >= VersionInt("c0.26")) items.add(Items.TNT);
        if (ver >= VersionInt("c0.26")) items.add(Items.BOOKSHELF);
        if (ver >= VersionInt("c0.0.20")) items.add(Items.WHITE_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.ORANGE_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.MAGENTA_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.LIGHT_BLUE_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.YELLOW_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.LIME_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.PINK_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.GRAY_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.LIGHT_GRAY_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.CYAN_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.PURPLE_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.BLUE_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.BROWN_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.GREEN_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.RED_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.BLACK_WOOL);
        if (ver >= VersionInt("b1.2")) items.add(Items.DISPENSER);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.FURNACE);
        if (ver >= VersionInt("b1.2")) items.add(Items.NOTE_BLOCK);
        if (ver >= VersionInt("a1.0.14")) items.add(Items.JUKEBOX);
        if (ver >= VersionInt("b1.7")) items.add(Items.STICKY_PISTON);
        if (ver >= VersionInt("b1.7")) items.add(Items.PISTON);
        if (ver >= VersionInt("a1.0.17")) items.add(Items.OAK_FENCE);
        if (ver >= VersionInt("b1.8")) items.add(Items.OAK_FENCE_GATE);
        if (ver >= VersionInt("i0.31.09")) items.add(Items.LADDER);
        if (ver >= VersionInt("i0.31.10")) items.add(Items.RAIL);
        if (ver >= VersionInt("b1.5")) items.add(Items.POWERED_RAIL);
        if (ver >= VersionInt("b1.5")) items.add(Items.DETECTOR_RAIL);
        if (ver >= VersionInt("i0.31")) items.add(Items.TORCH);
        if (ver >= VersionInt("i0.31.12")) items.add(Items.OAK_STAIRS);
        if (ver >= VersionInt("i0.31.12")) items.add(Items.COBBLESTONE_STAIRS);
        if (ver >= VersionInt("b1.8")) items.add(Items.BRICK_STAIRS);
        if (ver >= VersionInt("b1.8")) items.add(Items.STONE_BRICK_STAIRS);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.LEVER);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.STONE_PRESSURE_PLATE);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.OAK_PRESSURE_PLATE);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.REDSTONE_TORCH);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.STONE_BUTTON);
        if (ver >= VersionInt("b1.6")) items.add(Items.OAK_TRAPDOOR);
        if (ver >= VersionInt("b1.9.3")) items.add(Items.ENCHANTING_TABLE);
        if (ver >= VersionInt("1.2")) items.add(Items.REDSTONE_LAMP);

        // ITEMS
        if (ver >= VersionInt("i0.31.01")) items.add(Items.IRON_SHOVEL);
        if (ver >= VersionInt("i0.31.02")) items.add(Items.IRON_PICKAXE);
        if (ver >= VersionInt("i0.31.02")) items.add(Items.IRON_AXE);
        if (ver >= VersionInt("i0.31.02")) items.add(Items.FLINT_AND_STEEL);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.APPLE);
        if (ver >= VersionInt("i0.31.03")) items.add(Items.BOW);
        if (ver >= VersionInt("i0.31.03")) items.add(Items.ARROW);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.COAL);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.DIAMOND);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.IRON_INGOT);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.GOLD_INGOT);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_INGOT);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.IRON_SWORD);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.WOODEN_SWORD);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.WOODEN_SHOVEL);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.WOODEN_PICKAXE);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.WOODEN_AXE);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.STONE_SWORD);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.STONE_SHOVEL);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.STONE_PICKAXE);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.STONE_AXE);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.DIAMOND_SWORD);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.DIAMOND_SHOVEL);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.DIAMOND_PICKAXE);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.DIAMOND_AXE);
        if (ver >= VersionInt("i0.31.05")) items.add(Items.STICK);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.BOWL);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.MUSHROOM_STEW);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.GOLDEN_SWORD);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.GOLDEN_SHOVEL);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.GOLDEN_PICKAXE);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.GOLDEN_AXE);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_SWORD);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_SHOVEL);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_PICKAXE);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_AXE);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.STRING);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.FEATHER);
        if (ver >= VersionInt("i0.31.06")) items.add(Items.GUNPOWDER);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.WOODEN_HOE);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.STONE_HOE);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.IRON_HOE);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.DIAMOND_HOE);
        if (ver >= VersionInt("i0.31.07"))  items.add(Items.GOLDEN_HOE);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.WHEAT_SEEDS);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.WHEAT);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.BREAD);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.LEATHER_HELMET);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.LEATHER_CHESTPLATE);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.LEATHER_LEGGINGS);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.LEATHER_BOOTS);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.CHAINMAIL_HELMET);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.CHAINMAIL_CHESTPLATE);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.CHAINMAIL_LEGGINGS);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.CHAINMAIL_BOOTS);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.IRON_HELMET);
        if (ver >= VersionInt("i0.31.01")) items.add(Items.IRON_CHESTPLATE);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.IRON_LEGGINGS);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.IRON_BOOTS);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.DIAMOND_HELMET);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.DIAMOND_CHESTPLATE);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.DIAMOND_LEGGINGS);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.DIAMOND_BOOTS);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.GOLDEN_HELMET);
        if (ver >= VersionInt("i0.31.07")) items.add(Items.GOLDEN_CHESTPLATE);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.GOLDEN_LEGGINGS);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.GOLDEN_BOOTS);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_HELMET);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_CHESTPLATE);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_LEGGINGS);
        if (ver >= VersionInt("1.16")) items.add(Items.NETHERITE_BOOTS);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.FLINT);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.PORKCHOP);
        if (ver >= VersionInt("i0.31.08")) items.add(Items.COOKED_PORKCHOP);
        if (ver >= VersionInt("i0.31.09")) items.add(Items.PAINTING);
        if (ver >= VersionInt("i0.31.09")) items.add(Items.ENCHANTED_GOLDEN_APPLE);
        if (ver >= VersionInt("i0.31.09")) items.add(Items.OAK_SIGN);
        if (ver >= VersionInt("i0.31.09")) items.add(Items.OAK_DOOR);
        if (ver >= VersionInt("i0.31.10")) items.add(Items.BUCKET);
        if (ver >= VersionInt("i0.31.10")) items.add(Items.WATER_BUCKET);
        if (ver >= VersionInt("i0.31.10")) items.add(Items.LAVA_BUCKET);
        if (ver >= VersionInt("i0.31.10")) items.add(Items.MINECART);
        if (ver >= VersionInt("i0.31.11")) items.add(Items.SADDLE);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.IRON_DOOR);
        if (ver >= VersionInt("a1.0.1")) items.add(Items.REDSTONE);
        if (ver >= VersionInt("a1.0.5")) items.add(Items.SNOWBALL);
        if (ver >= VersionInt("a1.0.6")) items.add(Items.OAK_BOAT);
        if (ver >= VersionInt("a1.0.8")) items.add(Items.LEATHER);
        if (ver >= VersionInt("a1.0.8")) items.add(Items.MILK_BUCKET);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.BRICK);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.CLAY_BALL);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.SUGAR_CANE);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.PAPER);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.BOOK);
        if (ver >= VersionInt("a1.0.11")) items.add(Items.SLIME_BALL);
        if (ver >= VersionInt("a1.0.14")) items.add(Items.CHEST_MINECART);
        if (ver >= VersionInt("a1.0.14")) items.add(Items.FURNACE_MINECART);
        if (ver >= VersionInt("a1.0.14")) items.add(Items.EGG);
        if (ver >= VersionInt("a1.1.0")) items.add(Items.COMPASS);
        if (ver >= VersionInt("a1.1.1")) items.add(Items.FISHING_ROD);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.CLOCK);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.GLOWSTONE_DUST);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.COD);
        if (ver >= VersionInt("a1.2.0")) items.add(Items.COOKED_COD);
        if (ver >= VersionInt("b1.2")) items.add(Items.INK_SAC);
        if (ver >= VersionInt("b1.2")) items.add(Items.BONE);
        if (ver >= VersionInt("b1.2")) items.add(Items.SUGAR);
        if (ver >= VersionInt("b1.2")) items.add(Items.CAKE);
        if (ver >= VersionInt("b1.3")) items.add(Items.RED_BED);
        if (ver >= VersionInt("b1.3")) items.add(Items.REPEATER);
        if (ver >= VersionInt("b1.4")) items.add(Items.COOKIE);
        if (ver >= VersionInt("b1.6.3")) items.add(Items.MAP);
        if (ver >= VersionInt("b1.7")) items.add(Items.SHEARS);
        if (ver >= VersionInt("b1.8")) items.add(Items.MELON_SLICE);
        if (ver >= VersionInt("b1.8")) items.add(Items.PUMPKIN_SEEDS);
        if (ver >= VersionInt("b1.8")) items.add(Items.MELON_SEEDS);
        if (ver >= VersionInt("b1.8")) items.add(Items.BEEF);
        if (ver >= VersionInt("b1.8")) items.add(Items.COOKED_BEEF);
        if (ver >= VersionInt("b1.8")) items.add(Items.CHICKEN);
        if (ver >= VersionInt("b1.8")) items.add(Items.COOKED_CHICKEN);
        if (ver >= VersionInt("b1.8")) items.add(Items.ROTTEN_FLESH);
        if (ver >= VersionInt("b1.8")) items.add(Items.ENDER_PEARL);
        if (ver >= VersionInt("b1.9")) items.add(Items.BLAZE_ROD);
        if (ver >= VersionInt("b1.9")) items.add(Items.GHAST_TEAR);
        if (ver >= VersionInt("b1.9")) items.add(Items.GOLD_NUGGET);
        if (ver >= VersionInt("b1.9")) items.add(Items.NETHER_WART);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.GLASS_BOTTLE);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.SPIDER_EYE);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.FERMENTED_SPIDER_EYE);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.BLAZE_POWDER);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MAGMA_CREAM);
        if (ver >= VersionInt("b1.9.3")) items.add(Items.BREWING_STAND);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.CAULDRON);
        if (ver >= VersionInt("b1.9.3")) items.add(Items.ENDER_EYE);
        if (ver >= VersionInt("b1.9.4")) items.add(Items.GLISTERING_MELON_SLICE);
        if (ver >= VersionInt("1.2.1")) items.add(Items.EXPERIENCE_BOTTLE);
        if (ver >= VersionInt("1.2.1")) items.add(Items.FIRE_CHARGE);

        //DISCS

        if (ver >= VersionInt("a1.0.14")) items.add(Items.MUSIC_DISC_13);
        if (ver >= VersionInt("a1.0.14")) items.add(Items.MUSIC_DISC_CAT);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_BLOCKS);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_CHIRP);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_FAR);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_MALL);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_MELLOHI);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_STAL);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_STRAD);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_WARD);
        if (ver >= VersionInt("b1.9.2")) items.add(Items.MUSIC_DISC_11);
        if (ver >= VersionInt("1.4.3")) items.add(Items.MUSIC_DISC_WAIT);

        if (CandyTweak.OLD_CREATIVE_INVENTORY_ALWAYS_SHOW_MODERN_ITEMS.get()) {
            for ( ItemStack itemstack : CreativeModeTabs.searchTab().getDisplayItems()) {
                Item item = itemstack.getItem();
                if (!items.contains(item) && itemstack.is(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                    items.add(item);
            }
        }

        //DYE

        if (ver >= VersionInt("b1.2")) items.add(Items.RED_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.GREEN_DYE);
        if (ver >= VersionInt("b1.2") && ver < VersionInt("1.14")) items.add(Items.COCOA_BEANS);
        if (ver >= VersionInt("1.14")) items.add(Items.BROWN_DYE);
        if (ver >= VersionInt("b1.2") && ver < VersionInt("1.14")) items.add(Items.LAPIS_LAZULI);
        if (ver >= VersionInt("1.14")) items.add(Items.BLUE_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.PURPLE_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.CYAN_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.LIGHT_GRAY_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.GRAY_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.PINK_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.LIME_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.YELLOW_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.LIGHT_BLUE_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.MAGENTA_DYE);
        if (ver >= VersionInt("b1.2")) items.add(Items.ORANGE_DYE);
        if (ver >= VersionInt("1.14")) items.add(Items.WHITE_DYE);
        if (ver >= VersionInt("1.14")) items.add(Items.BLACK_DYE);

        if (CandyTweak.OLD_CREATIVE_INVENTORY_ALWAYS_SHOW_MODERN_ITEMS.get()) {
            for (ItemStack itemstack : CreativeModeTabs.searchTab().getDisplayItems()) {
                Item item = itemstack.getItem();
                if (!items.contains(item) && (item instanceof DyeItem))
                    items.add(item);
            }
        }

        //MISC
        items.add(Items.BONE_MEAL);

        //EGGS

        if (ver >= VersionInt("1.1")) items.add(Items.MOOSHROOM_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.OCELOT_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.CREEPER_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.SKELETON_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.SPIDER_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.ZOMBIE_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.SLIME_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.GHAST_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.VILLAGER_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.ENDERMAN_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.PIG_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.CAVE_SPIDER_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.SHEEP_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.SILVERFISH_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.COW_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.BLAZE_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.CHICKEN_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.MAGMA_CUBE_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.SQUID_SPAWN_EGG);
        if (ver >= VersionInt("1.1")) items.add(Items.WOLF_SPAWN_EGG);
        if (CandyTweak.OLD_CREATIVE_INVENTORY_ALWAYS_SHOW_MODERN_ITEMS.get()) {
            for (SpawnEggItem egg : SpawnEggItem.eggs()) {
                if (!items.contains(egg))
                    items.add(egg);
            }
        }

        //Everything Else

        if (CandyTweak.OLD_CREATIVE_INVENTORY_ALWAYS_SHOW_MODERN_ITEMS.get()) {
            for ( ItemStack itemstack : CreativeModeTabs.searchTab().getDisplayItems()) {
                Item item = itemstack.getItem();
                if (!items.contains(item) && !(item instanceof SpawnEggItem) && !(item instanceof PotionItem))
                    items.add(item);
            }
        }

        var itemstacks = new ArrayList<ItemStack>();

        for (Item item : items) {
            itemstacks.add(new ItemStack(item));
        }

        return itemstacks;
    }
}
