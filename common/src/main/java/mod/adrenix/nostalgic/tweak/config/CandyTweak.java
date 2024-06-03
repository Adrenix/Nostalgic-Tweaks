package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.TweakCondition;
import mod.adrenix.nostalgic.tweak.TweakIssue;
import mod.adrenix.nostalgic.tweak.container.group.CandyGroup;
import mod.adrenix.nostalgic.tweak.enums.*;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.listing.*;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.world.item.Items;

// @formatter:off
public interface CandyTweak
{
    // Block

    TweakEnum<MissingTexture> OLD_MISSING_TEXTURE = TweakEnum.client(MissingTexture.MODERN, CandyGroup.BLOCK).newForUpdate().reloadResources().build();
    TweakFlag OLD_GRASS_SIDE_TEXTURE = TweakFlag.client(false, CandyGroup.BLOCK).newForUpdate().reloadResources().build();
    TweakFlag OLD_FAST_GRASS_TEXTURE = TweakFlag.client(true, CandyGroup.BLOCK).newForUpdate().reloadResources().build();
    TweakFlag REMOVE_MIPMAP_TEXTURE = TweakFlag.client(true, CandyGroup.BLOCK).newForUpdate().reloadResources().build();
    TweakFlag SODIUM_WATER_AO = TweakFlag.client(false, CandyGroup.BLOCK).newForUpdate().reloadChunks().ignoreIf(ModTracker.SODIUM::isNotInstalled).build();
    TweakItemSet AMBIENT_OCCLUSION_BLOCKS = TweakItemSet.client(DefaultListing.ambientOcclusion(), CandyGroup.BLOCK).newForUpdate().reloadChunks().build();
    TweakItemSet DISABLE_BLOCK_OFFSETS = TweakItemSet.client(DefaultListing.disabledOffsets(), CandyGroup.BLOCK).icon(TextureIcon.fromItem(Items.POPPY)).newForUpdate().reloadChunks().build();
    TweakFlag DISABLE_ALL_OFFSET = TweakFlag.client(false, CandyGroup.BLOCK).newForUpdate().reloadChunks().build();

    // Custom Hitbox

    TweakItemSet OLD_BLOCK_OUTLINES = TweakItemSet.client(DefaultListing.blockOutlines(), CandyGroup.BLOCK_HITBOX_OUTLINE).icon(Icons.HITBOX_OUTLINE).newForUpdate().build();
    TweakColor BLOCK_OUTLINE_COLOR = TweakColor.client("#00000066", CandyGroup.BLOCK_HITBOX_OUTLINE_AESTHETIC).newForUpdate().whenDisabled("#00000066").build();
    TweakNumber<Float> BLOCK_OUTLINE_THICKNESS = TweakNumber.client(3.25F, CandyGroup.BLOCK_HITBOX_OUTLINE_AESTHETIC).newForUpdate().slider(Lang.Slider.THICKNESS, 0.0F, 10.0F).interval(0.25F).roundTo(2).build();
    TweakFlag OLD_BLOCK_OVERLAY = TweakFlag.client(false, CandyGroup.BLOCK_HITBOX_OVERLAY).whenDisabled(false).newForUpdate().build();
    TweakEnum<RenderOrder> BLOCK_OVERLAY_RENDER_ORDER = TweakEnum.client(RenderOrder.FIRST, CandyGroup.BLOCK_HITBOX_OVERLAY).newForUpdate().build();
    TweakEnum<ColorType> BLOCK_OVERLAY_COLOR_TYPE = TweakEnum.client(ColorType.SOLID, CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).newForUpdate().build();
    TweakColor BLOCK_OVERLAY_COLOR = TweakColor.client("#FFFFFF5A", CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).newForUpdate().build();
    TweakColor BLOCK_OVERLAY_GRADIENT_TOP = TweakColor.client("#0000005A", CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).newForUpdate().build();
    TweakColor BLOCK_OVERLAY_GRADIENT_BOTTOM = TweakColor.client("#FFFFFF5A", CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).newForUpdate().build();
    TweakFlag PULSATE_BLOCK_OVERLAY = TweakFlag.client(true, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).newForUpdate().build();
    TweakEnum<AnimationType> PULSATE_OVERLAY_ANIMATION = TweakEnum.client(AnimationType.LINEAR, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).newForUpdate().build();
    TweakNumber<Float> BLOCK_OVERLAY_SPEED = TweakNumber.client(0.2F, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).newForUpdate().slider(Lang.Slider.SECONDS, 0.0F, 10.0F).interval(0.25F).build();
    TweakNumber<Float> MINIMUM_PULSATION_TRANSPARENCY = TweakNumber.client(0.1F, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).newForUpdate().slider(Lang.Slider.MINIMUM, 0.0F, 1.0F).interval(0.25F).build();
    TweakNumber<Float> MAXIMUM_PULSATION_TRANSPARENCY = TweakNumber.client(0.4F, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).newForUpdate().slider(Lang.Slider.MAXIMUM, 0.0F, 1.0F).interval(0.25F).build();

    // Chests

    TweakFlag OLD_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).modIssues(TweakIssue.OPTIFINE).reloadResources().build();
    TweakFlag OLD_ENDER_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).reloadResources().build();
    TweakFlag OLD_TRAPPED_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).reloadResources().build();
    TweakFlag APPLY_CHEST_VOXEL = TweakFlag.server(true, CandyGroup.BLOCK_CHEST).newForUpdate().build();
    TweakItemSet OLD_MOD_CHESTS = TweakItemSet.client(new ItemSet(ItemRule.ONLY_CHESTS), CandyGroup.BLOCK_CHEST).newForUpdate().reloadResources().build();
    TweakItemSet TRANSLUCENT_CHESTS = TweakItemSet.client(new ItemSet(ItemRule.ONLY_CHESTS), CandyGroup.BLOCK_CHEST).newForUpdate().reloadChunks().build();

    // Torch

    TweakFlag OLD_TORCH_BRIGHTNESS = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().build();
    TweakFlag OLD_TORCH_BOTTOM = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().build();
    TweakFlag OLD_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().build();
    TweakFlag OLD_REDSTONE_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().build();
    TweakFlag OLD_SOUL_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).newForUpdate().reloadChunks().build();

    // Bed

    TweakFlag HIDE_PLAYER_IN_BED = TweakFlag.client(true, CandyGroup.BLOCK_BED).newForUpdate().build();

    // Heads-up Display

    TweakFlag HIDE_EXPERIENCE_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR).newForUpdate().build();
    TweakFlag HIDE_HUNGER_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR).newForUpdate().build();

    // Game Version Overlay

    TweakFlag OLD_VERSION_OVERLAY = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_VERSION).build();
    TweakEnum<Corner> OLD_OVERLAY_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_VERSION).newForUpdate().load().build();
    TweakNumber<Integer> OLD_OVERLAY_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_VERSION).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> OLD_OVERLAY_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_VERSION).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakText OLD_OVERLAY_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_HUD_VERSION).load().build();

    // Alternative Experience Text

    TweakFlag SHOW_EXP_LEVEL_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).newForUpdate().build();
    TweakFlag SHOW_EXP_LEVEL_IN_CREATIVE = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).newForUpdate().build();
    TweakEnum<Corner> ALT_EXP_LEVEL_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).newForUpdate().load().build();
    TweakNumber<Integer> ALT_EXP_LEVEL_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_EXP_LEVEL_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakText ALT_EXP_LEVEL_TEXT = TweakText.client("Level: %a%v", CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).newForUpdate().load().build();

    // Alternative Progress Text

    TweakFlag SHOW_EXP_PROGRESS_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().build();
    TweakFlag SHOW_EXP_PROGRESS_IN_CREATIVE = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().build();
    TweakFlag USE_DYNAMIC_PROGRESS_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().load().build();
    TweakEnum<Corner> ALT_EXP_PROGRESS_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().load().build();
    TweakNumber<Integer> ALT_EXP_PROGRESS_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_EXP_PROGRESS_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakText ALT_EXP_PROGRESS_TEXT = TweakText.client("Experience: %v%", CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).newForUpdate().load().build();

    // Alternative Food Text

    TweakFlag SHOW_HUNGER_FOOD_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).newForUpdate().build();
    TweakFlag USE_DYNAMIC_FOOD_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).newForUpdate().load().build();
    TweakEnum<Corner> ALT_HUNGER_FOOD_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).newForUpdate().load().build();
    TweakNumber<Integer> ALT_HUNGER_FOOD_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_HUNGER_FOOD_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakText ALT_HUNGER_FOOD_TEXT = TweakText.client("Food: %v", CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).newForUpdate().load().build();

    // Alternative Saturation Text

    TweakFlag SHOW_HUNGER_SATURATION_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).newForUpdate().build();
    TweakFlag USE_DYNAMIC_SATURATION_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).newForUpdate().load().build();
    TweakEnum<Corner> ALT_HUNGER_SATURATION_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).newForUpdate().load().build();
    TweakNumber<Integer> ALT_HUNGER_SATURATION_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_HUNGER_SATURATION_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakText ALT_HUNGER_SATURATION_TEXT = TweakText.client("Saturation: %v%", CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).newForUpdate().load().build();

    // Window Title

    TweakFlag ENABLE_WINDOW_TITLE = TweakFlag.client(false, CandyGroup.INTERFACE_WINDOW).newForUpdate().whenDisabled(false).build();
    TweakFlag MATCH_VERSION_OVERLAY = TweakFlag.client(false, CandyGroup.INTERFACE_WINDOW).newForUpdate().alert(TweakAlert.WINDOW_TITLE_DISABLED).build();
    TweakText WINDOW_TITLE_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_WINDOW).newForUpdate().alert(TweakAlert.WINDOW_TITLE_DISABLED).build();

    // Debug Screen

    TweakEnum<Generic> OLD_DEBUG = TweakEnum.client(Generic.BETA, CandyGroup.INTERFACE_DEBUG).newForUpdate().whenDisabled(Generic.MODERN).build();
    TweakFlag DEBUG_ENTITY_ID = TweakFlag.server(true, CandyGroup.INTERFACE_DEBUG).newForUpdate().load().build();

    // Debug Chart

    TweakEnum<DebugChart> FPS_CHART = TweakEnum.client(DebugChart.CLASSIC, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().whenDisabled(DebugChart.MODERN).build();
    TweakFlag SHOW_DEBUG_PIE_CHART = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().build();
    TweakFlag OLD_PIE_CHART_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).newForUpdate().build();

    // Debug Color

    TweakFlag SHOW_DEBUG_LEFT_TEXT_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().build();
    TweakFlag SHOW_DEBUG_RIGHT_TEXT_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().build();
    TweakFlag SHOW_DEBUG_LEFT_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled(true).build();
    TweakFlag SHOW_DEBUG_RIGHT_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled(true).build();
    TweakColor DEBUG_LEFT_BACKGROUND_COLOR = TweakColor.client("#50505090", CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled("#50505090").build();
    TweakColor DEBUG_RIGHT_BACKGROUND_COLOR = TweakColor.client("#50505090", CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled("#50505090").build();
    TweakFlag SHOW_DEBUG_LEFT_TEXT_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().build();
    TweakFlag SHOW_DEBUG_RIGHT_TEXT_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().build();
    TweakColor DEBUG_LEFT_TEXT_COLOR = TweakColor.client("#FFFFFFFF", CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled("#FFFFFFFF").build();
    TweakColor DEBUG_RIGHT_TEXT_COLOR = TweakColor.client("#E0E0E0FF", CandyGroup.INTERFACE_DEBUG_COLOR).newForUpdate().whenDisabled("#FFFFFFFF").build();

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
    TweakFlag INVERTED_PLAYER_LIGHTING = TweakFlag.client(true, CandyGroup.INTERFACE_INVENTORY).newForUpdate().build();
    TweakEnum<Hotbar> OLD_CREATIVE_HOTBAR = TweakEnum.server(Hotbar.CLASSIC, CandyGroup.INTERFACE_INVENTORY).ignoreNetworkCheck().whenDisabled(Hotbar.MODERN).build();

    // Generic Screen

    TweakFlag OLD_BUTTON_TEXT_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_GUI).build();
    TweakEnum<GuiBackground> OLD_GUI_BACKGROUND = TweakEnum.client(GuiBackground.SOLID_BLACK, CandyGroup.INTERFACE_GUI).newForUpdate().build();
    TweakFlag CUSTOM_GUI_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_GUI).newForUpdate().build();
    TweakColor CUSTOM_GUI_TOP_GRADIENT = TweakColor.client("#00000000", CandyGroup.INTERFACE_GUI).newForUpdate().alert(TweakAlert.CUSTOM_GUI_DISABLED).load().build();
    TweakColor CUSTOM_GUI_BOTTOM_GRADIENT = TweakColor.client("#00000000", CandyGroup.INTERFACE_GUI).newForUpdate().alert(TweakAlert.CUSTOM_GUI_DISABLED).load().build();

    // Loading Overlay

    TweakEnum<Overlay> OLD_LOADING_OVERLAY = TweakEnum.client(Overlay.ALPHA, CandyGroup.INTERFACE_LOADING).whenDisabled(Overlay.MODERN).load().build();
    TweakFlag REMOVE_LOADING_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING).modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag CUSTOM_LOADING_OVERLAY_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING_COLOR).newForUpdate().build();
    TweakColor LOADING_OVERLAY_BACKGROUND_COLOR = TweakColor.client("#000000", CandyGroup.INTERFACE_LOADING_COLOR).newForUpdate().build();
    TweakFlag CUSTOM_LOADING_PROGRESS_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING_COLOR).newForUpdate().build();
    TweakColor PROGRESS_BAR_OUTLINE_COLOR = TweakColor.client("#FFFFFF", CandyGroup.INTERFACE_LOADING_COLOR).newForUpdate().build();
    TweakColor PROGRESS_BAR_INSIDE_COLOR = TweakColor.client("#FFFFFF", CandyGroup.INTERFACE_LOADING_COLOR).newForUpdate().build();

    // Progress Screen

    TweakFlag OLD_PROGRESS_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_PROGRESS).build();

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

    // Death Screen

    TweakFlag OLD_DEATH_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_DEATH).newForUpdate().build();
    TweakFlag OLD_DEATH_SCORE = TweakFlag.client(true, CandyGroup.INTERFACE_DEATH).newForUpdate().build();
    TweakFlag HIDE_CAUSE_OF_DEATH = TweakFlag.client(true, CandyGroup.INTERFACE_DEATH).newForUpdate().build();

    // World Select Screen

    TweakEnum<Generic> OLD_WORLD_SELECT_SCREEN = TweakEnum.client(Generic.BETA, CandyGroup.INTERFACE_WORLD_SELECT).newForUpdate().whenDisabled(Generic.MODERN).build();
    TweakFlag ADD_WORLD_THUMBNAIL = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_SELECT).newForUpdate().build();
    TweakFlag ADD_WORLD_METADATA = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_SELECT).newForUpdate().build();
    TweakFlag IGNORE_WORLD_SIZE = TweakFlag.client(false, CandyGroup.INTERFACE_WORLD_SELECT).newForUpdate().build();

    // World Create Screen

    TweakFlag OLD_STYLE_CREATE_WORLD_TABS = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_CREATE).newForUpdate().build();
    TweakFlag REMOVE_CREATE_WORLD_FOOTER = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_CREATE).newForUpdate().build();

    // Title Screen

    TweakFlag OVERRIDE_TITLE_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).build();
    TweakFlag OLD_TITLE_BACKGROUND = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).load().build();
    TweakFlag UNCAP_TITLE_FPS = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).load().build();

    // Title Screen Logo

    TweakFlag OLD_ALPHA_LOGO = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_LOGO).load().build();

    // Title Screen Buttons

    TweakEnum<TitleLayout> TITLE_BUTTON_LAYOUT = TweakEnum.client(TitleLayout.MODERN, CandyGroup.INTERFACE_TITLE_BUTTON).load().build();
    TweakFlag INCLUDE_MODS_ON_TITLE = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().load().build();
    TweakFlag REMOVE_TITLE_REALMS_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().load().build();
    TweakFlag REMOVE_TITLE_ACCESSIBILITY_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).load().build();
    TweakFlag REMOVE_TITLE_LANGUAGE_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).load().build();
    TweakFlag REMOVE_EXTRA_TITLE_BUTTONS = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().load().build();
    TweakFlag ADD_QUIT_BUTTON = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_BUTTON).newForUpdate().load().build();

    // Title Screen Text

    TweakText TITLE_VERSION_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_TITLE_TEXT).load().build();
    TweakFlag TITLE_BOTTOM_LEFT_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_TEXT).load().build();
    TweakFlag TITLE_TOP_RIGHT_DEBUG_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_TEXT).newForUpdate().load().build();
    TweakFlag REMOVE_TITLE_MOD_LOADER_TEXT = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_TEXT).load().build();

    // Tooltips

    TweakFlag OLD_TOOLTIP_BOXES = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP).build();
    TweakFlag OLD_NO_ITEM_TOOLTIPS = TweakFlag.client(false, CandyGroup.INTERFACE_TOOLTIP).build();

    // Tooltip Parts

    TweakFlag SHOW_ENCHANTMENT_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();
    TweakFlag SHOW_MODIFIER_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();
    TweakFlag SHOW_DYE_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).newForUpdate().whenDisabled(true).load().build();

    // Tooltip Color

    TweakEnum<ColorType> TOOLTIP_COLOR_TYPE = TweakEnum.client(ColorType.SOLID, CandyGroup.INTERFACE_TOOLTIP_COLOR).newForUpdate().build();
    TweakColor TOOLTIP_BACKGROUND_COLOR = TweakColor.client("#000000C0", CandyGroup.INTERFACE_TOOLTIP_COLOR).newForUpdate().build();
    TweakColor TOOLTIP_GRADIENT_TOP = TweakColor.client("#FFFFFFC0", CandyGroup.INTERFACE_TOOLTIP_COLOR).newForUpdate().build();
    TweakColor TOOLTIP_GRADIENT_BOTTOM = TweakColor.client("#000000C0", CandyGroup.INTERFACE_TOOLTIP_COLOR).newForUpdate().build();

    // Items

    TweakFlag FIX_ITEM_MODEL_GAP = TweakFlag.client(true, CandyGroup.ITEM).reloadResources().ignoreIf(NostalgicTweaks::isForge).build();
    TweakFlag OLD_DAMAGE_ARMOR_TINT = TweakFlag.client(true, CandyGroup.ITEM).newForUpdate().modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag OLD_ITEM_HOLDING = TweakFlag.client(true, CandyGroup.ITEM).build();
    TweakItemSet IGNORED_HOLDING_ITEMS = TweakItemSet.client(DefaultListing.ignoredHoldingItems(), CandyGroup.ITEM).newForUpdate().build();

    // Item Merging

    TweakFlag OLD_ITEM_MERGING = TweakFlag.server(true, CandyGroup.ITEM_MERGING).build();
    TweakNumber<Integer> ITEM_MERGE_LIMIT = TweakNumber.server(16, CandyGroup.ITEM_MERGING).newForUpdate().slider(Lang.Slider.LIMIT, 1, 64).build();

    // 2D Items

    TweakFlag OLD_2D_ITEMS = TweakFlag.client(true, CandyGroup.ITEM_FLAT).build();
    TweakFlag OLD_2D_RENDERING = TweakFlag.client(true, CandyGroup.ITEM_FLAT).newForUpdate().modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag DISABLE_ENCHANTED_GROUND_ITEMS = TweakFlag.client(false, CandyGroup.ITEM_FLAT).andIf(TweakCondition::areItemsFlat).build();
    TweakFlag DISABLE_ENCHANTED_STATIC_ITEMS = TweakFlag.client(false, CandyGroup.ITEM_FLAT).andIf(TweakCondition::areItemsFlat).build();

    // Item Display

    TweakFlag OLD_DURABILITY_COLORS = TweakFlag.client(true, CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_NO_SELECTED_ITEM_NAME = TweakFlag.client(true, CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_PLAIN_SELECTED_ITEM_NAME = TweakFlag.client(false, CandyGroup.ITEM_DISPLAY).build();

    // Name Tag

    TweakFlag OLD_NAME_TAGS = TweakFlag.client(false, CandyGroup.NAME_TAG).newForUpdate().build();
    TweakFlag SUPPORTER_TAGS = TweakFlag.client(true, CandyGroup.NAME_TAG).newForUpdate().build();

    // Block Lighting

    TweakFlag DISABLE_LIGHT_FLICKER = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).build();
    TweakFlag INVERTED_BLOCK_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).newForUpdate().build();
    TweakFlag OLD_LEAVES_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().build();
    TweakFlag OLD_WATER_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().conflictMods(ModTracker.STARLIGHT).build();
    TweakFlag CHEST_LIGHT_BLOCK = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().conflictMods(ModTracker.STARLIGHT).build();

    // Light Engine

    TweakFlag ROUND_ROBIN_RELIGHT = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_ENGINE).newForUpdate().reloadChunks().modIssues(TweakIssue.SODIUM, TweakIssue.OPTIFINE).conflictMods(ModTracker.STARLIGHT).build();
    TweakFlag OLD_SMOOTH_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_ENGINE).reloadChunks().build();
    TweakFlag OLD_NETHER_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_ENGINE).reloadChunks().build();
    TweakFlag OLD_CLASSIC_ENGINE = TweakFlag.server(false, CandyGroup.LIGHTING_WORLD_ENGINE).newForUpdate().reloadChunks().warningTag().build();

    // Lightmap Texture

    TweakFlag SMOOTH_LIGHT_TRANSITION = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_TEXTURE).newForUpdate().build();
    TweakFlag DISABLE_BRIGHTNESS = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_TEXTURE).newForUpdate().alert(TweakAlert.BRIGHTNESS_CONFLICT).build();
    TweakFlag OLD_LIGHT_COLOR = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_TEXTURE).newForUpdate().build();

    // Shader Support

    TweakNumber<Integer> MAX_BLOCK_LIGHT = TweakNumber.client(15, CandyGroup.LIGHTING_WORLD_SHADER).slider(Lang.Slider.BLOCK_LIGHT, 0, 15).newForUpdate().reloadChunks().build();

    // Disabled Particles

    TweakStringSet DISABLED_PARTICLES = TweakStringSet.client(new StringSet(ListingSuggestion.PARTICLE), CandyGroup.PARTICLE_DISABLED).newForUpdate().build();

    // Experience Particles

    TweakFlag OLD_OPAQUE_EXPERIENCE = TweakFlag.client(true, CandyGroup.PARTICLE_EXPERIENCE).build();

    // Biome Particles

    TweakFlag DISABLE_NETHER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BIOME).newForUpdate().build();
    TweakFlag DISABLE_UNDERWATER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BIOME).newForUpdate().build();

    // Block Particles

    TweakFlag DISABLE_LAVA_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_LAVA_DRIP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_WATER_DRIP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_LEVER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_GROWTH_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_MODEL_DESTRUCTION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_ENDER_CHEST_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_BLOCK).build();

    // Player Particles

    TweakFlag DISABLE_FALLING_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();
    TweakFlag DISABLE_SPRINTING_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();
    TweakFlag HIDE_FIRST_PERSON_MAGIC_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_PLAYER).newForUpdate().build();

    // Attack Particles

    TweakFlag OLD_SWEEP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_DAMAGE_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_CRIT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_MAGIC_HIT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();

    // Explosion Particles

    TweakFlag OLD_EXPLOSION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_EXPLOSION).build();
    TweakFlag OLD_MIXED_EXPLOSION_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_EXPLOSION).build();
    TweakFlag UNOPTIMIZED_EXPLOSION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_EXPLOSION).newForUpdate().build();

    // World

    TweakFlag OLD_SQUARE_BORDER = TweakFlag.server(true, CandyGroup.WORLD).reloadChunks().ignoreNetworkCheck().build();

    // World Fog

    TweakEnum<WorldFog> OLD_WORLD_FOG = TweakEnum.client(WorldFog.ALPHA_R164, CandyGroup.WORLD_FOG).newForUpdate().build();
    TweakBinding FOG_BINDING = TweakBinding.client(-1, CandyGroup.WORLD_FOG, KeybindingId.FOG).build();
    TweakFlag OLD_SUNRISE_SUNSET_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();
    TweakFlag OLD_DARK_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).newForUpdate().build();
    TweakFlag OLD_DYNAMIC_FOG_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG).newForUpdate().alert(TweakAlert.DYNAMIC_FOG).build();
    TweakEnum<FogColor> UNIVERSAL_FOG_COLOR = TweakEnum.client(FogColor.DISABLED, CandyGroup.WORLD_FOG).newForUpdate().alert(TweakAlert.UNIVERSAL_FOG).build();
    TweakFlag OLD_NETHER_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();

    // Custom World Fog

    TweakFlag USE_CUSTOM_OVERWORLD_FOG_DENSITY = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();
    TweakFlag USE_CUSTOM_OVERWORLD_FOG_COLOR = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();
    TweakFlag USE_CUSTOM_NETHER_FOG_DENSITY = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();
    TweakFlag USE_CUSTOM_NETHER_FOG_COLOR = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).newForUpdate().build();

    // Custom Fog Color

    TweakColor CUSTOM_OVERWORLD_FOG_COLOR = TweakColor.client("#FFFFFF", CandyGroup.WORLD_FOG_CUSTOM_COLOR).newForUpdate().alert(TweakAlert.OVERWORLD_FOG_COLOR).build();
    TweakColor CUSTOM_NETHER_FOG_COLOR = TweakColor.client("#540E0E", CandyGroup.WORLD_FOG_CUSTOM_COLOR).newForUpdate().alert(TweakAlert.NETHER_FOG_COLOR).build();

    // Custom Fog Density

    TweakNumber<Integer> CUSTOM_OVERWORLD_FOG_START = TweakNumber.client(100, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).newForUpdate().alert(TweakAlert.OVERWORLD_FOG_DENSITY).slider(Lang.Slider.PERCENTAGE, 0, 100, "%").build();
    TweakNumber<Integer> CUSTOM_OVERWORLD_FOG_END = TweakNumber.client(60, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).newForUpdate().alert(TweakAlert.OVERWORLD_FOG_DENSITY).slider(Lang.Slider.DENSITY, 0, 100, "%").build();
    TweakNumber<Integer> CUSTOM_NETHER_FOG_START = TweakNumber.client(100, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).newForUpdate().alert(TweakAlert.NETHER_FOG_DENSITY).slider(Lang.Slider.PERCENTAGE, 0, 100, "%").build();
    TweakNumber<Integer> CUSTOM_NETHER_FOG_END = TweakNumber.client(60, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).newForUpdate().alert(TweakAlert.NETHER_FOG_DENSITY).slider(Lang.Slider.DENSITY, 0, 100, "%").build();

    // Water Fog

    TweakFlag OLD_WATER_FOG_DENSITY = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();
    TweakFlag OLD_WATER_FOG_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();
    TweakFlag SMOOTH_WATER_DENSITY = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();
    TweakFlag SMOOTH_WATER_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).newForUpdate().build();

    // World Sky

    TweakNumber<Integer> OLD_CLOUD_HEIGHT = TweakNumber.client(128, CandyGroup.WORLD_SKY).range(108, 192).type(SliderType.CLOUD).whenDisabled(192).build();
    TweakFlag RENDER_SUNRISE_SUNSET_COLOR = TweakFlag.client(true, CandyGroup.WORLD_SKY).whenDisabled(true).newForUpdate().build();
    TweakFlag OLD_SUNRISE_AT_NORTH = TweakFlag.client(true, CandyGroup.WORLD_SKY).build();
    TweakEnum<Generic> OLD_STARS = TweakEnum.client(Generic.ALPHA, CandyGroup.WORLD_SKY).whenDisabled(Generic.MODERN).build();
    TweakEnum<SkyColor> UNIVERSAL_SKY_COLOR = TweakEnum.client(SkyColor.DISABLED, CandyGroup.WORLD_SKY).newForUpdate().alert(TweakAlert.UNIVERSAL_SKY).build();
    TweakFlag OLD_DYNAMIC_SKY_COLOR = TweakFlag.client(true, CandyGroup.WORLD_SKY).newForUpdate().alert(TweakAlert.DYNAMIC_SKY).build();
    TweakFlag OLD_NETHER_SKY = TweakFlag.client(true, CandyGroup.WORLD_SKY).newForUpdate().build();

    // Custom Sky

    TweakFlag CUSTOM_OVERWORLD_SKY = TweakFlag.client(false, CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().build();
    TweakColor CUSTOM_OVERWORLD_SKY_COLOR = TweakColor.client("#FFFFFF", CandyGroup.WORLD_SKY_CUSTOM).newForUpdate().build();

    // Void Sky

    TweakEnum<Generic> OLD_BLUE_VOID = TweakEnum.client(Generic.ALPHA, CandyGroup.WORLD_VOID_SKY).whenDisabled(Generic.MODERN).build();
    TweakFlag OLD_BLUE_VOID_OVERRIDE = TweakFlag.client(true, CandyGroup.WORLD_VOID_SKY).build();
    TweakFlag OLD_DARK_VOID_HEIGHT = TweakFlag.client(false, CandyGroup.WORLD_VOID_SKY).alert(TweakAlert.VOID_CONFLICT).build();
    TweakFlag CUSTOM_VOID_SKY = TweakFlag.client(false, CandyGroup.WORLD_VOID_SKY).newForUpdate().build();
    TweakColor CUSTOM_VOID_SKY_COLOR = TweakColor.client("#0000FF", CandyGroup.WORLD_VOID_SKY).newForUpdate().build();

    // Void Fog

    TweakFlag RENDER_VOID_FOG = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakFlag CREATIVE_VOID_FOG = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakFlag CREATIVE_VOID_PARTICLES = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakFlag LIGHT_REMOVES_VOID_FOG = TweakFlag.client(false, CandyGroup.WORLD_VOID_FOG).newForUpdate().build();
    TweakNumber<Integer> VOID_FOG_ENCROACH = TweakNumber.client(70, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.ENCROACH, 0, 100, "%").build();
    TweakNumber<Integer> VOID_FOG_START = TweakNumber.client(50, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.Y_LEVEL, -64, 320).build();
    TweakNumber<Integer> VOID_PARTICLE_START = TweakNumber.client(-47, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.Y_LEVEL, -64, 320).build();
    TweakNumber<Integer> VOID_PARTICLE_RADIUS = TweakNumber.client(16, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.RADIUS, 0, 32).build();
    TweakNumber<Integer> VOID_PARTICLE_DENSITY = TweakNumber.client(20, CandyGroup.WORLD_VOID_FOG).newForUpdate().slider(Lang.Slider.DENSITY, 0, 100, "%").build();
}
