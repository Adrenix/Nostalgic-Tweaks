package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.common.config.v2.container.group.CandyGroup;
import mod.adrenix.nostalgic.common.config.v2.gui.SliderType;
import mod.adrenix.nostalgic.common.config.v2.gui.TweakSlider;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.LinkedHashSet;

public abstract class CandyTweak
{
    // Block

    public static final Tweak<Boolean> FIX_AMBIENT_OCCLUSION = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK).reloadChunks().build();
    public static final Tweak<Boolean> DISABLE_ALL_OFFSET = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.BLOCK).newForUpdate().reloadChunks().build();
    public static final Tweak<Boolean> DISABLE_FLOWER_OFFSET = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK).newForUpdate().reloadChunks().build();
    public static final Tweak<TweakVersion.MissingTexture> OLD_MISSING_TEXTURE = Tweak.builder(TweakVersion.MissingTexture.MODERN, TweakSide.CLIENT, CandyGroup.BLOCK).newForUpdate().reloadResources().build();

    // Hitbox Outlines

    public static final Tweak<Boolean> OLD_STAIR_OUTLINE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    public static final Tweak<Boolean> OLD_FENCE_OUTLINE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SLAB_OUTLINE = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    public static final Tweak<Boolean> OLD_WALL_OUTLINE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_OUTLINE).newForUpdate().build();
    public static final Tweak<HashSet<String>> OLD_BLOCK_OUTLINES = Tweak.builder(new HashSet<String>(), TweakSide.CLIENT, CandyGroup.BLOCK_OUTLINE).newForUpdate().load().bottom().build();

    // Chests

    public static final Tweak<Boolean> OLD_CHEST = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_CHEST).reloadResources().optifineTag().build();
    public static final Tweak<Boolean> OLD_CHEST_VOXEL = Tweak.builder(false, TweakSide.SERVER, CandyGroup.BLOCK_CHEST).reloadChunks().warningTag().build();
    public static final Tweak<Boolean> OLD_ENDER_CHEST = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_CHEST).reloadResources().build();
    public static final Tweak<Boolean> OLD_TRAPPED_CHEST = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_CHEST).reloadResources().build();

    // Torch

    public static final Tweak<Boolean> OLD_TORCH_BRIGHTNESS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_TORCH).newForUpdate().top().reloadChunks().build();
    public static final Tweak<Boolean> OLD_TORCH_MODEL = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_TORCH).newForUpdate().top().reloadChunks().sodiumTag().andIf(TweakCondition::isSodiumAbsent).build();
    public static final Tweak<Boolean> OLD_REDSTONE_TORCH_MODEL = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_TORCH).newForUpdate().top().reloadChunks().sodiumTag().andIf(TweakCondition::isSodiumAbsent).build();
    public static final Tweak<Boolean> OLD_SOUL_TORCH_MODEL = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.BLOCK_TORCH).newForUpdate().top().reloadChunks().sodiumTag().andIf(TweakCondition::isSodiumAbsent).build();

    // Interface

    public static final Tweak<Boolean> OLD_BUTTON_HOVER = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE).build();
    public static final Tweak<TweakVersion.Hotbar> OLD_CREATIVE_HOTBAR = Tweak.builder(TweakVersion.Hotbar.CLASSIC, TweakSide.SERVER, CandyGroup.INTERFACE_ANVIL).ignoreNetworkCheck().whenDisabled(TweakVersion.Hotbar.MODERN).build();

    // Window Title

    public static final Tweak<Boolean> ENABLE_WINDOW_TITLE = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_WINDOW).newForUpdate().top().whenDisabled(false).build();
    public static final Tweak<Boolean> MATCH_VERSION_OVERLAY = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_WINDOW).newForUpdate().top().alert(TweakAlert::isWindowTitleDisabled).build();
    public static final Tweak<String> WINDOW_TITLE_TEXT = Tweak.builder("Minecraft %v", TweakSide.CLIENT, CandyGroup.INTERFACE_WINDOW).newForUpdate().top().alert(TweakAlert::isWindowTitleDisabled).build();

    // Debug Screen

    public static final Tweak<TweakVersion.Generic> OLD_DEBUG = Tweak.builder(TweakVersion.Generic.BETA, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG).newForUpdate().top().whenDisabled(TweakVersion.Generic.MODERN).build();
    public static final Tweak<Boolean> DEBUG_ENTITY_ID = Tweak.builder(true, TweakSide.SERVER, CandyGroup.INTERFACE_DEBUG).newForUpdate().load().bottom().build();

    // Debug Chart

    public static final Tweak<TweakType.DebugChart> FPS_CHART = Tweak.builder(TweakType.DebugChart.CLASSIC, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().top().whenDisabled(TweakType.DebugChart.MODERN).build();
    public static final Tweak<Boolean> SHOW_DEBUG_TPS_CHART = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().top().build();
    public static final Tweak<Boolean> SHOW_DEBUG_PIE_CHART = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().top().build();
    public static final Tweak<Boolean> OLD_PIE_CHART_BACKGROUND = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().top().build();

    // Debug Color

    public static final Tweak<Boolean> SHOW_DEBUG_TEXT_SHADOW = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled(false).top().build();
    public static final Tweak<Boolean> SHOW_DEBUG_BACKGROUND = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled(true).top().build();
    public static final Tweak<String> DEBUG_BACKGROUND_COLOR = Tweak.builder("#50505090", TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().top().colorTweak().whenDisabled("#50505090").load().build();

    // Debug Extra

    public static final Tweak<Boolean> SHOW_DEBUG_GPU_USAGE = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    public static final Tweak<Boolean> SHOW_DEBUG_LIGHT_DATA = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    public static final Tweak<Boolean> SHOW_DEBUG_FACING_DATA = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    public static final Tweak<Boolean> SHOW_DEBUG_TARGET_DATA = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();
    public static final Tweak<Boolean> SHOW_DEBUG_BIOME_DATA = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_DEBUG_EXTRA).newForUpdate().build();

    // Inventory Screen

    public static final Tweak<Boolean> OLD_INVENTORY = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().top().build();
    public static final Tweak<TweakType.RecipeBook> INVENTORY_BOOK = Tweak.builder(TweakType.RecipeBook.DISABLED, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().top().whenDisabled(TweakType.RecipeBook.MODERN).build();
    public static final Tweak<TweakType.InventoryShield> INVENTORY_SHIELD = Tweak.builder(TweakType.InventoryShield.INVISIBLE, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().top().whenDisabled(TweakType.InventoryShield.MODERN).alert(TweakAlert::isShieldConflict).build();
    public static final Tweak<Boolean> DISABLE_EMPTY_ARMOR_TEXTURE = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().top().build();
    public static final Tweak<Boolean> DISABLE_EMPTY_SHIELD_TEXTURE = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().top().build();
    public static final Tweak<Boolean> INVERTED_BLOCK_LIGHTING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().bottom().build();
    public static final Tweak<Boolean> INVERTED_PLAYER_LIGHTING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_INVENTORY).newForUpdate().bottom().build();

    // GUI

    public static final Tweak<TweakType.GuiBackground> OLD_GUI_BACKGROUND = Tweak.builder(TweakType.GuiBackground.SOLID_BLACK, TweakSide.CLIENT, CandyGroup.INTERFACE_GUI).newForUpdate().top().build();
    public static final Tweak<Boolean> CUSTOM_GUI_BACKGROUND = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_GUI).newForUpdate().top().build();
    public static final Tweak<String> CUSTOM_TOP_GRADIENT = Tweak.builder("#00000000", TweakSide.CLIENT, CandyGroup.INTERFACE_GUI).newForUpdate().colorTweak().top().load().build();
    public static final Tweak<String> CUSTOM_BOTTOM_GRADIENT = Tweak.builder("#00000000", TweakSide.CLIENT, CandyGroup.INTERFACE_GUI).newForUpdate().colorTweak().top().load().build();

    // Loading Overlay

    public static final Tweak<TweakVersion.Overlay> OLD_LOADING_OVERLAY = Tweak.builder(TweakVersion.Overlay.ALPHA, TweakSide.CLIENT, CandyGroup.INTERFACE_LOADING).top().whenDisabled(TweakVersion.Overlay.MODERN).load().build();
    public static final Tweak<Boolean> REMOVE_LOADING_BAR = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_LOADING).top().optifineTag().build();
    public static final Tweak<Boolean> OLD_LOADING_SCREENS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_LOADING).build();

    // Version Overlay

    public static final Tweak<Boolean> OLD_VERSION_OVERLAY = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_VERSION).top().build();
    public static final Tweak<TweakType.Corner> OLD_OVERLAY_CORNER = Tweak.builder(TweakType.Corner.TOP_LEFT, TweakSide.CLIENT, CandyGroup.INTERFACE_VERSION).newForUpdate().top().load().build();
    public static final Tweak<String> OLD_OVERLAY_TEXT = Tweak.builder("Minecraft %v", TweakSide.CLIENT, CandyGroup.INTERFACE_VERSION).top().load().build();

    // Pause Screen

    public static final Tweak<TweakVersion.PauseLayout> OLD_PAUSE_MENU = Tweak.builder(TweakVersion.PauseLayout.MODERN, TweakSide.CLIENT, CandyGroup.INTERFACE_PAUSE).newForUpdate().top().load().build();
    public static final Tweak<Boolean> INCLUDE_MODS_ON_PAUSE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_PAUSE).newForUpdate().build();
    public static final Tweak<Boolean> REMOVE_EXTRA_PAUSE_BUTTONS = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_PAUSE).newForUpdate().build();

    // Anvil Screen

    public static final Tweak<Boolean> OLD_ANVIL_SCREEN = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_ANVIL).newForUpdate().build();

    // Crafting Screen

    public static final Tweak<Boolean> OLD_CRAFTING_SCREEN = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_CRAFTING).newForUpdate().top().build();
    public static final Tweak<TweakType.RecipeBook> CRAFTING_BOOK = Tweak.builder(TweakType.RecipeBook.DISABLED, TweakSide.CLIENT, CandyGroup.INTERFACE_CRAFTING).newForUpdate().top().whenDisabled(TweakType.RecipeBook.MODERN).build();

    // Furnace Screen

    public static final Tweak<Boolean> OLD_FURNACE_SCREEN = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_FURNACE).newForUpdate().top().build();
    public static final Tweak<TweakType.RecipeBook> FURNACE_BOOK = Tweak.builder(TweakType.RecipeBook.DISABLED, TweakSide.CLIENT, CandyGroup.INTERFACE_FURNACE).newForUpdate().top().whenDisabled(TweakType.RecipeBook.MODERN).build();

    // Chat Screen

    public static final Tweak<Boolean> OLD_CHAT_INPUT = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_CHAT).build();
    public static final Tweak<Boolean> OLD_CHAT_BOX = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_CHAT).build();
    public static final Tweak<Boolean> DISABLE_SIGNATURE_BOXES = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_CHAT).newForUpdate().build();
    public static final Tweak<Integer> CHAT_OFFSET = Tweak.builder(0, TweakSide.CLIENT, CandyGroup.INTERFACE_CHAT).newForUpdate().bottom().slider(TweakSlider.builder(0, 0, 32, 1).langKey(LangUtil.Gui.SLIDER_OFFSET).build()).load().build();

    // Tooltips

    public static final Tweak<Boolean> OLD_TOOLTIP_BOXES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TOOLTIP).top().build();
    public static final Tweak<Boolean> OLD_NO_ITEM_TOOLTIPS = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TOOLTIP).top().build();

    // Tooltip Parts

    public static final Tweak<Boolean> SHOW_ENCHANTMENT_TIP = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();
    public static final Tweak<Boolean> SHOW_MODIFIERS_TIP = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();
    public static final Tweak<Boolean> SHOW_DYE_TIP = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();

    // Items

    public static final Tweak<Boolean> FIX_ITEM_MODEL_GAP = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM).top().reloadResources().build();
    public static final Tweak<Boolean> OLD_DAMAGE_ARMOR_TINT = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM).newForUpdate().optifineTag().build();
    public static final Tweak<Boolean> OLD_ITEM_HOLDING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM).build();
    public static final Tweak<LinkedHashSet<String>> IGNORED_HOLDING_ITEMS = Tweak.builder(ItemCommonUtil.getKeysFromItems(Items.CROSSBOW), TweakSide.CLIENT, CandyGroup.ITEM).newForUpdate().bottom().load().build();

    // Item Merging

    public static final Tweak<Integer> ITEM_MERGE_LIMIT = Tweak.builder(16, TweakSide.SERVER, CandyGroup.ITEM_MERGING).newForUpdate().top().slider(TweakSlider.builder(16, 1, 64, 1).langKey(LangUtil.Gui.SLIDER_LIMIT).build()).build();
    public static final Tweak<Boolean> OLD_ITEM_MERGING = Tweak.builder(true, TweakSide.SERVER, CandyGroup.ITEM_MERGING).build();

    // 2D Items

    public static final Tweak<Boolean> OLD_2D_COLORS = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.ITEM_FLAT).newForUpdate().build();
    public static final Tweak<Boolean> OLD_2D_ITEMS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM_FLAT).build();
    public static final Tweak<Boolean> OLD_2D_FRAMES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM_FLAT).build();
    public static final Tweak<Boolean> OLD_2D_THROWN_ITEMS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM_FLAT).build();
    public static final Tweak<Boolean> OLD_2D_ENCHANTED_ITEMS = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.ITEM_FLAT).andIf(TweakCondition::areItemsFlat).build();
    public static final Tweak<Boolean> OLD_2D_RENDERING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM_FLAT).newForUpdate().optifineTag().build();

    // Item Display

    public static final Tweak<Boolean> OLD_DURABILITY_COLORS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM_DISPLAY).build();
    public static final Tweak<Boolean> OLD_NO_SELECTED_ITEM_NAME = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.ITEM_DISPLAY).build();
    public static final Tweak<Boolean> OLD_PLAIN_SELECTED_ITEM_NAME = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.ITEM_DISPLAY).build();

    // World Lighting

    public static final Tweak<Boolean> FIX_CHUNK_BORDER_LAG = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).newForUpdate().top().build();
    public static final Tweak<Boolean> DISABLE_BRIGHTNESS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).newForUpdate().top().alert(TweakAlert::isBrightnessConflict).build();
    public static final Tweak<Boolean> DISABLE_LIGHT_FLICKER = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).top().build();
    public static final Tweak<Boolean> OLD_CLASSIC_LIGHTING = Tweak.builder(false, TweakSide.SERVER, CandyGroup.LIGHTING_WORLD).newForUpdate().reloadChunks().build();
    public static final Tweak<Boolean> OLD_NETHER_LIGHTING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).reloadChunks().build();
    public static final Tweak<Boolean> OLD_LIGHT_RENDERING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).newForUpdate().conflictModIds("lod").optifineTag().sodiumTag().reloadChunks().build();
    public static final Tweak<Boolean> OLD_LIGHT_COLOR = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).newForUpdate().alert(TweakAlert::isLightConflict).build();
    public static final Tweak<Boolean> OLD_SMOOTH_LIGHTING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD).reloadChunks().build();

    // Shader Support

    public static final Tweak<Integer> MAX_BLOCK_LIGHT = Tweak.builder(15, TweakSide.CLIENT, CandyGroup.LIGHTING_WORLD_SHADER).newForUpdate().reloadChunks().slider(TweakSlider.builder(15, 0, 15, 1).langKey(LangUtil.Gui.SLIDER_BLOCK_LIGHT).build()).build();

    // Block Lighting

    public static final Tweak<Boolean> OLD_LEAVES_LIGHTING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_BLOCK).reloadChunks().build();
    public static final Tweak<Boolean> OLD_WATER_LIGHTING = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.LIGHTING_BLOCK).reloadChunks().build();

    // Particles

    public static final Tweak<Boolean> OLD_OPAQUE_EXPERIENCE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE).build();
    public static final Tweak<Boolean> DISABLE_NETHER_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_UNDERWATER_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE).newForUpdate().build();

    // Block Particles

    public static final Tweak<Boolean> DISABLE_LAVA_PARTICLES = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_LEVER_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_GROWTH_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_MODEL_DESTRUCTION_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();

    // Player Particles

    public static final Tweak<Boolean> DISABLE_FALLING_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_SPRINTING_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();

    // Attack Particles

    public static final Tweak<Boolean> OLD_SWEEP_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_ATTACK).build();
    public static final Tweak<Boolean> OLD_NO_DAMAGE_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_ATTACK).build();
    public static final Tweak<Boolean> OLD_NO_CRIT_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_ATTACK).build();
    public static final Tweak<Boolean> OLD_NO_MAGIC_HIT_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_ATTACK).build();

    // Explosion Particles

    public static final Tweak<Boolean> OLD_EXPLOSION_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.PARTICLE_EXPLOSION).build();
    public static final Tweak<Boolean> OLD_MIXED_EXPLOSION_PARTICLES = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.PARTICLE_EXPLOSION).build();
    public static final Tweak<Boolean> UNOPTIMIZED_EXPLOSION_PARTICLES = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.PARTICLE_EXPLOSION).newForUpdate().build();

    // Title Screen

    public static final Tweak<Boolean> OVERRIDE_TITLE_SCREEN = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE).top().build();
    public static final Tweak<Boolean> OLD_TITLE_BACKGROUND = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE).load().build();
    public static final Tweak<Boolean> UNCAP_TITLE_FPS = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE).load().build();

    // Title Screen Logo

    public static final Tweak<Boolean> OLD_ALPHA_LOGO = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_LOGO).load().build();
    public static final Tweak<Boolean> OLD_LOGO_OUTLINE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_LOGO).load().build();

    // Title Screen Buttons

    public static final Tweak<TweakVersion.TitleLayout> OLD_BUTTON_LAYOUT = Tweak.builder(TweakVersion.TitleLayout.MODERN, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_BUTTON).top().load().build();
    public static final Tweak<Boolean> INCLUDE_MODS_ON_TITLE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().top().load().build();
    public static final Tweak<Boolean> REMOVE_TITLE_REALMS_BUTTON = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().top().load().build();
    public static final Tweak<Boolean> REMOVE_TITLE_ACCESSIBILITY_BUTTON = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_BUTTON).top().load().build();
    public static final Tweak<Boolean> REMOVE_TITLE_LANGUAGE_BUTTON = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_BUTTON).top().load().build();

    // Title Screen Text

    public static final Tweak<String> TITLE_VERSION_TEXT = Tweak.builder("Minecraft %v", TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_TEXT).top().load().build();
    public static final Tweak<Boolean> TITLE_BOTTOM_LEFT_TEXT = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_TEXT).top().load().build();
    public static final Tweak<Boolean> REMOVE_TITLE_MOD_LOADER_TEXT = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.INTERFACE_TITLE_TEXT).top().load().build();

    // World

    public static final Tweak<Boolean> OLD_SQUARE_BORDER = Tweak.builder(true, TweakSide.SERVER, CandyGroup.WORLD).reloadChunks().ignoreNetworkCheck().build();
    public static final Tweak<Boolean> OLD_NAME_TAGS = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD).newForUpdate().build();

    // World Fog

    public static final Tweak<TweakVersion.WorldFog> OLD_WORLD_FOG = Tweak.builder(TweakVersion.WorldFog.ALPHA_R164, TweakSide.CLIENT, CandyGroup.WORLD_FOG).newForUpdate().top().build();
    public static final Tweak<Boolean> DISABLE_HORIZON_FOG = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_FOG).newForUpdate().top().build();
    public static final Tweak<Boolean> OLD_NETHER_FOG = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG).top().build();
    public static final Tweak<Boolean> OLD_SUNRISE_SUNSET_FOG = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG).top().build();
    public static final Tweak<Boolean> OLD_DARK_FOG = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG).newForUpdate().top().build();
    public static final Tweak<Boolean> OLD_DYNAMIC_FOG_COLOR = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG).newForUpdate().top().alert(TweakAlert::isDynamicFogConflict).build();
    public static final Tweak<TweakVersion.FogColor> UNIVERSAL_FOG_COLOR = Tweak.builder(TweakVersion.FogColor.DISABLED, TweakSide.CLIENT, CandyGroup.WORLD_FOG).newForUpdate().top().alert(TweakAlert::isUniversalFogConflict).build();

    // Custom World Fog

    public static final Tweak<Boolean> CUSTOM_TERRAIN_FOG = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().top().build();
    public static final Tweak<String> CUSTOM_TERRAIN_FOG_COLOR = Tweak.builder("#FFFFFFFF", TweakSide.CLIENT, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().colorTweak().top().load().build();
    public static final Tweak<Boolean> CUSTOM_NETHER_FOG = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().top().build();
    public static final Tweak<String> CUSTOM_NETHER_FOG_COLOR = Tweak.builder("#FF0000FF", TweakSide.CLIENT, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().colorTweak().top().load().build();

    // Water Fog

    public static final Tweak<Boolean> OLD_WATER_FOG_DENSITY = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG_WATER).newForUpdate().top().build();
    public static final Tweak<Boolean> OLD_WATER_FOG_COLOR = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG_WATER).newForUpdate().top().build();
    public static final Tweak<Boolean> SMOOTH_WATER_DENSITY = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG_WATER).newForUpdate().top().build();
    public static final Tweak<Boolean> SMOOTH_WATER_COLOR = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_FOG_WATER).newForUpdate().top().build();

    // World Sky

    public static final Tweak<Boolean> DISABLE_SUNRISE_SUNSET_COLORS = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_SKY).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SUNRISE_AT_NORTH = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_SKY).build();
    public static final Tweak<TweakVersion.Generic> OLD_STARS = Tweak.builder(TweakVersion.Generic.ALPHA, TweakSide.CLIENT, CandyGroup.WORLD_SKY).whenDisabled(TweakVersion.Generic.MODERN).build();
    public static final Tweak<Boolean> OLD_DYNAMIC_SKY_COLOR = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_SKY).newForUpdate().bottom().alert(TweakAlert::isDynamicSkyConflict).build();
    public static final Tweak<TweakVersion.SkyColor> UNIVERSAL_SKY_COLOR = Tweak.builder(TweakVersion.SkyColor.DISABLED, TweakSide.CLIENT, CandyGroup.WORLD_SKY).newForUpdate().bottom().alert(TweakAlert::isUniversalSkyConflict).build();
    public static final Tweak<Boolean> OLD_NETHER_SKY = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_SKY).newForUpdate().build();
    public static final Tweak<Integer> OLD_CLOUD_HEIGHT = Tweak.builder(108, TweakSide.CLIENT, CandyGroup.WORLD_SKY).whenDisabled(192).slider(TweakSlider.builder(108, 108, 192, 1).type(SliderType.CLOUD).build()).build();

    // Custom World Sky

    public static final Tweak<Boolean> CUSTOM_WORLD_SKY = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().top().build();
    public static final Tweak<String> CUSTOM_WORLD_SKY_COLOR = Tweak.builder("#FFFFFFFF", TweakSide.CLIENT, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().colorTweak().top().load().build();
    public static final Tweak<Boolean> CUSTOM_NETHER_SKY = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().top().build();
    public static final Tweak<String> CUSTOM_NETHER_SKY_COLOR = Tweak.builder("#FF0000FF", TweakSide.CLIENT, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().colorTweak().top().load().build();

    // Void Sky

    public static final Tweak<TweakVersion.Generic> OLD_BLUE_VOID = Tweak.builder(TweakVersion.Generic.ALPHA, TweakSide.CLIENT, CandyGroup.WORLD_VOID_SKY).top().whenDisabled(TweakVersion.Generic.MODERN).build();
    public static final Tweak<Boolean> OLD_BLUE_VOID_OVERRIDE = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_VOID_SKY).top().build();
    public static final Tweak<Boolean> OLD_DARK_VOID_HEIGHT = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_VOID_SKY).top().alert(TweakAlert::isVoidConflict).build();
    public static final Tweak<Boolean> CUSTOM_VOID_SKY = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_VOID_SKY).newForUpdate().top().build();
    public static final Tweak<String> CUSTOM_VOID_SKY_COLOR = Tweak.builder("#0000FFFF", TweakSide.CLIENT, CandyGroup.WORLD_VOID_SKY).newForUpdate().colorTweak().top().load().build();

    // Void Fog

    public static final Tweak<Boolean> DISABLE_VOID_FOG = Tweak.builder(false, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().whenDisabled(true).top().build();
    public static final Tweak<Boolean> CREATIVE_VOID_FOG = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().top().build();
    public static final Tweak<Boolean> CREATIVE_VOID_PARTICLES = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().top().build();
    public static final Tweak<Boolean> LIGHT_REMOVES_VOID_FOG = Tweak.builder(true, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().whenDisabled(true).top().build();
    public static final Tweak<String> VOID_FOG_COLOR = Tweak.builder("#0C0C0CFF", TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().colorTweak().top().load().build();
    public static final Tweak<Integer> VOID_FOG_ENCROACH = Tweak.builder(50, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().top().slider(TweakSlider.builder(50, 0, 100, 1).suffix("%").langKey(LangUtil.Gui.SLIDER_ENCROACH).build()).build();
    public static final Tweak<Integer> VOID_FOG_START = Tweak.builder(50, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().bottom().slider(TweakSlider.builder(50, -64, 320, 1).langKey(LangUtil.Gui.SLIDER_Y_LEVEL).build()).build();
    public static final Tweak<Integer> VOID_PARTICLE_START = Tweak.builder(-47, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().bottom().slider(TweakSlider.builder(-47, -64, 320, 1).langKey(LangUtil.Gui.SLIDER_Y_LEVEL).build()).build();
    public static final Tweak<Integer> VOID_PARTICLE_RADIUS = Tweak.builder(16, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().bottom().slider(TweakSlider.builder(16, 0, 32, 1).langKey(LangUtil.Gui.SLIDER_RADIUS).build()).build();
    public static final Tweak<Integer> VOID_PARTICLE_DENSITY = Tweak.builder(20, TweakSide.CLIENT, CandyGroup.WORLD_VOID_FOG).newForUpdate().bottom().slider(TweakSlider.builder(20, 0, 100, 1).suffix("%").langKey(LangUtil.Gui.SLIDER_DENSITY).build()).build();
}
