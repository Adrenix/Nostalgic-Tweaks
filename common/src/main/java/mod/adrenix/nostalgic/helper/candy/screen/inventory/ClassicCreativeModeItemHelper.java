package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicCreativeModeItemHelper {


    public static Collection<ItemStack> GetItems() {
        return GetItemsFromVersion(ShowAsVersion);
    }

    // ABCDEFG
    // A = Release State: 0 (Pre-Classic), 1 (Classic/Indev), 2 (Alpha), 3 (Beta), 4 (Release). 4 by default (Release 1.2.5)
    // BCDEFG = Version: 010205 by default (Release 1.2.5)
    public static int ShowAsVersion = 3010205;

    public static Collection<ItemStack> GetItemsFromVersion(int ver) {
        var items = new ArrayList<Item>();

        // This looks really horrific but the old minecraft versions manually did the order of blocks in the inventory

        //BLOCKS
        if (ver >= 0000001) items.add(Items.COBBLESTONE); // Pre-classic build 2
        if (ver >= 0000000) items.add(Items.STONE); // Pre-classic build 1
        if (ver >= 1003100) items.add(Items.DIAMOND_ORE); // Indev 0.31
        if (ver >= 1000014) items.add(Items.GOLD_ORE); // c0.0.14a
        if (ver >= 1000014) items.add(Items.IRON_ORE); // c0.0.14a
        if (ver >= 1000014) items.add(Items.COAL_ORE); // c0.0.14a
        if (ver >= 3010200) items.add(Items.LAPIS_ORE); // b1.2
        if (ver >= 2010001) items.add(Items.REDSTONE_ORE); // a1.0.1
        if (ver >= 3010800) items.add(Items.STONE_BRICKS);
        if (ver >= 3010800) items.add(Items.MOSSY_STONE_BRICKS);
        if (ver >= 3010800) items.add(Items.CRACKED_STONE_BRICKS);
        if (ver >= 4010200) items.add(Items.CHISELED_STONE_BRICKS);
        if (ver >= 2010011) items.add(Items.CLAY);
        if (ver >= 1003100) items.add(Items.DIAMOND_BLOCK);
        if (ver >= 1000020) items.add(Items.GOLD_BLOCK);
        if (ver >= 1000020) items.add(Items.IRON_BLOCK);
        if (ver >= 1000012) items.add(Items.BEDROCK);
        if (ver >= 3010200) items.add(Items.LAPIS_BLOCK);
        if (ver >= 10260) items.add(Items.BRICKS);
        if (ver >= 10260) items.add(Items.MOSSY_COBBLESTONE);
        if (ver >= 10260) items.add(Items.SMOOTH_STONE_SLAB);
        if (ver >= 31300) items.add(Items.SANDSTONE_SLAB);
        if (ver >= 31300) items.add(Items.OAK_SLAB);
        if (ver >= 31300) items.add(Items.COBBLESTONE_SLAB);
        if (ver >= 31800) items.add(Items.BRICK_SLAB);
        if (ver >= 31800) items.add(Items.STONE_BRICK_SLAB);
        if (ver >= 10280) items.add(Items.OBSIDIAN);
        if (ver >= 21200) items.add(Items.NETHERRACK);
        if (ver >= 21200) items.add(Items.SOUL_SAND);
        if (ver >= 21200) items.add(Items.GLOWSTONE);
        if (ver >= 1000014) items.add(Items.OAK_LOG);
        if (ver >= 31200) items.add(Items.SPRUCE_LOG);
        if (ver >= 31200) items.add(Items.BIRCH_LOG);
        if (ver >= 41200) items.add(Items.JUNGLE_LOG);
        if (ver >= 1000014) items.add(Items.OAK_LEAVES);
        if (ver >= 31200) items.add(Items.SPRUCE_LEAVES);
        if (ver >= 31200) items.add(Items.BIRCH_LEAVES);
        if (ver >= 41200) items.add(Items.JUNGLE_LEAVES);
        if (ver >= 0000001) items.add(Items.DIRT);
        if (ver >= 0000000) items.add(Items.GRASS_BLOCK);
        if (ver >= 1000014) items.add(Items.SAND);
        if (ver >= 31200) items.add(Items.SANDSTONE);
        if (ver >= 41240) items.add(Items.CHISELED_SANDSTONE);
        if (ver >= 41240) items.add(Items.CUT_SANDSTONE);
        if (ver >= 10012) items.add(Items.GRAVEL);
        if (ver >= 31500) items.add(Items.COBWEB);
        if (ver >= 00001) items.add(Items.OAK_PLANKS);
        if (ver >= 41240) items.add(Items.SPRUCE_PLANKS);
        if (ver >= 41240) items.add(Items.BIRCH_PLANKS);
        if (ver >= 41240) items.add(Items.JUNGLE_PLANKS);
        if (ver >= 00002) items.add(Items.OAK_SAPLING);
        if (ver >= 31500) items.add(Items.SPRUCE_SAPLING);
        if (ver >= 31500) items.add(Items.BIRCH_SAPLING);
        if (ver >= 41200) items.add(Items.JUNGLE_SAPLING);
        if (ver >= 31600) items.add(Items.DEAD_BUSH);
        if (ver >= 10019) items.add(Items.SPONGE);
        if (ver >= 21040) items.add(Items.ICE);
        if (ver >= 21050) items.add(Items.SNOW_BLOCK);
        if (ver >= 10020) items.add(Items.DANDELION);
        if (ver >= 10020) items.add(Items.POPPY);
        if (ver >= 10020) items.add(Items.BROWN_MUSHROOM);
        if (ver >= 10020) items.add(Items.RED_MUSHROOM);
        if (ver >= 21060) items.add(Items.CACTUS);
        if (ver >= 31800) items.add(Items.MELON);
        if (ver >= 21200) items.add(Items.CARVED_PUMPKIN);
        if (ver >= 21200) items.add(Items.JACK_O_LANTERN);
        if (ver >= 31800) items.add(Items.VINE);
        if (ver >= 31800) items.add(Items.IRON_BARS);
        if (ver >= 31800) items.add(Items.GLASS_PANE);
        if (ver >= 31900) items.add(Items.NETHER_BRICKS);
        if (ver >= 31900) items.add(Items.NETHER_BRICK_FENCE);
        if (ver >= 31900) items.add(Items.NETHER_BRICK_STAIRS);
        if (ver >= 31940) items.add(Items.END_STONE);
        if (ver >= 31900) items.add(Items.MYCELIUM);
        if (ver >= 31900) items.add(Items.LILY_PAD);
        if (ver >= 31600) items.add(Items.SHORT_GRASS);
        if (ver >= 31600) items.add(Items.FERN);
        if (ver >= 10310) items.add(Items.CHEST);
        if (ver >= 10310) items.add(Items.CRAFTING_TABLE);
        if (ver >= 10019) items.add(Items.GLASS);
        if (ver >= 10260) items.add(Items.TNT);
        if (ver >= 10260) items.add(Items.BOOKSHELF);
        if (ver >= 10200) items.add(Items.WHITE_WOOL);
        if (ver >= 31200) items.add(Items.ORANGE_WOOL);
        if (ver >= 31200) items.add(Items.MAGENTA_WOOL);
        if (ver >= 31200) items.add(Items.LIGHT_BLUE_WOOL);
        if (ver >= 31200) items.add(Items.YELLOW_WOOL);
        if (ver >= 31200) items.add(Items.LIME_WOOL);
        if (ver >= 31200) items.add(Items.PINK_WOOL);
        if (ver >= 31200) items.add(Items.GRAY_WOOL);
        if (ver >= 31200) items.add(Items.LIGHT_GRAY_WOOL);
        if (ver >= 31200) items.add(Items.CYAN_WOOL);
        if (ver >= 31200) items.add(Items.PURPLE_WOOL);
        if (ver >= 31200) items.add(Items.BLUE_WOOL);
        if (ver >= 31200) items.add(Items.BROWN_WOOL);
        if (ver >= 31200) items.add(Items.GREEN_WOOL);
        if (ver >= 31200) items.add(Items.RED_WOOL);
        if (ver >= 31200) items.add(Items.BLACK_WOOL);
        if (ver >= 31200) items.add(Items.DISPENSER);
        if (ver >= 11219) items.add(Items.FURNACE);
        if (ver >= 31200) items.add(Items.NOTE_BLOCK);
        if (ver >= 21014) items.add(Items.JUKEBOX);
        items.add(Items.STICKY_PISTON);
        items.add(Items.PISTON);
        items.add(Items.OAK_FENCE);
        items.add(Items.OAK_FENCE_GATE);
        if (ver >= 11607) items.add(Items.LADDER);
        if (ver >= 11618) items.add(Items.RAIL);
        items.add(Items.POWERED_RAIL);
        items.add(Items.DETECTOR_RAIL);
        items.add(Items.TORCH);
        if (ver >= 11629) items.add(Items.OAK_STAIRS);
        if (ver >= 11629) items.add(Items.COBBLESTONE_STAIRS);
        items.add(Items.BRICK_STAIRS);
        items.add(Items.STONE_BRICK_STAIRS);
        if (ver >= 21010) items.add(Items.LEVER);
        if (ver >= 21010) items.add(Items.STONE_PRESSURE_PLATE);
        if (ver >= 21010) items.add(Items.OAK_PRESSURE_PLATE);
        if (ver >= 21010) items.add(Items.REDSTONE_TORCH);
        if (ver >= 21010) items.add(Items.STONE_BUTTON);
        items.add(Items.OAK_TRAPDOOR);
        items.add(Items.ENCHANTING_TABLE);
        items.add(Items.REDSTONE_LAMP);

        // ITEMS
        items.add(Items.IRON_SHOVEL);
        items.add(Items.IRON_PICKAXE);
        items.add(Items.IRON_AXE);
        items.add(Items.FLINT_AND_STEEL);
        items.add(Items.APPLE);
        items.add(Items.BOW);
        items.add(Items.ARROW);
        items.add(Items.COAL);
        items.add(Items.DIAMOND);
        items.add(Items.IRON_INGOT);
        items.add(Items.GOLD_INGOT);
        items.add(Items.IRON_SWORD);
        items.add(Items.WOODEN_SWORD);
        items.add(Items.WOODEN_SHOVEL);
        items.add(Items.WOODEN_PICKAXE);
        items.add(Items.WOODEN_AXE);
        items.add(Items.STONE_SWORD);
        items.add(Items.STONE_SHOVEL);
        items.add(Items.STONE_PICKAXE);
        items.add(Items.STONE_AXE);
        items.add(Items.DIAMOND_SWORD);
        items.add(Items.DIAMOND_SHOVEL);
        items.add(Items.DIAMOND_PICKAXE);
        items.add(Items.DIAMOND_AXE);
        items.add(Items.STICK);
        items.add(Items.BOWL);
        items.add(Items.MUSHROOM_STEW);
        items.add(Items.GOLDEN_SWORD);
        items.add(Items.GOLDEN_SHOVEL);
        items.add(Items.GOLDEN_PICKAXE);
        items.add(Items.GOLDEN_AXE);
        items.add(Items.STRING);
        items.add(Items.FEATHER);
        items.add(Items.GUNPOWDER);
        items.add(Items.WOODEN_HOE);
        items.add(Items.STONE_HOE);
        items.add(Items.IRON_HOE);
        items.add(Items.DIAMOND_HOE);
        items.add(Items.GOLDEN_HOE);
        items.add(Items.WHEAT_SEEDS);
        items.add(Items.WHEAT);
        items.add(Items.BREAD);
        items.add(Items.LEATHER_HELMET);
        items.add(Items.LEATHER_CHESTPLATE);
        items.add(Items.LEATHER_LEGGINGS);
        items.add(Items.LEATHER_BOOTS);
        items.add(Items.CHAINMAIL_HELMET);
        items.add(Items.CHAINMAIL_CHESTPLATE);
        items.add(Items.CHAINMAIL_LEGGINGS);
        items.add(Items.CHAINMAIL_BOOTS);
        items.add(Items.IRON_HELMET);
        items.add(Items.IRON_CHESTPLATE);
        items.add(Items.IRON_LEGGINGS);
        items.add(Items.IRON_BOOTS);
        items.add(Items.DIAMOND_HELMET);
        items.add(Items.DIAMOND_CHESTPLATE);
        items.add(Items.DIAMOND_LEGGINGS);
        items.add(Items.DIAMOND_BOOTS);
        items.add(Items.GOLDEN_HELMET);
        items.add(Items.GOLDEN_CHESTPLATE);
        items.add(Items.GOLDEN_LEGGINGS);
        items.add(Items.GOLDEN_BOOTS);
        items.add(Items.FLINT);
        items.add(Items.PORKCHOP);
        items.add(Items.COOKED_PORKCHOP);
        items.add(Items.PAINTING);
        items.add(Items.ENCHANTED_GOLDEN_APPLE);
        if (ver >= 11607) items.add(Items.OAK_SIGN);
        if (ver >= 11607) items.add(Items.OAK_DOOR);
        items.add(Items.BUCKET);
        items.add(Items.WATER_BUCKET);
        items.add(Items.LAVA_BUCKET);
        items.add(Items.MINECART);
        items.add(Items.SADDLE);
        items.add(Items.IRON_DOOR);
        items.add(Items.REDSTONE);
        items.add(Items.SNOWBALL);
        items.add(Items.OAK_BOAT);
        items.add(Items.LEATHER);
        items.add(Items.MILK_BUCKET);
        items.add(Items.BRICK);
        items.add(Items.CLAY_BALL);
        items.add(Items.SUGAR_CANE);
        items.add(Items.PAPER);
        items.add(Items.BOOK);
        items.add(Items.SLIME_BALL);
        items.add(Items.CHEST_MINECART);
        items.add(Items.FURNACE_MINECART);
        items.add(Items.EGG);
        items.add(Items.COMPASS);
        items.add(Items.FISHING_ROD);
        items.add(Items.CLOCK);
        items.add(Items.GLOWSTONE_DUST);
        items.add(Items.COD);
        items.add(Items.COOKED_COD);
        items.add(Items.INK_SAC);
        items.add(Items.BONE);
        items.add(Items.SUGAR);
        items.add(Items.CAKE);
        items.add(Items.RED_BED);
        items.add(Items.REPEATER);
        items.add(Items.COOKIE);
        items.add(Items.MAP);
        items.add(Items.SHEARS);
        items.add(Items.MELON_SLICE);
        items.add(Items.PUMPKIN_SEEDS);
        items.add(Items.MELON_SEEDS);
        items.add(Items.BEEF);
        items.add(Items.COOKED_BEEF);
        items.add(Items.CHICKEN);
        items.add(Items.COOKED_CHICKEN);
        items.add(Items.ROTTEN_FLESH);
        items.add(Items.ENDER_PEARL);
        items.add(Items.BLAZE_ROD);
        items.add(Items.GHAST_TEAR);
        items.add(Items.GOLD_NUGGET);
        items.add(Items.NETHER_WART);
        items.add(Items.GLASS_BOTTLE);
        items.add(Items.SPIDER_EYE);
        items.add(Items.FERMENTED_SPIDER_EYE);
        items.add(Items.BLAZE_POWDER);
        items.add(Items.MAGMA_CREAM);
        items.add(Items.BREWING_STAND);
        items.add(Items.CAULDRON);
        items.add(Items.ENDER_EYE);
        items.add(Items.GLISTERING_MELON_SLICE);
        items.add(Items.EXPERIENCE_BOTTLE);
        items.add(Items.FIRE_CHARGE);

        //DISCS
        for ( ItemStack itemstack : CreativeModeTabs.searchTab().getDisplayItems()) {
            Item item = itemstack.getItem();
            if (!items.contains(item) && itemstack.is(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                items.add(item);
        }

        //DYE
        for ( ItemStack itemstack : CreativeModeTabs.searchTab().getDisplayItems()) {
            Item item = itemstack.getItem();
            if (!items.contains(item) && (item instanceof DyeItem))
                items.add(item);
        }

        //MISC
        items.add(Items.BONE_MEAL);

        //EGGS
        for ( SpawnEggItem egg : SpawnEggItem.eggs()) {
            items.add(egg);
        }

        for ( ItemStack itemstack : CreativeModeTabs.searchTab().getDisplayItems()) {
            Item item = itemstack.getItem();
            if (!items.contains(item) && !(item instanceof SpawnEggItem) && !(item instanceof PotionItem))
                items.add(item);
        }

        var itemstacks = new ArrayList<ItemStack>();

        for (Item item : items) {
            itemstacks.add(new ItemStack(item));
        }

        return itemstacks;
    }
}
