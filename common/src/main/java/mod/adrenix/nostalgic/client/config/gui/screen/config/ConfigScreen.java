package mod.adrenix.nostalgic.client.config.gui.screen.config;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakCategory;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakEmbed;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakSubcategory;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryListOverlay;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.SearchCrumbs;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.KeyBindButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.OverlapButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the screen that appears when the settings button is clicked. Most of the user's time will be spent here.
 * Tweaks are separated by groups. Within those groups are categories, subcategories, and embedded subcategories.
 *
 * All groups will be handled by {@link ConfigRowList}.
 */

public class ConfigScreen extends Screen
{
    /* Configuration Tabs */

    /**
     * This enumeration provides the tabs listed near the top of this screen.
     * Special tabs such as ALL and SEARCH are handled manually by different classes.
     */
    public enum ConfigTab
    {
        ALL(LangUtil.Gui.SETTINGS_ALL),
        GENERAL(LangUtil.Vanilla.GENERAL),
        SOUND(LangUtil.Config.SOUND_TITLE),
        CANDY(LangUtil.Config.CANDY_TITLE),
        GAMEPLAY(LangUtil.Config.GAMEPLAY_TITLE),
        ANIMATION(LangUtil.Config.ANIMATION_TITLE),
        SWING(LangUtil.Config.SWING_TITLE),
        SEARCH(LangUtil.Vanilla.SEARCH);

        ConfigTab(String langKey) { this.langKey = langKey; }

        private final String langKey;
        public String getLangKey() { return this.langKey; }
    }

    /* Instance Fields */

    public final ArrayList<Runnable> renderLast = new ArrayList<>();
    protected final Map<String, TweakClientCache<?>> search = new TreeMap<>();
    private final Minecraft minecraft;
    private final Screen parentScreen;
    private ConfigWidgets widgetProvider;
    private ConfigRenderer rendererProvider;
    private ConfigTab configTab = ConfigTab.GENERAL;
    private String searchCache = "";
    private int rowHeightCache = 0;
    private double scrollAmountCache = 0.0D;
    private static boolean isCacheReflected = false;

    /* Getters */

    public Font getFont() { return this.font; }
    public Minecraft getMinecraft() { return this.minecraft; }
    public ConfigWidgets getWidgets() { return this.widgetProvider; }
    public ConfigRenderer getRenderer() { return this.rendererProvider; }
    public ConfigTab getConfigTab() { return this.configTab; }

    /* Constructors */

    /**
     * Get a configuration screen with a custom title.
     * @param parentScreen The parent screen.
     * @param title The configuration screen's title.
     */
    public ConfigScreen(Screen parentScreen, Component title)
    {
        super(title);

        this.minecraft = Minecraft.getInstance();
        this.parentScreen = parentScreen;

        if (Minecraft.getInstance().level != null && !ConfigScreen.isCacheReflected)
        {
            ConfigScreen.isCacheReflected = true;

            TweakClientCache.all().forEach((key, tweak) ->
            {
                TweakData.EntryStatus entryStatus = tweak.getMetadata(TweakData.EntryStatus.class);

                if (entryStatus != null && tweak.getStatus() == TweakStatus.WAIT)
                    tweak.setStatus(TweakStatus.FAIL);
            });

            TweakServerCache.all().forEach((key, tweak) ->
            {
                TweakData.EntryStatus entryStatus = tweak.getMetadata(TweakData.EntryStatus.class);

                if (entryStatus != null && tweak.getStatus() == TweakStatus.WAIT)
                    tweak.setStatus(TweakStatus.FAIL);
            });
        }

        NostalgicTweaks.LOGGER.debug("Found %s possibly conflicted tweaks", TweakClientCache.getConflicts());
    }

    /**
     * Get a configuration screen with the standard configuration title.
     * @param parentScreen The parent screen.
     */
    public ConfigScreen(Screen parentScreen)
    {
        this(parentScreen, Component.translatable(LangUtil.Config.CONFIG_TITLE));
    }

    /* Screen Methods */

    /**
     * Caches important widget values so that those values can be restored at a later point in time.
     */
    public void setupCache()
    {
        this.searchCache = this.getWidgets().getSearchInput().getValue();
        this.rowHeightCache = this.getWidgets().getConfigRowList().getRowHeight();
        this.scrollAmountCache = this.getWidgets().getConfigRowList().getScrollAmount();
    }

    /**
     * Restores important widget values that were cached at an earlier point in time.
     */
    public void restoreCache()
    {
        this.getWidgets().getSearchInput().setValue(this.searchCache);
        this.getWidgets().getConfigRowList().setRowHeight(this.rowHeightCache);

        if (this.configTab == ConfigTab.SEARCH)
            this.getWidgets().getSearchInput().setVisible(true);
    }

    /**
     * Initializes the configuration screen.
     */
    @Override
    protected void init()
    {
        this.widgetProvider = new ConfigWidgets(this);
        this.rendererProvider = new ConfigRenderer(this);
        this.getWidgets().generate();
    }

    /**
     * Handler method for when the game window is resized.
     * @param minecraft A singleton Minecraft instance.
     * @param width The new game window width.
     * @param height The new game window height.
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        this.setupCache();

        super.resize(minecraft, width, height);

        this.restoreCache();

        if (this.configTab == ConfigTab.SEARCH)
            this.getWidgets().focusInput = true;

        Overlay.resize();
    }

    /**
     * Ticks the search input widget.
     */
    @Override
    public void tick() { this.getWidgets().getSearchInput().tick(); }

    /**
     * Handler method for when this screen is closed.
     */
    @Override
    public void onClose()
    {
        // Save tweak cache and config file
        this.save();
        AutoConfig.getConfigHolder(ClientConfig.class).save();

        // Clear expansion states stored in config rows
        ContainerButton.collapseAll();

        // Return to parent screen
        this.minecraft.setScreen(this.parentScreen);
    }

    /**
     * Public implementation of the screen's protected renderable widget addition method.
     * @param widget The widget to add to this screen.
     * @return The widget instance.
     * @param <T> The type of widget.
     */
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget)
    {
        return super.addRenderableWidget(widget);
    }

    /**
     * Resets the configuration row list to a fresh empty state.
     * Any previous tabbing selection will be discarded.
     */
    public void resetRowList()
    {
        this.getWidgets().getConfigRowList().children().clear();
        this.getWidgets().getConfigRowList().resetScrollbar();
        this.getWidgets().getConfigRowList().resetLastSelection();
    }

    /**
     * Changes the screen's configuration tab. This method must be used when the configuration tab needs to be changed.
     * State management and row list management is also handled by this method when the configuration tab is changed.
     * @param configTab The new configuration tab.
     */
    public void setConfigTab(ConfigTab configTab)
    {
        if (this.configTab == configTab)
            return;

        if (this.configTab == ConfigTab.ALL || configTab == ConfigTab.ALL)
            ContainerButton.collapseAll();

        if (configTab == ConfigTab.SEARCH)
            this.getWidgets().getConfigRowList().setRowHeight(36);
        else
            this.getWidgets().getConfigRowList().resetRowHeight();

        this.configTab = configTab;

        this.resetRowList();

        if (configTab == ConfigTab.ALL)
        {
            if (!Overlay.isOpened())
            {
                this.getRenderer().generateRowsFromAllGroups();
                new CategoryListOverlay();
            }
        }
        else if (configTab == ConfigTab.SEARCH)
        {
            this.getWidgets().getSearchInput().setVisible(true);
            this.getWidgets().runSearch(this.getWidgets().getSearchInput().getValue());
        }
    }

    /**
     * Handler method for when a character is typed.
     * @param codePoint The character code.
     * @param modifiers Modifiers.
     * @return Whether the character that was typed was handled by this method.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (this.configTab == ConfigTab.SEARCH && this.getWidgets().getSearchInput().isFocused())
        {
            boolean isCharTyped = this.getWidgets().getSearchInput().charTyped(codePoint, modifiers);

            if (isCharTyped && !KeyUtil.isModifierDown())
                this.getWidgets().getConfigRowList().children().clear();

            return isCharTyped;
        }

        ConfigRowList.Row focused = this.getWidgets().getConfigRowList().getFocused();

        if (focused != null)
        {
            for (AbstractWidget widget : focused.children)
            {
                if (widget instanceof EditBox)
                    widget.charTyped(codePoint, modifiers);
            }
        }

        return false;
    }

    /**
     * Handler method for when the mouse scrolls.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param delta Scroll amount.
     * @return Whether this method handled the mouse scroll.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (Overlay.isOpened())
            return Overlay.mouseScrolled(mouseX, mouseY, delta);

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * Handler method for when the mouse is clicked.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse being clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (Overlay.isOpened())
            return Overlay.mouseClicked(mouseX, mouseY, button);

        if (this.getWidgets().getSearchInput().isFocused())
            this.getWidgets().getSearchInput().mouseClicked(mouseX, mouseY, button);

        if (this.configTab == ConfigTab.SEARCH)
        {
            if (ConfigRowList.overTweakId != null && ConfigWidgets.isInsideRowList(mouseY))
            {
                ContainerButton.collapseAll();
                ConfigRowList.jumpToTweakId = ConfigRowList.overTweakId;
            }

            for (ConfigRowList.Row row : this.getWidgets().getConfigRowList().children())
            {
                for (AbstractWidget widget : row.children)
                {
                    if (widget instanceof SearchCrumbs crumb)
                    {
                        boolean isClicked = crumb.mouseClicked(mouseX, mouseY, button);

                        if (isClicked)
                        {
                            ContainerButton.collapseAll();
                            return true;
                        }
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse moves while a mouse button is held down.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX The mouse drag x-position.
     * @param dragY The mouse drag y-position.
     * @return Whether this method handled the mouse drag event.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (Overlay.isOpened())
            return Overlay.mouseDragged(mouseX, mouseY, button, dragX, dragY);

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * Handler method for when the mouse button is released after a dragging.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was released.
     * @return Whether this method handled the mouse being released.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (Overlay.isOpened())
            Overlay.onRelease(mouseX, mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /* Key Utility & Handling */

    /**
     * @return Get the focused edit box within the configuration row list.
     */
    private EditBox getEditBox()
    {
        ConfigRowList.Row focused = this.getWidgets().getConfigRowList().getFocused();

        if (focused != null)
        {
            for (AbstractWidget widget : focused.children)
                if (widget instanceof EditBox && ((EditBox) widget).canConsumeInput())
                    return (EditBox) widget;
        }

        return null;
    }

    /**
     * @return Get the focused key mapping button within the configuration row list.
     */
    private KeyBindButton getMappingInput()
    {
        ConfigRowList.Row focused = this.getWidgets().getConfigRowList().getFocused();

        if (focused != null)
        {
            for (AbstractWidget widget : focused.children)
                if (widget instanceof KeyBindButton && ((KeyBindButton) widget).isModifying())
                    return (KeyBindButton) widget;
        }

        return null;
    }

    /**
     * Handler method for when a key is pressed.
     * @param keyCode The key code that was pressed.
     * @param scanCode A key scan code.
     * @param modifiers Key code modifiers.
     * @return Whether this method handled the key that was pressed.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Debugging
        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_D)
            this.parentScreen.keyPressed(keyCode, scanCode, modifiers);

        // Overlays
        if (Overlay.isOpened())
            return Overlay.keyPressed(keyCode, scanCode, modifiers);

        // Config Row List
        if (this.getWidgets().getConfigRowList().keyPressed(keyCode, scanCode, modifiers))
            return true;

        /* Config Screen */

        KeyBindButton mappingInput = this.getMappingInput();
        EditBox editBox = this.getEditBox();

        if (KeyUtil.isEsc(keyCode) && this.shouldCloseOnEsc() && mappingInput == null)
        {
            if (this.getWidgets().getSearchInput().isFocused())
                this.getWidgets().getSearchInput().setFocused(false);
            else if (editBox != null && editBox.isFocused())
                editBox.setFocused(false);
            else
                this.onCancel();

            return true;
        }

        if (editBox != null)
            return editBox.keyPressed(keyCode, scanCode, modifiers);

        if (mappingInput != null)
        {
            mappingInput.setKey(keyCode, scanCode);
            return true;
        }
        else if (KeyUtil.isSaving(keyCode))
        {
            this.onClose();
            return true;
        }
        else if ((KeyUtil.isGoingLeft(keyCode) || KeyUtil.isGoingRight(keyCode)) && !this.getWidgets().getSearchInput().isFocused())
        {
            ConfigTab[] tabs = ConfigTab.values();
            ConfigTab last = ConfigTab.GENERAL;

            for (int i = 0; i < tabs.length; i ++)
            {
                ConfigTab tab = tabs[i];

                if (tab == ConfigTab.ALL && this.configTab != ConfigTab.ALL)
                    continue;
                else if (this.configTab == ConfigTab.ALL)
                    last = ConfigTab.ALL;

                if (KeyUtil.isGoingLeft(keyCode) && this.configTab == tab)
                {
                    this.setConfigTab(this.configTab != last ? last : tabs[tabs.length - 2]);
                    break;
                }
                else if (KeyUtil.isGoingRight(keyCode) && this.configTab == tab)
                {
                    this.setConfigTab(i + 1 < tabs.length - 1 ? tabs[i + 1] : tabs[1]);
                    break;
                }

                last = tab;
            }
        }

        if (this.configTab == ConfigTab.SEARCH && this.getWidgets().getSearchInput().isFocused() && !KeyUtil.isEsc(keyCode))
        {
            boolean isInputChanged = this.getWidgets().getSearchInput().keyPressed(keyCode, scanCode, modifiers);

            if (!KeyUtil.isLeftOrRight(keyCode))
            {
                if (KeyUtil.isSearching(keyCode))
                    this.getWidgets().getSearchInput().setValue("");
                else if (KeyUtil.isModifierDown() || isInputChanged)
                {
                    if (isInputChanged)
                        this.getWidgets().runSearch(this.getWidgets().getSearchInput().getValue());

                    return true;
                }
            }

            return isInputChanged;
        }

        if (KeyUtil.isSearching(keyCode))
        {
            this.setConfigTab(ConfigTab.SEARCH);
            this.getWidgets().focusInput = true;

            return true;
        }
        else if (KeyUtil.isSelectAll(keyCode))
        {
            this.setConfigTab(ConfigTab.ALL);

            if (!Overlay.isOpened())
                new CategoryListOverlay();

            return true;
        }
        else
        {
            if (!KeyUtil.isTab(keyCode) && super.keyPressed(keyCode, scanCode, modifiers))
                return true;

            return keyCode == 257 || keyCode == 335;
        }
    }

    /* Search Tab Jumping */

    /**
     * Get a configuration tab based on a provided language key for a group.
     * @param groupLangKey A language file key.
     * @return A configuration tab related to the provide language key.
     */
    private ConfigTab getTabFromGroupKey(String groupLangKey)
    {
        for (TweakGroup group : TweakGroup.values())
        {
            if (group.getLangKey().equals(groupLangKey))
            {
                return switch (group)
                {
                    case CANDY -> ConfigTab.CANDY;
                    case SOUND -> ConfigTab.SOUND;
                    case SWING -> ConfigTab.SWING;
                    case GAMEPLAY -> ConfigTab.GAMEPLAY;
                    case ANIMATION -> ConfigTab.ANIMATION;
                    default -> ConfigTab.GENERAL;
                };
            }
        }

        return ConfigTab.GENERAL;
    }

    /**
     * Sets this screen's configuration tab based on the provided language key associated with a tweak group.
     * @param groupLangKey A language file key.
     */
    public void setTabFromGroupKey(String groupLangKey) { this.setConfigTab(this.getTabFromGroupKey(groupLangKey)); }

    /**
     * Sets the scrollbar on a configuration container row based on the provided container identifier.
     * @param containerId An enumeration value from {@link TweakGui} (category, subcategory, or embedded).
     */
    public void setScrollOnContainer(Object containerId) { ConfigRowList.jumpToContainerId = containerId; }

    /**
     * Jump to a configuration row entry that holds controllers for the provided tweak.
     * @param tweak The tweak to jump to.
     */
    private void jumpToTweak(TweakClientCache<?> tweak)
    {
        ConfigRowList list = this.getWidgets().getConfigRowList();

        for (ConfigRowList.Row row : list.children())
        {
            if (row.tweak == tweak)
            {
                list.setFocusOn(row.controller);
                list.setScrollOn(row);
            }
        }
    }

    /**
     * Jump to a container that holds the provided tweak.
     * @param tweak A tweak with container data.
     */
    private void jumpToContainerFromTweak(TweakClientCache<?> tweak)
    {
        if (tweak.getEmbed() != null)
        {
            this.jumpToContainer(tweak.getEmbed().container().getSubcategory().getCategory());
            this.jumpToContainer(tweak.getEmbed().container().getSubcategory());
            this.jumpToContainer(tweak.getEmbed().container());
        }
        else if (tweak.getSubcategory() != null)
        {
            this.jumpToContainer(tweak.getSubcategory().container().getCategory());
            this.jumpToContainer(tweak.getSubcategory().container());
        }
        else if (tweak.getCategory() != null)
            this.jumpToContainer(tweak.getCategory().container());
    }

    /**
     * Jump to a container based on the provided container identifier.
     * @param tweakContainer An enumeration value from {@link TweakGui} (category, subcategory, or embedded).
     */
    private void jumpToContainer(Object tweakContainer)
    {
        ConfigRowList list = this.getWidgets().getConfigRowList();

        for (ConfigRowList.Row row : list.children())
        {
            for (AbstractWidget widget : row.children)
            {
                if (widget instanceof ContainerButton container)
                {
                    if (container.getId() == tweakContainer)
                    {
                        if (!container.isExpanded())
                            container.silentPress();

                        list.setFocusOn(container);
                        list.setScrollOn(row);
                    }
                }
            }
        }
    }

    /* On-click Handlers */

    /**
     * Confirms that the user wants to exit the current configuration screen instance.
     */
    protected void onCancel()
    {
        if (!this.isSavable())
        {
            this.onClose(true);
            return;
        }

        this.setupCache();

        this.minecraft.setScreen
        (
            new ConfirmScreen
            (
                new CancelConsumer(),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_TITLE),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_BODY),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_DISCARD),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_CANCEL)
            )
        );
    }

    /* Closing Consumers */

    /**
     * Helper class that redirects the screen based on user input.
     */
    private class CancelConsumer implements BooleanConsumer
    {
        @Override
        public void accept(boolean understood)
        {
            if (understood)
            {
                ConfigScreen.this.onClose(true);
                ConfigScreen.this.minecraft.setScreen(ConfigScreen.this.parentScreen);
            }
            else
            {
                ConfigScreen.this.minecraft.setScreen(ConfigScreen.this);
                ConfigScreen.this.restoreCache();
            }
        }
    }

    /**
     * Handler method for when the screen is about to exit.
     * @param isCancelled Whether to cancel any configuration changes.
     */
    protected void onClose(boolean isCancelled)
    {
        if (isCancelled)
        {
            for (TweakClientCache<?> cache : TweakClientCache.all().values())
            {
                if (cache.isSavable())
                    cache.undo();
            }
        }

        this.onClose();
    }

    /* Saving */

    /**
     * Checks if the save button should be active.
     * @return Whether there is a tweak with a saved value that does not match the current client cache.
     */
    public boolean isSavable()
    {
        boolean isCacheDifferent = false;

        for (TweakClientCache<?> cache : TweakClientCache.all().values())
        {
            if (isCacheDifferent)
                break;

            if (cache.isSavable())
                isCacheDifferent = true;
        }

        return isCacheDifferent;
    }

    /**
     * Saves the client cache and runs any applicable runnables that need to run after the save button is pressed by the
     * user.
     */
    private void save()
    {
        for (TweakClientCache<?> cache : TweakClientCache.all().values())
        {
            if (cache.isSavable())
                cache.save();
        }

        RunUtil.onSave.forEach(Runnable::run);
        NostalgicTweaks.LOGGER.debug("Ran (%s) onSave functions", RunUtil.onSave.size());
    }

    /* Rendering */

    /**
     * Handler method for rendering the configuration screen and its subscribed widgets.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The mouse x-position.
     * @param mouseY The mouse y-position.
     * @param partialTick The change in time between frames.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        ConfigRowList list = this.getWidgets().getConfigRowList();

        String tabKey = this.configTab.getLangKey();
        String configKey = LangUtil.Config.CONFIG_TITLE;
        String title = Component.translatable(tabKey).getString() + " " + Component.translatable(configKey).getString();

        // Config Row Generation for Group Tabs

        if (list.children().isEmpty())
            this.getRenderer().generateAndRender(graphics, mouseX, mouseY, partialTick);

        // Reset scrollbar position without flashing

        if (this.scrollAmountCache > 0.0D)
        {
            list.render(graphics, mouseX, mouseY, partialTick);
            this.getWidgets().getConfigRowList().setScrollAmount(this.scrollAmountCache);
            this.scrollAmountCache = 0.0D;
        }

        // Background Rendering

        if (this.minecraft.level != null)
            graphics.fillGradient(0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(graphics);

        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        graphics.fillGradient(0, 0, this.width, this.height, 1744830464, 1744830464);

        // Render config row list

        list.render(graphics, mouseX, mouseY, partialTick);

        // Widget Overlay Overrides

        for (Button button : this.getWidgets().getCategories())
            button.active = !Overlay.isOpened();

        // Configuration Tab Activation

        switch (this.configTab)
        {
            case ALL -> this.getWidgets().getList().active = !Overlay.isOpened();
            case GENERAL -> this.getWidgets().getGeneral().active = false;
            case SOUND -> this.getWidgets().getSound().active = false;
            case CANDY -> this.getWidgets().getCandy().active = false;
            case GAMEPLAY -> this.getWidgets().getGameplay().active = false;
            case ANIMATION -> this.getWidgets().getAnimation().active = false;
            case SWING -> this.getWidgets().getSwing().active = false;
            case SEARCH -> this.getWidgets().getSearch().active = false;
        }

        this.getWidgets().getSave().active = this.isSavable() && !Overlay.isOpened();
        this.getWidgets().getCancel().active = !Overlay.isOpened();

        // Widget Rendering

        for (Renderable widget : this.getWidgets().children)
        {
            if (!(widget instanceof ConfigRowList))
                widget.render(graphics, mouseX, mouseY, partialTick);
        }

        if (this.configTab != ConfigTab.SEARCH)
        {
            this.getWidgets().getSearchInput().setVisible(false);
            graphics.drawCenteredString(this.font, title, this.width / 2, 8, 0xFFFFFF);
        }
        else if (this.getWidgets().focusInput)
        {
            this.getWidgets().focusSearch();
            this.getWidgets().focusInput = false;
        }

        this.getWidgets().getClear().active = this.getWidgets().getSearchInput().getValue().length() > 0;
        this.getWidgets().getSearchControls().forEach((button) -> button.visible = this.configTab == ConfigTab.SEARCH);
        this.getWidgets().getSearchInput().render(graphics, mouseX, mouseY, partialTick);

        // Render Highlighted Overlap

        for (Renderable widget : this.getWidgets().children)
        {
            if (widget instanceof OverlapButton button && button.isMouseOver(mouseX, mouseY))
                button.render(graphics, mouseX, mouseY, partialTick);
        }

        // Magnifying Glass Icon

        graphics.blit(TextureLocation.WIDGETS, this.getWidgets().getSearch().getX() + 5, this.getWidgets().getSearch().getY() + 4, 0, 15, 12, 12);

        // Finish Screen Rendering

        if (!Overlay.isOpened())
            this.renderLast.forEach(Runnable::run);

        this.renderLast.clear();

        // Overlay Rendering

        Overlay.render(graphics, mouseX, mouseY, partialTick);

        // Crumb & Tweak Jumping

        if (ConfigRowList.jumpToContainerId != null)
        {
            if (ConfigRowList.jumpToContainerId instanceof TweakEmbed embed)
            {
                this.jumpToContainer(embed.getSubcategory().getCategory());
                this.jumpToContainer(embed.getSubcategory());
                this.jumpToContainer(embed);
            }
            else if (ConfigRowList.jumpToContainerId instanceof TweakSubcategory subcategory)
            {
                this.jumpToContainer(subcategory.getCategory());
                this.jumpToContainer(subcategory);
            }
            else if (ConfigRowList.jumpToContainerId instanceof TweakCategory category)
                this.jumpToContainer(category);

            ConfigRowList.jumpToContainerId = null;
        }


        if (ConfigRowList.jumpToTweakId != null && this.configTab == ConfigTab.SEARCH)
        {
            TweakClientCache<?> tweak = TweakClientCache.all().get(ConfigRowList.jumpToTweakId);

            this.getWidgets().getList().playDownSound(Minecraft.getInstance().getSoundManager());
            this.setTabFromGroupKey(tweak.getGroup().getLangKey());
        }
        else if (ConfigRowList.jumpToTweakId != null)
        {
            TweakClientCache<?> tweak = TweakClientCache.all().get(ConfigRowList.jumpToTweakId);

            this.jumpToContainerFromTweak(tweak);
            this.jumpToTweak(tweak);

            ConfigRowList.jumpToTweakId = null;
            ConfigRowList.overTweakId = null;
        }

        // Debugging

        if (NostalgicTweaks.isDebugging())
            graphics.drawString(this.font, "Debug: §2ON", 2, this.height - 10, 0xFFFF00);
    }
}
