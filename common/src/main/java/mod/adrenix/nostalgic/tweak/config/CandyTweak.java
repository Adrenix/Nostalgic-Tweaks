package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.TweakCondition;
import mod.adrenix.nostalgic.tweak.TweakIssue;
import mod.adrenix.nostalgic.tweak.container.group.CandyGroup;
import mod.adrenix.nostalgic.tweak.enums.*;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedHashSet;

// @formatter:off
public interface CandyTweak
{
    // Block

    TweakFlag DISABLE_FLOWER_OFFSET = TweakFlag.client(true, CandyGroup.BLOCK).newForUpdate().reloadChunks().build();
    TweakFlag DISABLE_ALL_OFFSET = TweakFlag.client(false, CandyGroup.BLOCK).newForUpdate().reloadChunks().build();
    TweakEnum<MissingTexture> OLD_MISSING_TEXTURE = TweakEnum.client(MissingTexture.MODERN, CandyGroup.BLOCK).newForUpdate().reloadResources().build();

    /**
     * Generates the default ambient occlusion blocks.
     */
    private static ItemSet defaultAmbientOcclusion()
    {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.add(ItemCommonUtil.getResourceKey(Blocks.SOUL_SAND));
        set.add(ItemCommonUtil.getResourceKey(Blocks.POWDER_SNOW));
        set.add(ItemCommonUtil.getResourceKey(Blocks.COMPOSTER));
        set.add(ItemCommonUtil.getResourceKey(Blocks.PISTON));

        return new ItemSet(ItemRule.ONLY_BLOCKS).startWith(set);
    }

    TweakItemSet AMBIENT_OCCLUSION_BLOCKS = TweakItemSet.client(defaultAmbientOcclusion(), CandyGroup.BLOCK).newForUpdate().reloadChunks().build();

    // Hitbox Outlines

    TweakFlag OLD_STAIR_OUTLINE = TweakFlag.client(true, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    TweakFlag OLD_FENCE_OUTLINE = TweakFlag.client(true, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    TweakFlag OLD_SLAB_OUTLINE = TweakFlag.client(false, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    TweakFlag OLD_WALL_OUTLINE = TweakFlag.client(true, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    TweakItemSet OLD_BLOCK_OUTLINES = TweakItemSet.client(new ItemSet(ItemRule.ONLY_BLOCKS), CandyGroup.BLOCK_OUTLINE).icon(Icons.BLOCK_OUTLINE).newForUpdate().build();

    // Chests

    TweakFlag OLD_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).modIssues(TweakIssue.OPTIFINE).reloadResources().build();
    TweakFlag OLD_CHEST_VOXEL = TweakFlag.server(false, CandyGroup.BLOCK_CHEST).reloadChunks().warningTag().build();
    TweakFlag OLD_ENDER_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).reloadResources().build();
    TweakFlag OLD_TRAPPED_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).reloadResources().build();

    // Torch

    TweakFlag OLD_TORCH_BRIGHTNESS = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().build();
    TweakFlag OLD_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().modIssues(TweakIssue.SODIUM).andIf(TweakCondition::isSodiumAbsent).build();
    TweakFlag OLD_REDSTONE_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().modIssues(TweakIssue.SODIUM).andIf(TweakCondition::isSodiumAbsent).build();
    TweakFlag OLD_SOUL_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().modIssues(TweakIssue.SODIUM).andIf(TweakCondition::isSodiumAbsent).build();

    // Interface

    TweakFlag OLD_BUTTON_HOVER = TweakFlag.client(true, CandyGroup.INTERFACE).build();
    TweakEnum<Hotbar> OLD_CREATIVE_HOTBAR = TweakEnum.server(Hotbar.CLASSIC, CandyGroup.INTERFACE).ignoreNetworkCheck().whenDisabled(Hotbar.MODERN).build();

    // Window Title

    TweakFlag ENABLE_WINDOW_TITLE = TweakFlag.client(false, CandyGroup.INTERFACE_WINDOW).newForUpdate().whenDisabled(false).build();
    TweakFlag MATCH_VERSION_OVERLAY = TweakFlag.client(false, CandyGroup.INTERFACE_WINDOW).newForUpdate().alert(TweakAlert.WINDOW_TITLE_DISABLED).build();
    TweakText WINDOW_TITLE_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_WINDOW).newForUpdate().alert(TweakAlert.WINDOW_TITLE_DISABLED).build();

    // Debug Screen

    TweakEnum<Generic> OLD_DEBUG = TweakEnum.client(Generic.BETA, CandyGroup.INTERFACE_DEBUG).newForUpdate().whenDisabled(Generic.MODERN).build();
    TweakFlag DEBUG_ENTITY_ID = TweakFlag.server(true, CandyGroup.INTERFACE_DEBUG).newForUpdate().load().build();

    // Debug Chart

    TweakEnum<DebugChart> FPS_CHART = TweakEnum.client(DebugChart.CLASSIC, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().whenDisabled(DebugChart.MODERN).build();
    TweakFlag SHOW_DEBUG_TPS_CHART = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().build();
    TweakFlag SHOW_DEBUG_PIE_CHART = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().build();
    TweakFlag OLD_PIE_CHART_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().build();

    // Debug Color

    TweakFlag SHOW_DEBUG_TEXT_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled(false).build();
    TweakFlag SHOW_DEBUG_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled(true).build();
    TweakColor DEBUG_BACKGROUND_COLOR = TweakColor.client("#50505090", CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled("#50505090").build();

    // Debug Extra

    TweakFlag SHOW_DEBUG_GPU_USAGE = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    TweakFlag SHOW_DEBUG_LIGHT_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    TweakFlag SHOW_DEBUG_FACING_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    TweakFlag SHOW_DEBUG_TARGET_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    TweakFlag SHOW_DEBUG_BIOME_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();

    // Inventory Screen

    TweakFlag OLD_INVENTORY = TweakFlag.client(true, CandyGroup.INTERFACE_INVENTORY).newForUpdate().build();
    TweakEnum<RecipeBook> INVENTORY_BOOK = TweakEnum.client(RecipeBook.SMALL, CandyGroup.INTERFACE_INVENTORY).newForUpdate().whenDisabled(RecipeBook.MODERN).build();
    TweakEnum<InventoryShield> INVENTORY_SHIELD = TweakEnum.client(InventoryShield.MIDDLE_RIGHT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().whenDisabled(InventoryShield.MODERN).alert(TweakAlert.SHIELD_CONFLICT).build();
    TweakFlag DISABLE_EMPTY_ARMOR_TEXTURE = TweakFlag.client(false, CandyGroup.INTERFACE_INVENTORY).newForUpdate().build();
    TweakFlag DISABLE_EMPTY_SHIELD_TEXTURE = TweakFlag.client(false, CandyGroup.INTERFACE_INVENTORY).newForUpdate().build();
    TweakFlag INVERTED_BLOCK_LIGHTING = TweakFlag.client(true, CandyGroup.INTERFACE_INVENTORY).newForUpdate().build();
    TweakFlag INVERTED_PLAYER_LIGHTING = TweakFlag.client(true, CandyGroup.INTERFACE_INVENTORY).newForUpdate().build();

    // Gui

    TweakEnum<GuiBackground> OLD_GUI_BACKGROUND = TweakEnum.client(GuiBackground.SOLID_BLACK, CandyGroup.INTERFACE_GUI).newForUpdate().build();
    TweakFlag CUSTOM_GUI_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_GUI).newForUpdate().build();
    TweakColor CUSTOM_TOP_GRADIENT = TweakColor.client("#00000000", CandyGroup.INTERFACE_GUI).newForUpdate().alert(TweakAlert.CUSTOM_GUI_DISABLED).load().build();
    TweakColor CUSTOM_BOTTOM_GRADIENT = TweakColor.client("#00000000", CandyGroup.INTERFACE_GUI).newForUpdate().alert(TweakAlert.CUSTOM_GUI_DISABLED).load().build();

    // Loading Overlay

    TweakEnum<Overlay> OLD_LOADING_OVERLAY = TweakEnum.client(Overlay.ALPHA, CandyGroup.INTERFACE_LOADING).whenDisabled(Overlay.MODERN).load().build();
    TweakFlag REMOVE_LOADING_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING).modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag OLD_LOADING_SCREENS = TweakFlag.client(true, CandyGroup.INTERFACE_LOADING).build();

    // Version Overlay

    TweakFlag OLD_VERSION_OVERLAY = TweakFlag.client(true, CandyGroup.INTERFACE_VERSION).build();
    TweakEnum<Corner> OLD_OVERLAY_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_VERSION).newForUpdate().load().build();
    TweakText OLD_OVERLAY_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_VERSION).load().build();

    // Pause Screen

    TweakEnum<PauseLayout> OLD_PAUSE_MENU = TweakEnum.client(PauseLayout.MODERN, CandyGroup.INTERFACE_PAUSE).newForUpdate().load().build();
    TweakFlag INCLUDE_MODS_ON_PAUSE = TweakFlag.client(true, CandyGroup.INTERFACE_PAUSE).newForUpdate().build();
    TweakFlag REMOVE_EXTRA_PAUSE_BUTTONS = TweakFlag.client(false, CandyGroup.INTERFACE_PAUSE).newForUpdate().build();

    // Anvil Screen

    TweakFlag OLD_ANVIL_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_ANVIL).newForUpdate().build();

    // Crafting Screen

    TweakFlag OLD_CRAFTING_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_CRAFTING).newForUpdate().build();
    TweakEnum<RecipeBook> CRAFTING_BOOK = TweakEnum.client(RecipeBook.SMALL, CandyGroup.INTERFACE_CRAFTING).newForUpdate().whenDisabled(RecipeBook.MODERN).build();

    // Furnace Screen

    TweakFlag OLD_FURNACE_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_FURNACE).newForUpdate().build();
    TweakEnum<RecipeBook> FURNACE_BOOK = TweakEnum.client(RecipeBook.SMALL, CandyGroup.INTERFACE_FURNACE).newForUpdate().whenDisabled(RecipeBook.MODERN).build();

    // Chat Screen

    TweakFlag OLD_CHAT_INPUT = TweakFlag.client(true, CandyGroup.INTERFACE_CHAT).build();
    TweakFlag OLD_CHAT_BOX = TweakFlag.client(true, CandyGroup.INTERFACE_CHAT).build();
    TweakFlag DISABLE_SIGNATURE_BOXES = TweakFlag.client(false, CandyGroup.INTERFACE_CHAT).newForUpdate().build();
    TweakNumber<Integer> CHAT_OFFSET = TweakNumber.client(0, CandyGroup.INTERFACE_CHAT).newForUpdate().slider(Lang.Slider.OFFSET, 0, 32).load().build();

    // Tooltips

    TweakFlag OLD_TOOLTIP_BOXES = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP).build();
    TweakFlag OLD_NO_ITEM_TOOLTIPS = TweakFlag.client(false, CandyGroup.INTERFACE_TOOLTIP).build();

    // Tooltip Parts

    TweakFlag SHOW_ENCHANTMENT_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();
    TweakFlag SHOW_MODIFIER_TIP = TweakFlag.client(false, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();
    TweakFlag SHOW_DYE_TIP = TweakFlag.client(false, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();

    // Items

    private static ItemSet defaultIgnoredHoldingItems()
    {
        return new ItemSet(ItemRule.NO_BLOCKS).startWith(ItemCommonUtil.getKeysFromItems(Items.CROSSBOW));
    }

    TweakFlag FIX_ITEM_MODEL_GAP = TweakFlag.client(true, CandyGroup.ITEM).reloadResources().build();
    TweakFlag OLD_DAMAGE_ARMOR_TINT = TweakFlag.client(true, CandyGroup.ITEM).newForUpdate().modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag OLD_ITEM_HOLDING = TweakFlag.client(true, CandyGroup.ITEM).build();
    TweakItemSet IGNORED_HOLDING_ITEMS = TweakItemSet.client(defaultIgnoredHoldingItems(), CandyGroup.ITEM).newForUpdate().build();

    // Item Merging

    TweakFlag OLD_ITEM_MERGING = TweakFlag.server(true, CandyGroup.ITEM_MERGING).build();
    TweakNumber<Integer> ITEM_MERGE_LIMIT = TweakNumber.server(16, CandyGroup.ITEM_MERGING).newForUpdate().slider(Lang.Slider.LIMIT, 1, 64).build();

    // 2D Items

    TweakFlag OLD_2D_COLORS = TweakFlag.client(false, CandyGroup.ITEM_FLAT).newForUpdate().build();
    TweakFlag OLD_2D_ITEMS = TweakFlag.client(true, CandyGroup.ITEM_FLAT).build();
    TweakFlag OLD_2D_FRAMES = TweakFlag.client(true, CandyGroup.ITEM_FLAT).build();
    TweakFlag OLD_2D_THROWN_ITEMS = TweakFlag.client(true, CandyGroup.ITEM_FLAT).build();
    TweakFlag OLD_2D_ENCHANTED_ITEMS = TweakFlag.client(false, CandyGroup.ITEM_FLAT).andIf(TweakCondition::areItemsFlat).build();
    TweakFlag OLD_2D_RENDERING = TweakFlag.client(true, CandyGroup.ITEM_FLAT).newForUpdate().modIssues(TweakIssue.OPTIFINE).build();

    // Item Display

    TweakFlag OLD_DURABILITY_COLORS = TweakFlag.client(true, CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_NO_SELECTED_ITEM_NAME = TweakFlag.client(true, CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_PLAIN_SELECTED_ITEM_NAME = TweakFlag.client(false, CandyGroup.ITEM_DISPLAY).build();

    // World Lighting

    TweakFlag FIX_CHUNK_BORDER_LAG = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).newForUpdate().build();
    TweakFlag DISABLE_BRIGHTNESS = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).newForUpdate().alert(TweakAlert.BRIGHTNESS_CONFLICT).build();
    TweakFlag DISABLE_LIGHT_FLICKER = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).build();
    TweakFlag OLD_NETHER_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).reloadChunks().build();
    TweakFlag OLD_LIGHT_RENDERING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).newForUpdate().modIssues(TweakIssue.SODIUM, TweakIssue.OPTIFINE).conflictMods(ModTracker.DISTANT_HORIZONS).reloadChunks().build();
    TweakFlag OLD_LIGHT_COLOR = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).newForUpdate().alert(TweakAlert.LIGHT_CONFLICT).build();
    TweakFlag OLD_SMOOTH_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD).reloadChunks().build();
    TweakFlag OLD_CLASSIC_LIGHTING = TweakFlag.server(false, CandyGroup.LIGHTING_WORLD).newForUpdate().reloadChunks().build();

    // Shader Support

    TweakNumber<Integer> MAX_BLOCK_LIGHT = TweakNumber.client(15, CandyGroup.LIGHTING_WORLD_SHADER).slider(Lang.Slider.BLOCK_LIGHT, 0, 15).newForUpdate().reloadChunks().build();

    // Block Lighting

    TweakFlag OLD_LEAVES_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().build();
    TweakFlag OLD_WATER_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().build();

    // Particles

    TweakFlag OLD_OPAQUE_EXPERIENCE = TweakFlag.client(true, CandyGroup.PARTICLE).build();
    TweakFlag DISABLE_NETHER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE).newForUpdate().build();
    TweakFlag DISABLE_UNDERWATER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE).newForUpdate().build();

    // Block Particles

    TweakFlag DISABLE_LAVA_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_LEVER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_GROWTH_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_MODEL_DESTRUCTION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();

    // Player Particles

    TweakFlag DISABLE_FALLING_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();
    TweakFlag DISABLE_SPRINTING_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();

    // Attack Particles

    TweakFlag OLD_SWEEP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_DAMAGE_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_CRIT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_MAGIC_HIT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();

    // Explosion Particles

    TweakFlag OLD_EXPLOSION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_EXPLOSION).build();
    TweakFlag OLD_MIXED_EXPLOSION_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_EXPLOSION).build();
    TweakFlag UNOPTIMIZED_EXPLOSION_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_EXPLOSION).newForUpdate().build();

    // Title Screen

    TweakFlag OVERRIDE_TITLE_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).build();
    TweakFlag OLD_TITLE_BACKGROUND = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).load().build();
    TweakFlag UNCAP_TITLE_FPS = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).load().build();

    // Title Screen Logo

    TweakFlag OLD_ALPHA_LOGO = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_LOGO).load().build();
    TweakFlag OLD_LOGO_OUTLINE = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_LOGO).load().build();

    // Title Screen Buttons

    TweakEnum<TitleLayout> OLD_BUTTON_LAYOUT = TweakEnum.client(TitleLayout.MODERN, CandyGroup.INTERFACE_TITLE_BUTTON).load().build();
    TweakFlag INCLUDE_MODS_ON_TITLE = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().load().build();
    TweakFlag REMOVE_TITLE_REALMS_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().load().build();
    TweakFlag REMOVE_TITLE_ACCESSIBILITY_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).load().build();
    TweakFlag REMOVE_TITLE_LANGUAGE_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).load().build();

    // Title Screen Text

    TweakText TITLE_VERSION_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_TITLE_TEXT).load().build();
    TweakFlag TITLE_BOTTOM_LEFT_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_TEXT).load().build();
    TweakFlag REMOVE_TITLE_MOD_LOADER_TEXT = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_TEXT).load().build();

    // World

    TweakFlag OLD_SQUARE_BORDER = TweakFlag.server(true, CandyGroup.WORLD).reloadChunks().ignoreNetworkCheck().build();
    TweakFlag OLD_NAME_TAGS = TweakFlag.client(false, CandyGroup.WORLD).newForUpdate().build();

    // World Fog

    TweakEnum<WorldFog> OLD_WORLD_FOG = TweakEnum.client(WorldFog.ALPHA_R164, CandyGroup.WORLD_FOG).newForUpdate().build();
    TweakFlag DISABLE_HORIZON_FOG = TweakFlag.client(false, CandyGroup.WORLD_FOG).newForUpdate().build();
    TweakFlag OLD_SUNRISE_SUNSET_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();
    TweakFlag OLD_DARK_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).newForUpdate().build();
    TweakFlag OLD_DYNAMIC_FOG_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG).newForUpdate().alert(TweakAlert.DYNAMIC_FOG).build();
    TweakEnum<FogColor> UNIVERSAL_FOG_COLOR = TweakEnum.client(FogColor.DISABLED, CandyGroup.WORLD_FOG).newForUpdate().alert(TweakAlert.UNIVERSAL_FOG).build();
    TweakFlag OLD_NETHER_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();

    // Custom World Fog

    TweakFlag CUSTOM_TERRAIN_FOG = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();
    TweakColor CUSTOM_TERRAIN_FOG_COLOR = TweakColor.client("#FFFFFF", CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();
    TweakFlag CUSTOM_NETHER_FOG = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();
    TweakColor CUSTOM_NETHER_FOG_COLOR = TweakColor.client("#FF0000", CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();

    // Water Fog

    TweakFlag OLD_WATER_FOG_DENSITY = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();
    TweakFlag OLD_WATER_FOG_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();
    TweakFlag SMOOTH_WATER_DENSITY = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();
    TweakFlag SMOOTH_WATER_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();

    // World Sky

    TweakNumber<Integer> OLD_CLOUD_HEIGHT = TweakNumber.client(108, CandyGroup.WORLD_SKY).range(108, 192).type(SliderType.CLOUD).whenDisabled(192).build();
    TweakFlag DISABLE_SUNRISE_SUNSET_COLORS = TweakFlag.client(false, CandyGroup.WORLD_SKY).newForUpdate().build();
    TweakFlag OLD_SUNRISE_AT_NORTH = TweakFlag.client(true, CandyGroup.WORLD_SKY).build();
    TweakEnum<Generic> OLD_STARS = TweakEnum.client(Generic.ALPHA, CandyGroup.WORLD_SKY).whenDisabled(Generic.MODERN).build();
    TweakFlag OLD_DYNAMIC_SKY_COLOR = TweakFlag.client(true, CandyGroup.WORLD_SKY).newForUpdate().alert(TweakAlert.DYNAMIC_SKY).build();
    TweakEnum<SkyColor> UNIVERSAL_SKY_COLOR = TweakEnum.client(SkyColor.DISABLED, CandyGroup.WORLD_SKY).newForUpdate().alert(TweakAlert.UNIVERSAL_SKY).build();
    TweakFlag OLD_NETHER_SKY = TweakFlag.client(true, CandyGroup.WORLD_SKY).newForUpdate().build();

    // Custom World Sky

    TweakFlag CUSTOM_WORLD_SKY = TweakFlag.client(false, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().build();
    TweakColor CUSTOM_WORLD_SKY_COLOR = TweakColor.client("#FFFFFF", CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().build();
    TweakFlag CUSTOM_NETHER_SKY = TweakFlag.client(false, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().build();
    TweakColor CUSTOM_NETHER_SKY_COLOR = TweakColor.client("#FF0000", CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().build();

    // Void Sky

    TweakEnum<Generic> OLD_BLUE_VOID = TweakEnum.client(Generic.ALPHA, CandyGroup.WORLD_VOID_SKY).whenDisabled(Generic.MODERN).build();
    TweakFlag OLD_BLUE_VOID_OVERRIDE = TweakFlag.client(true, CandyGroup.WORLD_VOID_SKY).build();
    TweakFlag OLD_DARK_VOID_HEIGHT = TweakFlag.client(false, CandyGroup.WORLD_VOID_SKY).alert(TweakAlert.VOID_CONFLICT).build();
    TweakFlag CUSTOM_VOID_SKY = TweakFlag.client(false, CandyGroup.WORLD_VOID_SKY).newForUpdate().build();
    TweakColor CUSTOM_VOID_SKY_COLOR = TweakColor.client("#0000FF", CandyGroup.WORLD_VOID_SKY).newForUpdate().build();

    // Void Fog

    TweakFlag DISABLE_VOID_FOG = TweakFlag.client(false, CandyGroup.WORLD_VOID_FOG).newForUpdate().whenDisabled(true).build();
    TweakFlag CREATIVE_VOID_FOG = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakFlag CREATIVE_VOID_PARTICLES = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakFlag LIGHT_REMOVES_VOID_FOG = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).newForUpdate().whenDisabled(true).build();
    TweakColor VOID_FOG_COLOR = TweakColor.client("#0C0C0C", CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakNumber<Integer> VOID_FOG_ENCROACH = TweakNumber.client(50, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.ENCROACH, 0, 100, "%").build();
    TweakNumber<Integer> VOID_FOG_START = TweakNumber.client(50, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.Y_LEVEL, -64, 320).build();
    TweakNumber<Integer> VOID_PARTICLE_START = TweakNumber.client(-47, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.Y_LEVEL, -64, 320).build();
    TweakNumber<Integer> VOID_PARTICLE_RADIUS = TweakNumber.client(16, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.RADIUS, 0, 32).build();
    TweakNumber<Integer> VOID_PARTICLE_DENSITY = TweakNumber.client(20, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.DENSITY, 0, 100, "%").build();
}
