package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.group.ModGroup;

public abstract class ModTweak
{
    /**
     * A global mod state flag that controls whether all tweaks should be off or in their configured state.
     */
    public static final Tweak<Boolean> ENABLED = Tweak.builder(true, TweakSide.CLIENT, TweakCategory.ROOT).ignore().noTooltip().load().build();

    /**
     * Control flag that determines if the user has successfully interacted with the tutorial toast.
     * If so, then the toast will not appear the next time a title screen is displayed.
     */
    public static final Tweak<Boolean> INTERACTED_WITH_CONFIG = Tweak.builder(false, TweakSide.CLIENT, TweakCategory.MOD).ignore().load().build();

    /**
     * Control flag that determines if the donator banner is displayed when the settings screen is opened.
     * This field is saved when the banner toggle button is clicked.
     */
    public static final Tweak<Boolean> DISPLAY_DONATOR_BANNER = Tweak.builder(true, TweakSide.CLIENT, TweakCategory.MOD).ignore().load().build();

    /**
     * Enumeration option that determines which screen is opened when the defined hotkey is pressed.
     */
    public static final Tweak<MenuOption> DEFAULT_SCREEN = Tweak.builder(MenuOption.MAIN_MENU, TweakSide.CLIENT, ModGroup.MENU).ignore().load().build();

    /**
     * Controls how many backup files are made in the user's backup folder directory.
     */
    public static final Tweak<Integer> NUMBER_OF_BACKUPS = Tweak.builder(5, TweakSide.CLIENT, ModGroup.CONFIG).ignore().load().build();

    /**
     * Controls if "New" tags are displayed next to tweak names.
     */
    public static final Tweak<Boolean> DISPLAY_NEW_TAGS = Tweak.builder(true, TweakSide.CLIENT, ModGroup.TAGS).ignore().load().build();

    /**
     * Controls if "Client", "Server", or "Dynamic" tags are displayed next to tweak names.
     */
    public static final Tweak<Boolean> DISPLAY_SIDED_TAGS = Tweak.builder(true, TweakSide.CLIENT, ModGroup.TAGS).ignore().load().build();

    /**
     * Controls if tooltips are displayed when the mouse hovers over tweak tags.
     */
    public static final Tweak<Boolean> DISPLAY_TAG_TOOLTIPS = Tweak.builder(true, TweakSide.CLIENT, ModGroup.TAGS).ignore().load().build();

    /**
     * Controls if the "!" tweak status symbol is displayed next to tweak controller buttons.
     */
    public static final Tweak<Boolean> DISPLAY_TWEAK_STATUS = Tweak.builder(true, TweakSide.CLIENT, ModGroup.STATUS).ignore().load().build();

    /**
     * Controls if the category tree is rendered in the configuration menu.
     */
    public static final Tweak<Boolean> DISPLAY_CATEGORY_TREE = Tweak.builder(true, TweakSide.CLIENT, ModGroup.TREE).ignore().load().build();

    /**
     * Changes the color of the category tree.
     */
    public static final Tweak<String> CATEGORY_TREE_COLOR = Tweak.builder("#8B8B8B7F", TweakSide.CLIENT, ModGroup.TREE).ignore().colorTweak().load().build();

    /**
     * Controls if a row is highlighted when the mouse hovers it.
     */
    public static final Tweak<Boolean> DISPLAY_ROW_HIGHLIGHT = Tweak.builder(true, TweakSide.CLIENT, ModGroup.ROWS).ignore().load().build();

    /**
     * Controls if row highlighting fades in and out.
     */
    public static final Tweak<Boolean> DO_ROW_HIGHLIGHT_FADE = Tweak.builder(true, TweakSide.CLIENT, ModGroup.ROWS).ignore().load().build();

    /**
     * Changes the color of row the highlighting background.
     */
    public static final Tweak<String> ROW_HIGHLIGHT_COLOR = Tweak.builder("#FFFFFF32", TweakSide.CLIENT, ModGroup.ROWS).ignore().colorTweak().load().build();
}
