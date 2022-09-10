package mod.adrenix.nostalgic.util.common;

public abstract class LangUtil
{
    public static class Vanilla
    {
        public static final String INVENTORY = "container.inventory";
        public static final String GENERAL = "stat.generalButton";
        public static final String SEARCH = "gui.socialInteractions.search_hint";
        public static final String GUI_ADVANCEMENTS = "gui.advancements";
        public static final String GUI_CANCEL = "gui.cancel";
        public static final String GUI_STATS = "gui.stats";
        public static final String GUI_DONE = "gui.done";
        public static final String READ_WORLD_DATA = "selectWorld.data_read";
        public static final String MENU_SINGLEPLAYER = "menu.singleplayer";
        public static final String MENU_MULTIPLAYER = "menu.multiplayer";
        public static final String MENU_DISCONNECT = "menu.disconnect";
        public static final String MENU_OPTIONS = "menu.options";
        public static final String MENU_RETURN_TO_GAME = "menu.returnToGame";
        public static final String MENU_RETURN_TO_TITLE = "menu.returnToMenu";
        public static final String MENU_QUIT = "menu.quit";
        public static final String MENU_GAME = "menu.game";
        public static final String MENU_LAN = "menu.shareToLan";
        public static final String SAVE_LEVEL = "menu.savingLevel";
    }

    public static class Key
    {
        public static final String OPEN_CONFIG = "key.nostalgic_tweaks.open_config";
        public static final String TOGGLE_FOG = "key.nostalgic_tweaks.toggle_fog";
        public static final String UNBOUND = "key.nostalgic_tweaks.unbound";
        public static final String CATEGORY_NAME = "category.nostalgic_tweaks.name";
    }

    public static class Gui
    {
        public static final String BACKGROUND_SOLID_BLACK = "gui.nostalgic_tweaks.background.solid_black";
        public static final String BACKGROUND_SOLID_BLUE = "gui.nostalgic_tweaks.background.solid_blue";
        public static final String BACKGROUND_GRADIENT_BLUE = "gui.nostalgic_tweaks.background.gradient_blue";

        public static final String RECIPE_BOOK_DISABLED = "gui.nostalgic_tweaks.recipe_book.disabled";
        public static final String RECIPE_BOOK_LARGE = "gui.nostalgic_tweaks.recipe_book.large";
        public static final String RECIPE_BOOK_SMALL = "gui.nostalgic_tweaks.recipe_book.small";

        public static final String DEBUG_CHART_DISABLED = "gui.nostalgic_tweaks.debug_chart.disabled";
        public static final String DEBUG_CHART_CLASSIC = "gui.nostalgic_tweaks.debug_chart.classic";
        public static final String DEBUG_CHART_MODERN = "gui.nostalgic_tweaks.debug_chart.modern";

        public static final String INVENTORY_SHIELD_INVISIBLE = "gui.nostalgic_tweaks.inventory.shield.invisible";
        public static final String INVENTORY_SHIELD_MIDDLE_RIGHT = "gui.nostalgic_tweaks.inventory.shield.middle_right";
        public static final String INVENTORY_SHIELD_BOTTOM_LEFT = "gui.nostalgic_tweaks.inventory.shield.bottom_left";

        public static final String GUI_OVERLAY_LIST = "gui.nostalgic_tweaks.overlay.list";
        public static final String GUI_OVERLAY_LIST_HINT = "gui.nostalgic_tweaks.overlay.list.hint";
        public static final String GUI_OVERLAY_COLOR = "gui.nostalgic_tweaks.overlay.color";
        public static final String GUI_OVERLAY_COLOR_HINT = "gui.nostalgic_tweaks.overlay.color.hint";
        public static final String GUI_OVERLAY_INPUT_TIP = "gui.nostalgic_tweaks.overlay.input.@Tooltip";
        public static final String GUI_OVERLAY_DRAG_TIP = "gui.nostalgic_tweaks.overlay.drag.@Tooltip";

        public static final String TOAST_TWEAK_UPDATE_HEADER = "gui.nostalgic_tweaks.settings.toast.tweak_update_header";
        public static final String TOAST_TWEAK_UPDATE_BODY = "gui.nostalgic_tweaks.settings.toast.tweak_update_body";
        public static final String TOAST_TWEAK_SENT_HEADER = "gui.nostalgic_tweaks.settings.toast.tweak_sent_header";
        public static final String TOAST_TWEAK_SENT_BODY = "gui.nostalgic_tweaks.settings.toast.tweak_sent_body";
        public static final String TOAST_SYNC_HEADER = "gui.nostalgic_tweaks.settings.toast.tweak_sync_header";
        public static final String TOAST_SYNC_BODY = "gui.nostalgic_tweaks.settings.toast.tweak_sync_body";

        public static final String SETTINGS_PRESETS = "gui.nostalgic_tweaks.settings.presets";
        public static final String SETTINGS_TITLE = "gui.nostalgic_tweaks.settings.title";
        public static final String SETTINGS_SUPPORT = "gui.nostalgic_tweaks.settings.support";
        public static final String SETTINGS_DISCORD = "gui.nostalgic_tweaks.settings.discord";
        public static final String SETTINGS_GOLDEN_DAYS = "gui.nostalgic_tweaks.settings.golden_days";

        public static final String CUSTOMIZE = "gui.nostalgic_tweaks.settings.customize";
        public static final String CUSTOMIZE_TOOL = "gui.nostalgic_tweaks.customize.tool";
        public static final String CUSTOMIZE_BLOCK = "gui.nostalgic_tweaks.customize.block";
        public static final String CUSTOMIZE_ITEM = "gui.nostalgic_tweaks.customize.item";
        public static final String CUSTOMIZE_RESET = "gui.nostalgic_tweaks.customize.reset";
        public static final String CUSTOMIZE_RESET_TOOLTIP_0 = "gui.nostalgic_tweaks.customize.reset.@Tooltip[0]";
        public static final String CUSTOMIZE_RESET_TOOLTIP_1 = "gui.nostalgic_tweaks.customize.reset.@Tooltip[1]";
        public static final String CUSTOMIZE_ADD = "gui.nostalgic_tweaks.customize.add";
        public static final String CUSTOMIZE_ADD_TOOLTIP = "gui.nostalgic_tweaks.customize.add.@Tooltip";
        public static final String CUSTOMIZE_AUTOFILL_TOOLTIP = "gui.nostalgic_tweaks.customize.autofill.@Tooltip";
        public static final String CUSTOMIZE_RANGE_TOOLTIP_0 = "gui.nostalgic_tweaks.customize.range.@Tooltip[0]";
        public static final String CUSTOMIZE_RANGE_TOOLTIP_1 = "gui.nostalgic_tweaks.customize.range.@Tooltip[1]";
        public static final String CUSTOMIZE_RANGE_TOOLTIP_2 = "gui.nostalgic_tweaks.customize.range.@Tooltip[2]";
        public static final String CUSTOMIZE_REMOVE_TOOLTIP = "gui.nostalgic_tweaks.customize.remove.@Tooltip";
        public static final String CUSTOMIZE_UNDO_TOOLTIP = "gui.nostalgic_tweaks.customize.undo.@Tooltip";
        public static final String CUSTOMIZE_UNKNOWN = "gui.nostalgic_tweaks.customize.unknown";
        public static final String CUSTOMIZE_HAND = "gui.nostalgic_tweaks.customize.hand";

        public static final String LEVEL_LOADING = "gui.nostalgic_tweaks.level.loading";
        public static final String LEVEL_BUILDING = "gui.nostalgic_tweaks.level.building";
        public static final String LEVEL_SIMULATE = "gui.nostalgic_tweaks.level.simulate";
        public static final String LEVEL_SAVING = "gui.nostalgic_tweaks.level.saving";
        public static final String LEVEL_ENTER_NETHER = "gui.nostalgic_tweaks.level.enterNether";
        public static final String LEVEL_ENTER_END = "gui.nostalgic_tweaks.level.enterEnd";
        public static final String LEVEL_LEAVING_NETHER = "gui.nostalgic_tweaks.level.leaveNether";
        public static final String LEVEL_LEAVING_END = "gui.nostalgic_tweaks.level.leaveEnd";

        public static final String PAUSE_GAME = "gui.nostalgic_tweaks.pause.game";
        public static final String PAUSE_ACHIEVEMENTS = "gui.nostalgic_tweaks.pause.achievements";
        public static final String PAUSE_RETURN_LOWER = "gui.nostalgic_tweaks.pause.return";
        public static final String PAUSE_SAVE_LOWER = "gui.nostalgic_tweaks.pause.save";

        public static final String SETTINGS_ALL = "gui.nostalgic_tweaks.config.all";
        public static final String SETTINGS_SPEED = "gui.nostalgic_tweaks.config.speed";
        public static final String SETTINGS_SPEED_HELP = "gui.nostalgic_tweaks.config.speed.@PrefixText";
        public static final String SETTINGS_INTENSITY = "gui.nostalgic_tweaks.config.intensity";
        public static final String SETTINGS_CUSTOM = "gui.nostalgic_tweaks.config.custom";
        public static final String SETTINGS_CLASSIC = "gui.nostalgic_tweaks.config.classic";
        public static final String SETTINGS_ALPHA = "gui.nostalgic_tweaks.config.alpha";
        public static final String SETTINGS_BETA = "gui.nostalgic_tweaks.config.beta";
        public static final String SETTINGS_MODERN = "gui.nostalgic_tweaks.config.modern";
        public static final String DEFAULT = "gui.nostalgic_tweaks.config.default";

        public static final String SLIDER_LIMIT = "gui.nostalgic_tweaks.config.slider.limit";
        public static final String SLIDER_RADIUS = "gui.nostalgic_tweaks.config.slider.radius";
        public static final String SLIDER_DENSITY = "gui.nostalgic_tweaks.config.slider.density";
        public static final String SLIDER_ENCROACH = "gui.nostalgic_tweaks.config.slider.encroach";
        public static final String SLIDER_Y_LEVEL = "gui.nostalgic_tweaks.config.slider.y_level";

        public static final String CORNER_TOP_LEFT = "gui.nostalgic_tweaks.config.corner.top_left";
        public static final String CORNER_TOP_RIGHT = "gui.nostalgic_tweaks.config.corner.top_right";
        public static final String CORNER_BOTTOM_LEFT = "gui.nostalgic_tweaks.config.corner.bottom_left";
        public static final String CORNER_BOTTOM_RIGHT = "gui.nostalgic_tweaks.config.corner.bottom_right";

        public static final String STATE_SHIFT = "gui.nostalgic_tweaks.config.state.shift";
        public static final String STATE_FUZZY = "gui.nostalgic_tweaks.config.state.fuzzy";
        public static final String STATE_FUZZY_TOOLTIP = "gui.nostalgic_tweaks.config.state.fuzzy.@Tooltip";
        public static final String STATE_BUBBLE = "gui.nostalgic_tweaks.config.state.bubble";
        public static final String STATE_BUBBLE_TOOLTIP = "gui.nostalgic_tweaks.config.state.bubble.@Tooltip";
        public static final String STATE_CLEAR = "gui.nostalgic_tweaks.config.state.clear";
        public static final String STATE_CLEAR_TOOLTIP = "gui.nostalgic_tweaks.config.state.clear.@Tooltip";
        public static final String STATE_TAG = "gui.nostalgic_tweaks.config.state.tag";
        public static final String STATE_TAG_TOOLTIP = "gui.nostalgic_tweaks.config.state.tag.@Tooltip";

        public static final String STATUS_WAIT = "gui.nostalgic_tweaks.config.status.wait";
        public static final String STATUS_WARN = "gui.nostalgic_tweaks.config.status.warn";
        public static final String STATUS_FAIL = "gui.nostalgic_tweaks.config.status.fail";
        public static final String STATUS_PERM = "gui.nostalgic_tweaks.config.status.perm";
        public static final String STATUS_NET = "gui.nostalgic_tweaks.config.status.net";
        public static final String STATUS_DYNAMIC_OP = "gui.nostalgic_tweaks.config.status.dynamic.op";
        public static final String STATUS_DYNAMIC_ON = "gui.nostalgic_tweaks.config.status.dynamic.on";
        public static final String STATUS_DYNAMIC_OFF = "gui.nostalgic_tweaks.config.status.dynamic.off";

        public static final String TAG_NEW = "gui.nostalgic_tweaks.config.tag.new";
        public static final String TAG_NEW_TOOLTIP = "gui.nostalgic_tweaks.config.tag.new.@Tooltip";
        public static final String TAG_CLIENT = "gui.nostalgic_tweaks.config.tag.client";
        public static final String TAG_CLIENT_TOOLTIP = "gui.nostalgic_tweaks.config.tag.client.@Tooltip";
        public static final String TAG_SERVER = "gui.nostalgic_tweaks.config.tag.server";
        public static final String TAG_SERVER_TOOLTIP = "gui.nostalgic_tweaks.config.tag.server.@Tooltip";
        public static final String TAG_DYNAMIC = "gui.nostalgic_tweaks.config.tag.dynamic";
        public static final String TAG_DYNAMIC_TOOLTIP = "gui.nostalgic_tweaks.config.tag.dynamic.@Tooltip";
        public static final String TAG_RELOAD = "gui.nostalgic_tweaks.config.tag.reload";
        public static final String TAG_RELOAD_TOOLTIP = "gui.nostalgic_tweaks.config.tag.reload.@Tooltip";
        public static final String TAG_RESTART = "gui.nostalgic_tweaks.config.tag.restart";
        public static final String TAG_RESTART_TOOLTIP = "gui.nostalgic_tweaks.config.tag.restart.@Tooltip";
        public static final String TAG_WARNING = "gui.nostalgic_tweaks.config.tag.warning";
        public static final String TAG_SYNC = "gui.nostalgic_tweaks.config.tag.sync";
        public static final String TAG_SYNC_TOOLTIP = "gui.nostalgic_tweaks.config.tag.sync.@Tooltip";
        public static final String TAG_AUTO = "gui.nostalgic_tweaks.config.tag.auto";
        public static final String TAG_AUTO_TOOLTIP = "gui.nostalgic_tweaks.config.tag.auto.@Tooltip";

        public static final String SEARCH_EMPTY = "gui.nostalgic_tweaks.config.search.empty";
        public static final String SEARCH_INVALID = "gui.nostalgic_tweaks.config.search.invalidTag";

        public static final String GENERAL_BINDINGS = "gui.nostalgic_tweaks.config.general.bindings.title";
        public static final String GENERAL_OVERRIDE_TITLE = "gui.nostalgic_tweaks.config.general.override.title";
        public static final String GENERAL_OVERRIDE_HELP = "gui.nostalgic_tweaks.config.general.override.help";
        public static final String GENERAL_OVERRIDE_DISABLE = "gui.nostalgic_tweaks.config.general.override.disable";
        public static final String GENERAL_OVERRIDE_ENABLE = "gui.nostalgic_tweaks.config.general.override.enable";
        public static final String GENERAL_OVERRIDE_SERVER = "gui.nostalgic_tweaks.config.general.override.server";
        public static final String GENERAL_OVERRIDE_SERVER_TIP = "gui.nostalgic_tweaks.config.general.override.server.@Tooltip";
        public static final String GENERAL_CONFIG_TITLE = "gui.nostalgic_tweaks.config.general.config.title";
        public static final String GENERAL_CONFIG_SCREEN_TITLE = "gui.nostalgic_tweaks.config.general.config.screen.title";
        public static final String GENERAL_CONFIG_SCREEN_MAIN = "gui.nostalgic_tweaks.config.general.config.screen.main";
        public static final String GENERAL_CONFIG_SCREEN_SETTINGS = "gui.nostalgic_tweaks.config.general.config.screen.settings";
        public static final String GENERAL_CONFIG_SCREEN_CUSTOM = "gui.nostalgic_tweaks.config.general.config.screen.custom";
        public static final String GENERAL_CONFIG_SCREEN_INFO = "gui.nostalgic_tweaks.config.general.config.screen.info";
        public static final String GENERAL_CONFIG_TREE_TITLE = "gui.nostalgic_tweaks.config.general.config.tree.title";
        public static final String GENERAL_CONFIG_TREE_INFO = "gui.nostalgic_tweaks.config.general.config.tree.info";
        public static final String GENERAL_CONFIG_ROW_TITLE = "gui.nostalgic_tweaks.config.general.config.row.title";
        public static final String GENERAL_CONFIG_ROW_INFO = "gui.nostalgic_tweaks.config.general.config.row.info";
        public static final String GENERAL_CONFIG_TAGS_TITLE = "gui.nostalgic_tweaks.config.general.config.tags.title";
        public static final String GENERAL_CONFIG_TAGS_INFO = "gui.nostalgic_tweaks.config.general.config.tags.info";
        public static final String GENERAL_CONFIG_NEW_TAGS_LABEL = "gui.nostalgic_tweaks.config.general.config.newTags.label";
        public static final String GENERAL_CONFIG_SIDED_TAGS_LABEL = "gui.nostalgic_tweaks.config.general.config.sidedTags.label";
        public static final String GENERAL_CONFIG_TAG_TOOLTIPS_LABEL = "gui.nostalgic_tweaks.config.general.config.tagTooltips.label";
        public static final String GENERAL_CONFIG_TWEAK_STATUS_HELP = "gui.nostalgic_tweaks.config.general.config.tweakStatus.help";
        public static final String GENERAL_CONFIG_TWEAK_STATUS_LABEL = "gui.nostalgic_tweaks.config.general.config.tweakStatus.label";
        public static final String GENERAL_NOTIFY_TITLE = "gui.nostalgic_tweaks.config.general.notify.title";
        public static final String GENERAL_NOTIFY_CONFLICT = "gui.nostalgic_tweaks.config.general.notify.conflict";
        public static final String GENERAL_SEARCH_TITLE = "gui.nostalgic_tweaks.config.general.search.title";
        public static final String GENERAL_SEARCH_HELP = "gui.nostalgic_tweaks.config.general.search.help";
        public static final String GENERAL_SEARCH_NEW = "gui.nostalgic_tweaks.config.general.search.new";
        public static final String GENERAL_SEARCH_CONFLICT = "gui.nostalgic_tweaks.config.general.search.conflict";
        public static final String GENERAL_SEARCH_RESET = "gui.nostalgic_tweaks.config.general.search.reset";
        public static final String GENERAL_SEARCH_CLIENT = "gui.nostalgic_tweaks.config.general.search.client";
        public static final String GENERAL_SEARCH_SERVER = "gui.nostalgic_tweaks.config.general.search.server";
        public static final String GENERAL_SEARCH_SAVE = "gui.nostalgic_tweaks.config.general.search.save";
        public static final String GENERAL_SEARCH_ALL = "gui.nostalgic_tweaks.config.general.search.all";
        public static final String GENERAL_SHORTCUT_TITLE = "gui.nostalgic_tweaks.config.general.shortcut.title";
        public static final String GENERAL_SHORTCUT_HELP = "gui.nostalgic_tweaks.config.general.shortcut.help";
        public static final String GENERAL_SHORTCUT_FIND = "gui.nostalgic_tweaks.config.general.shortcut.find";
        public static final String GENERAL_SHORTCUT_SAVE = "gui.nostalgic_tweaks.config.general.shortcut.save";
        public static final String GENERAL_SHORTCUT_EXIT = "gui.nostalgic_tweaks.config.general.shortcut.exit";
        public static final String GENERAL_SHORTCUT_JUMP = "gui.nostalgic_tweaks.config.general.shortcut.jump";
        public static final String GENERAL_SHORTCUT_ALL = "gui.nostalgic_tweaks.config.general.shortcut.all";
        public static final String GENERAL_SHORTCUT_GROUP = "gui.nostalgic_tweaks.config.general.shortcut.group";

        public static final String SOUND_CATEGORY_BLOCK = "gui.nostalgic_tweaks.config.sound.category.block";
        public static final String SOUND_CATEGORY_DAMAGE = "gui.nostalgic_tweaks.config.sound.category.damage";
        public static final String SOUND_CATEGORY_EXPERIENCE = "gui.nostalgic_tweaks.config.sound.category.experience";
        public static final String SOUND_CATEGORY_MOB = "gui.nostalgic_tweaks.config.sound.category.mob";

        public static final String SOUND_SUBCATEGORY_CHEST = "gui.nostalgic_tweaks.config.sound.subcategory.chest";

        public static final String GAMEPLAY_CATEGORY_BUG = "gui.nostalgic_tweaks.config.gameplay.category.bug";
        public static final String GAMEPLAY_CATEGORY_COMBAT = "gui.nostalgic_tweaks.config.gameplay.category.combat";
        public static final String GAMEPLAY_CATEGORY_EXPERIENCE = "gui.nostalgic_tweaks.config.gameplay.category.experience";
        public static final String GAMEPLAY_CATEGORY_HUNGER = "gui.nostalgic_tweaks.config.gameplay.category.hunger";
        public static final String GAMEPLAY_CATEGORY_MECHANICS = "gui.nostalgic_tweaks.config.gameplay.category.mechanics";

        public static final String GAMEPLAY_SUBCATEGORY_BOW = "gui.nostalgic_tweaks.config.gameplay.subcategory.bow";
        public static final String GAMEPLAY_SUBCATEGORY_EXPERIENCE_BAR = "gui.nostalgic_tweaks.config.gameplay.subcategory.experience_bar";
        public static final String GAMEPLAY_SUBCATEGORY_EXPERIENCE_ORB = "gui.nostalgic_tweaks.config.gameplay.subcategory.experience_orb";
        public static final String GAMEPLAY_SUBCATEGORY_EXPERIENCE_BLOCK = "gui.nostalgic_tweaks.config.gameplay.subcategory.experience_block";
        public static final String GAMEPLAY_SUBCATEGORY_FIRE = "gui.nostalgic_tweaks.config.gameplay.subcategory.fire";
        public static final String GAMEPLAY_SUBCATEGORY_SWIMMING = "gui.nostalgic_tweaks.config.gameplay.subcategory.swimming";
        public static final String GAMEPLAY_SUBCATEGORY_HUNGER_BAR = "gui.nostalgic_tweaks.config.gameplay.subcategory.hunger_bar";
        public static final String GAMEPLAY_SUBCATEGORY_FOOD = "gui.nostalgic_tweaks.config.gameplay.subcategory.food";

        public static final String GAMEPLAY_EMBED_XP_LEVEL = "gui.nostalgic_tweaks.config.gameplay.embed.experience.level";
        public static final String GAMEPLAY_EMBED_XP_PROGRESS = "gui.nostalgic_tweaks.config.gameplay.embed.experience.progress";
        public static final String GAMEPLAY_EMBED_HUNGER_FOOD = "gui.nostalgic_tweaks.config.gameplay.embed.hunger.food";
        public static final String GAMEPLAY_EMBED_HUNGER_SATURATION = "gui.nostalgic_tweaks.config.gameplay.embed.hunger.saturation";

        public static final String ANIMATION_CATEGORY_ARM = "gui.nostalgic_tweaks.config.animation.category.arm";
        public static final String ANIMATION_CATEGORY_MOB = "gui.nostalgic_tweaks.config.animation.category.mob";
        public static final String ANIMATION_CATEGORY_PLAYER = "gui.nostalgic_tweaks.config.animation.category.player";
        public static final String ANIMATION_CATEGORY_ITEM = "gui.nostalgic_tweaks.config.animation.category.item";

        public static final String CANDY_CATEGORY_PARTICLE = "gui.nostalgic_tweaks.config.candy.category.particle";
        public static final String CANDY_CATEGORY_LIGHTING = "gui.nostalgic_tweaks.config.candy.category.lighting";
        public static final String CANDY_CATEGORY_WORLD = "gui.nostalgic_tweaks.config.candy.category.world";
        public static final String CANDY_CATEGORY_ITEM = "gui.nostalgic_tweaks.config.candy.category.item";
        public static final String CANDY_CATEGORY_GUI = "gui.nostalgic_tweaks.config.candy.category.interface";
        public static final String CANDY_CATEGORY_BLOCK = "gui.nostalgic_tweaks.config.candy.category.block";

        public static final String CANDY_SUBCATEGORY_CHEST = "gui.nostalgic_tweaks.config.candy.subcategory.chest";
        public static final String CANDY_SUBCATEGORY_CHAT = "gui.nostalgic_tweaks.config.candy.subcategory.chat";
        public static final String CANDY_SUBCATEGORY_GUI = "gui.nostalgic_tweaks.config.candy.subcategory.gui";
        public static final String CANDY_SUBCATEGORY_CRAFTING = "gui.nostalgic_tweaks.config.candy.subcategory.crafting";
        public static final String CANDY_SUBCATEGORY_DEBUG = "gui.nostalgic_tweaks.config.candy.subcategory.debug";
        public static final String CANDY_SUBCATEGORY_FURNACE = "gui.nostalgic_tweaks.config.candy.subcategory.furnace";
        public static final String CANDY_SUBCATEGORY_INVENTORY = "gui.nostalgic_tweaks.config.candy.subcategory.inventory";
        public static final String CANDY_SUBCATEGORY_ITEM = "gui.nostalgic_tweaks.config.candy.subcategory.item";
        public static final String CANDY_SUBCATEGORY_MERGE = "gui.nostalgic_tweaks.config.candy.subcategory.merge";
        public static final String CANDY_SUBCATEGORY_TITLE = "gui.nostalgic_tweaks.config.candy.subcategory.title";
        public static final String CANDY_SUBCATEGORY_LOADING = "gui.nostalgic_tweaks.config.candy.subcategory.loading";
        public static final String CANDY_SUBCATEGORY_VERSION = "gui.nostalgic_tweaks.config.candy.subcategory.version";
        public static final String CANDY_SUBCATEGORY_PAUSE = "gui.nostalgic_tweaks.config.candy.subcategory.pause";
        public static final String CANDY_SUBCATEGORY_TOOLTIP = "gui.nostalgic_tweaks.config.candy.subcategory.tooltip";
        public static final String CANDY_SUBCATEGORY_FLAT = "gui.nostalgic_tweaks.config.candy.subcategory.flat";
        public static final String CANDY_SUBCATEGORY_BLOCK_LIGHT = "gui.nostalgic_tweaks.config.candy.subcategory.block_light";
        public static final String CANDY_SUBCATEGORY_WORLD_LIGHT = "gui.nostalgic_tweaks.config.candy.subcategory.world_light";
        public static final String CANDY_SUBCATEGORY_ATTACK = "gui.nostalgic_tweaks.config.candy.subcategory.attack";
        public static final String CANDY_SUBCATEGORY_BLOCK_PARTICLES = "gui.nostalgic_tweaks.config.candy.subcategory.block_particles";
        public static final String CANDY_SUBCATEGORY_EXPLOSION = "gui.nostalgic_tweaks.config.candy.subcategory.explosion";
        public static final String CANDY_SUBCATEGORY_PLAYER = "gui.nostalgic_tweaks.config.candy.subcategory.player";
        public static final String CANDY_SUBCATEGORY_FOG = "gui.nostalgic_tweaks.config.candy.subcategory.fog";
        public static final String CANDY_SUBCATEGORY_SKY = "gui.nostalgic_tweaks.config.candy.subcategory.sky";
        public static final String CANDY_SUBCATEGORY_VOID = "gui.nostalgic_tweaks.config.candy.subcategory.void";

        public static final String CANDY_EMBED_TITLE_LOGO = "gui.nostalgic_tweaks.config.candy.embed.title.logo";
        public static final String CANDY_EMBED_TITLE_BUTTON = "gui.nostalgic_tweaks.config.candy.embed.title.button";
        public static final String CANDY_EMBED_TITLE_TEXT = "gui.nostalgic_tweaks.config.candy.embed.title.text";
        public static final String CANDY_EMBED_TOOLTIP_PARTS = "gui.nostalgic_tweaks.config.candy.embed.tooltip.parts";
        public static final String CANDY_EMBED_VOID_FOG = "gui.nostalgic_tweaks.config.candy.embed.void.fog";
        public static final String CANDY_EMBED_VOID_SKY = "gui.nostalgic_tweaks.config.candy.embed.void.sky";

        public static final String SWING_CATEGORY_ITEM = "gui.nostalgic_tweaks.config.swing.category.item";
        public static final String SWING_CATEGORY_POTION = "gui.nostalgic_tweaks.config.swing.category.potion";

        public static final String CANDY_TITLE_MODS = "gui.nostalgic_tweaks.config.candy.title.mods";
        public static final String CANDY_TITLE_MODS_TEXTURE = "gui.nostalgic_tweaks.config.candy.title.mods_texture";
        public static final String CANDY_TITLE_TEXTURE_PACK = "gui.nostalgic_tweaks.config.candy.title.texture_pack";
        public static final String CANDY_TITLE_TUTORIAL = "gui.nostalgic_tweaks.config.candy.title.tutorial";
        public static final String CANDY_TITLE_COPYRIGHT_ALPHA = "gui.nostalgic_tweaks.config.candy.title.copyright.alpha";
        public static final String CANDY_TITLE_COPYRIGHT_BETA = "gui.nostalgic_tweaks.config.candy.title.copyright.beta";
    }

    public static class Cloth
    {
        public static final String CONFIG_TITLE = "text.autoconfig.nostalgic_tweaks.title";
        public static final String SOUND_TITLE = "text.autoconfig.nostalgic_tweaks.option.sound";
        public static final String CANDY_TITLE = "text.autoconfig.nostalgic_tweaks.option.eyeCandy";
        public static final String GAMEPLAY_TITLE = "text.autoconfig.nostalgic_tweaks.option.gameplay";
        public static final String ANIMATION_TITLE = "text.autoconfig.nostalgic_tweaks.option.animation";
        public static final String SWING_TITLE = "text.autoconfig.nostalgic_tweaks.option.swing";
        public static final String QUIT_CONFIG = "text.cloth-config.quit_config";
        public static final String QUIT_CONFIG_SURE = "text.cloth-config.quit_config_sure";
        public static final String QUIT_DISCARD = "text.cloth-config.quit_discard";
        public static final String SAVE_AND_DONE = "text.cloth-config.save_and_done";
        public static final String RESET = "text.cloth-config.reset_value";
        public static final String YES = "text.cloth-config.boolean.value.true";
        public static final String NO = "text.cloth-config.boolean.value.false";
    }
}
