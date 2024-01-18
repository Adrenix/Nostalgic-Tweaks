package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

// @formatter:off
public interface CandyGroup
{
    // Block

    Container BLOCK = Container.group(Category.EYE_CANDY, "block").color(0x98C767).icon(Blocks.GRASS_BLOCK).build();
    Container BLOCK_OUTLINE = Container.group(BLOCK, "block_outline").color(0x8A99AD).icon(Icons.HITBOX_OUTLINE).build();
    Container BLOCK_OUTLINE_AESTHETIC = Container.group(BLOCK_OUTLINE, "block_outline_aesthetic").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container BLOCK_OVERLAY = Container.group(BLOCK, "block_overlay").color(0xFFFFFF).icon(Icons.HITBOX_OVERLAY).build();
    Container BLOCK_OVERLAY_COLOR = Container.group(BLOCK_OVERLAY, "block_overlay_color").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container BLOCK_OVERLAY_ANIMATION = Container.group(BLOCK_OVERLAY, "block_overlay_animation").color(0x00AEDD).icon(Icons.PLAYER_CONTROLS).build();
    Container BLOCK_CHEST = Container.group(BLOCK, "block_chest").color(0xFFB444).icon(Blocks.CHEST).build();
    Container BLOCK_TORCH = Container.group(BLOCK, "block_torch").color(0xFFD800).icon(Items.TORCH).build();

    // Interface

    Container INTERFACE = Container.group(Category.EYE_CANDY, "interface").color(0xFFFFFF).icon(Icons.LIST).build();
    Container INTERFACE_GUI = Container.group(INTERFACE, "interface_gui").color(0xE0E0E0).icon(Icons.BUTTON).build();
    Container INTERFACE_ANVIL = Container.group(INTERFACE, "interface_anvil").color(0x969696).icon(Blocks.ANVIL).build();
    Container INTERFACE_CHAT = Container.group(INTERFACE, "interface_chat").color(0xFFFFFF).icon(Icons.CHAT).build();
    Container INTERFACE_CRAFTING = Container.group(INTERFACE, "interface_crafting").color(0xF38A47).icon(Blocks.CRAFTING_TABLE).build();
    Container INTERFACE_DEBUG = Container.group(INTERFACE, "interface_debug").color(0xFB4A4C).icon(Icons.BUG).build();
    Container INTERFACE_DEBUG_COLOR = Container.group(INTERFACE_DEBUG, "interface_debug_color").color(0xFFD800).icon(Icons.BUG_COLOR).build();
    Container INTERFACE_DEBUG_EXTRA = Container.group(INTERFACE_DEBUG, "interface_debug_extra").color(0x8EFF5E).icon(Icons.BUG_EXTRA).build();
    Container INTERFACE_DEBUG_CHART = Container.group(INTERFACE_DEBUG, "interface_debug_chart").color(0x886AE2).icon(Icons.DEBUG_CHART).build();
    Container INTERFACE_FURNACE = Container.group(INTERFACE, "interface_furnace").color(0x8F8F90).icon(Blocks.FURNACE).build();
    Container INTERFACE_INVENTORY = Container.group(INTERFACE, "interface_inventory").color(0xB6896C).icon(Items.PLAYER_HEAD).build();
    Container INTERFACE_LOADING = Container.group(INTERFACE, "interface_loading").color(0xE22837).icon(Icons.MOJANG).build();
    Container INTERFACE_PAUSE = Container.group(INTERFACE, "interface_pause").color(0xFFFFA0).icon(Icons.PAUSE).build();
    Container INTERFACE_TITLE = Container.group(INTERFACE, "interface_title").color(0xFFFF00).icon(Icons.TEXT).build();
    Container INTERFACE_TITLE_BUTTON = Container.group(INTERFACE_TITLE, "interface_title_button").color(0xE0E0E0).icon(Icons.BUTTON).build();
    Container INTERFACE_TITLE_LOGO = Container.group(INTERFACE_TITLE, "interface_title_logo").color(0xA9A7A6).icon(Blocks.STONE).build();
    Container INTERFACE_TITLE_TEXT = Container.group(INTERFACE_TITLE, "interface_title_text").color(0xB79859).icon(Items.OAK_SIGN).build();
    Container INTERFACE_TOOLTIP = Container.group(INTERFACE, "interface_tooltip").color(0x98D0E7).icon(Icons.TOOLTIP).build();
    Container INTERFACE_TOOLTIP_PARTS = Container.group(INTERFACE_TOOLTIP, "interface_tooltip_parts").color(0xFFDB5C).icon(Icons.SCREWDRIVER).build();
    Container INTERFACE_VERSION = Container.group(INTERFACE, "interface_version").color(0xB7603E).icon(Items.WRITABLE_BOOK).build();
    Container INTERFACE_WINDOW = Container.group(INTERFACE, "interface_window").color(0xFFFFFF).icon(Blocks.GLASS_PANE).build();

    // Item

    Container ITEM = Container.group(Category.EYE_CANDY, "item").color(0x8CF4E2).icon(Items.DIAMOND).build();
    Container ITEM_FLAT = Container.group(ITEM, "item_flat").color(0xFFFFFF).icon(Items.PAPER).build();
    Container ITEM_DISPLAY = Container.group(ITEM, "item_display").color(0xBC9862).icon(Items.ITEM_FRAME).build();
    Container ITEM_MERGING = Container.group(ITEM, "item_merging").color(0x969696).icon(Blocks.HOPPER).build();

    // Lighting

    Container LIGHTING = Container.group(Category.EYE_CANDY, "lighting").color(0xFEDB5B).icon(Icons.YELLOW_LIGHT).build();
    Container LIGHTING_BLOCK = Container.group(LIGHTING, "lighting_block").color(0xF4EFE9).icon(Icons.GRAY_LIGHT).build();
    Container LIGHTING_WORLD = Container.group(LIGHTING, "lighting_world").color(0xCDF7F8).icon(Icons.BLUE_LIGHT).build();
    Container LIGHTING_WORLD_SHADER = Container.group(LIGHTING_WORLD, "lighting_world_shader").color(0xF5967A).icon(Icons.RED_LIGHT).build();

    // Particle

    Container PARTICLE = Container.group(Category.EYE_CANDY, "particle").color(0x00C94D).icon(Icons.PARTICLES).build();
    Container PARTICLE_ATTACK = Container.group(PARTICLE, "particle_attack").color(0xE0E0E0).icon(Items.IRON_SWORD).build();
    Container PARTICLE_BLOCK = Container.group(PARTICLE, "particle_block").color(0x98C767).icon(Blocks.GRASS_BLOCK).build();
    Container PARTICLE_EXPLOSION = Container.group(PARTICLE, "particle_explosion").color(0xE63D14).icon(Blocks.TNT).build();
    Container PARTICLE_PLAYER = Container.group(PARTICLE, "particle_player").color(0xB6896C).icon(Items.PLAYER_HEAD).build();

    // World

    Container WORLD = Container.group(Category.EYE_CANDY, "world").color(0x46A2EE).icon(Icons.FLAT_EARTH).build();
    Container WORLD_FOG = Container.group(WORLD, "world_fog").color(0xFFFFFF).icon(Icons.FOG_EARTH).build();
    Container WORLD_FOG_CUSTOM = Container.group(WORLD_FOG, "world_fog_custom").color(0xDFADD5).icon(Icons.PINK_FOG).build();
    Container WORLD_FOG_WATER = Container.group(WORLD_FOG, "world_fog_water").color(0x8ED1FF).icon(Icons.WATER_FOG).build();
    Container WORLD_SKY = Container.group(WORLD, "world_sky").color(0xFEDB5B).icon(Icons.SKY).build();
    Container WORLD_SKY_CUSTOM = Container.group(WORLD_SKY, "world_sky_custom").color(0xEBC8E3).icon(Icons.PINK_SKY).build();
    Container WORLD_VOID = Container.group(WORLD, "world_void").color(0x969696).icon(Blocks.BEDROCK).build();
    Container WORLD_VOID_FOG = Container.group(WORLD_VOID, "world_void_fog").color(0xFFFFFF).icon(Icons.VOID_FOG).build();
    Container WORLD_VOID_SKY = Container.group(WORLD_VOID, "world_void_sky").color(0xF6BB2F).icon(Icons.VOID_SKY).build();
}
