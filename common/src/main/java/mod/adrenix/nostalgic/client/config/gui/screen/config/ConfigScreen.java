package mod.adrenix.nostalgic.client.config.gui.screen.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryList;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.SearchCrumbs;
import mod.adrenix.nostalgic.client.config.gui.widget.button.GroupButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.KeyBindButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import org.lwjgl.glfw.GLFW;

import java.util.*;

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
        SOUND(LangUtil.Cloth.SOUND_TITLE),
        CANDY(LangUtil.Cloth.CANDY_TITLE),
        GAMEPLAY(LangUtil.Cloth.GAMEPLAY_TITLE),
        ANIMATION(LangUtil.Cloth.ANIMATION_TITLE),
        SWING(LangUtil.Cloth.SWING_TITLE),
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
    private static boolean isCacheReflected = false;

    /* Getters */

    public Font getFont() { return this.font; }
    public Minecraft getMinecraft() { return this.minecraft; }
    public ConfigWidgets getWidgets() { return this.widgetProvider; }
    public ConfigRenderer getRenderer() { return this.rendererProvider; }
    public ConfigTab getConfigTab() { return this.configTab; }

    /* Constructor */

    public ConfigScreen(Screen parentScreen)
    {
        super(Component.translatable(LangUtil.Cloth.CONFIG_TITLE));

        this.minecraft = Minecraft.getInstance();
        this.parentScreen = parentScreen;

        if (Minecraft.getInstance().level != null && !ConfigScreen.isCacheReflected)
        {
            ConfigScreen.isCacheReflected = true;

            TweakClientCache.all().forEach((key, tweak) -> {
                TweakSide.EntryStatus entryStatus = tweak.getMetadata(TweakSide.EntryStatus.class);

                if (entryStatus != null && tweak.getStatus() == StatusType.WAIT)
                    tweak.setStatus(StatusType.FAIL);
            });

            TweakServerCache.all().forEach((key, tweak) -> {
                TweakSide.EntryStatus entryStatus = tweak.getMetadata(TweakSide.EntryStatus.class);

                if (entryStatus != null && tweak.getStatus() == StatusType.WAIT)
                    tweak.setStatus(StatusType.FAIL);
            });
        }
    }

    /* Screen Methods */

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
        String searching = this.getWidgets().getSearchInput().getValue();
        int rowHeight = this.getWidgets().getConfigRowList().getRowHeight();

        super.resize(minecraft, width, height);

        this.getWidgets().getSearchInput().setValue(searching);
        this.getWidgets().getConfigRowList().setRowHeight(rowHeight);

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
        GroupButton.collapseAll();

        // Return to parent screen
        this.minecraft.setScreen(this.parentScreen);
    }

    /**
     * Public implementation of the screen's protected renderable widget addition method.
     * @param widget The widget to add to this screen.
     * @return The widget instance.
     * @param <T> The type of widget.
     */
    public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget)
    {
        return super.addRenderableWidget(widget);
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
        else if (this.configTab == ConfigTab.ALL || configTab == ConfigTab.ALL)
            GroupButton.collapseAll();

        if (configTab == ConfigTab.SEARCH)
            this.getWidgets().getConfigRowList().setRowHeight(36);
        else
            this.getWidgets().getConfigRowList().resetRowHeight();

        this.configTab = configTab;

        this.getWidgets().getConfigRowList().children().clear();
        this.getWidgets().getConfigRowList().setScrollAmount(0);
        this.getWidgets().getConfigRowList().resetLastSelection();

        if (configTab == ConfigTab.ALL)
        {
            if (!Overlay.isOpened())
            {
                this.getRenderer().generateRowsFromAllGroups();
                CategoryList.OVERLAY.open(this.getWidgets().getConfigRowList());
            }
        }
        else if (configTab == ConfigTab.SEARCH)
            this.getWidgets().runSearch(this.getWidgets().getSearchInput().getValue());
    }

    /**
     * Handler method for when a character is typed.
     * @param code The character code.
     * @param modifiers Modifiers.
     * @return Whether the character that was typed was handled by this method.
     */
    @Override
    public boolean charTyped(char code, int modifiers)
    {
        if (this.configTab == ConfigTab.SEARCH && this.getWidgets().getSearchInput().isFocused())
        {
            boolean isCharTyped = this.getWidgets().getSearchInput().charTyped(code, modifiers);

            if (isCharTyped && !KeyUtil.isModifierDown())
                this.getWidgets().getConfigRowList().children().clear();

            return isCharTyped;
        }

        ConfigRowList.Row focused = this.getWidgets().getConfigRowList().getFocused();

        if (focused != null)
        {
            for (AbstractWidget widget : focused.children)
                if (widget instanceof EditBox)
                    widget.charTyped(code, modifiers);
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
            if (ConfigRowList.overTweakId != null)
            {
                GroupButton.collapseAll();
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
                            GroupButton.collapseAll();
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
                this.getWidgets().getSearchInput().setFocus(false);
            else if (editBox != null && editBox.isFocused())
                editBox.setFocus(false);
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

            if (keyCode != GLFW.GLFW_KEY_LEFT && keyCode != GLFW.GLFW_KEY_RIGHT)
            {
                if (KeyUtil.isSearching(keyCode))
                {
                    this.getWidgets().getSearchInput().setValue("");
                    this.getWidgets().getConfigRowList().setScrollAmount(0);
                }
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
                CategoryList.OVERLAY.open(this.getWidgets().getConfigRowList());

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
        for (GroupType group : GroupType.values())
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
     * Sets the scrollbar on a configuration container row based on the provided group identifier.
     * @param id An enumeration value from {@link TweakClient} (category, subcategory, or embedded).
     */
    public void setScrollOnGroup(Object id) { ConfigRowList.jumpToGroupId = id; }

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
     * Jump to a group that holds the provided tweak.
     * @param tweak A tweak with group data.
     */
    private void jumpToGroupFromTweak(TweakClientCache<?> tweak)
    {
        if (tweak.getEmbedded() != null)
        {
            this.jumpToGroup(tweak.getEmbedded().group().getSubcategory().getCategory());
            this.jumpToGroup(tweak.getEmbedded().group().getSubcategory());
            this.jumpToGroup(tweak.getEmbedded().group());
        }
        else if (tweak.getSubcategory() != null)
        {
            this.jumpToGroup(tweak.getSubcategory().group().getCategory());
            this.jumpToGroup(tweak.getSubcategory().group());
        }
        else if (tweak.getCategory() != null)
            this.jumpToGroup(tweak.getCategory().group());
    }

    /**
     * Jump to a group based on the provided group identifier.
     * @param groupId An enumeration value from {@link TweakClient} (category, subcategory, or embedded).
     */
    private void jumpToGroup(Object groupId)
    {
        ConfigRowList list = this.getWidgets().getConfigRowList();

        for (ConfigRowList.Row row : list.children())
        {
            for (AbstractWidget widget : row.children)
            {
                if (widget instanceof GroupButton group)
                {
                    if (group.getId() == groupId)
                    {
                        if (!group.isExpanded())
                            group.silentPress();

                        list.setFocusOn(group);
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

        this.minecraft.setScreen(
            new ConfirmScreen(
                new CancelConsumer(),
                Component.translatable(LangUtil.Cloth.QUIT_CONFIG),
                Component.translatable(LangUtil.Cloth.QUIT_CONFIG_SURE),
                Component.translatable(LangUtil.Cloth.QUIT_DISCARD),
                Component.translatable(LangUtil.Vanilla.GUI_CANCEL)
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
                ConfigScreen.this.minecraft.setScreen(ConfigScreen.this);
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
        NostalgicTweaks.LOGGER.debug(String.format("Ran (%s) onSave functions", RunUtil.onSave.size()));
    }

    /* Rendering */

    /**
     * Handler method for rendering the configuration screen and its subscribed widgets.
     * @param poseStack The current pose stack.
     * @param mouseX The mouse x-position.
     * @param mouseY The mouse y-position.
     * @param partialTick The change in time between frames.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        ConfigRowList list = this.getWidgets().getConfigRowList();

        String tabKey = this.configTab.getLangKey();
        String configKey = LangUtil.Cloth.CONFIG_TITLE;
        String title = Component.translatable(tabKey).getString() + " " + Component.translatable(configKey).getString();

        // Background Rendering

        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(0);

        // Config Row Generation for Group Tabs

        if (list.children().isEmpty())
            this.getRenderer().generateAndRender(poseStack, mouseX, mouseY, partialTick);

        list.render(poseStack, mouseX, mouseY, partialTick);

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

        for (Widget widget : this.getWidgets().children)
        {
            if (!(widget instanceof ConfigRowList))
                widget.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (this.configTab != ConfigTab.SEARCH)
        {
            this.getWidgets().getSearchInput().setVisible(false);
            ConfigScreen.drawCenteredString(poseStack, this.font, title, this.width / 2, 8, 0xFFFFFF);
        }
        else if (this.getWidgets().focusInput)
        {
            this.getWidgets().focusSearch();
            this.getWidgets().focusInput = false;
        }

        this.getWidgets().getClear().active = this.getWidgets().getSearchInput().getValue().length() > 0;
        this.getWidgets().getSearchControls().forEach((button) -> button.visible = this.configTab == ConfigTab.SEARCH);
        this.getWidgets().getSearchInput().render(poseStack, mouseX, mouseY, partialTick);

        // Magnifying Glass Icon

        RenderSystem.setShaderTexture(0, ModUtil.Resource.WIDGETS_LOCATION);
        this.blit(poseStack, this.getWidgets().getSearch().x + 5, this.getWidgets().getSearch().y + 4, 0, 15, 12, 12);

        // Finish Screen Rendering

        if (!Overlay.isOpened())
            this.renderLast.forEach(Runnable::run);

        this.renderLast.clear();

        // Overlay Rendering

        Overlay.render(poseStack, mouseX, mouseY, partialTick);

        // Crumb & Tweak Jumping

        if (ConfigRowList.jumpToGroupId != null)
        {
            if (ConfigRowList.jumpToGroupId instanceof TweakClient.Embedded embed)
            {
                this.jumpToGroup(embed.getSubcategory().getCategory());
                this.jumpToGroup(embed.getSubcategory());
                this.jumpToGroup(embed);
            }
            else if (ConfigRowList.jumpToGroupId instanceof TweakClient.Subcategory subcategory)
            {
                this.jumpToGroup(subcategory.getCategory());
                this.jumpToGroup(subcategory);
            }
            else if (ConfigRowList.jumpToGroupId instanceof TweakClient.Category category)
                this.jumpToGroup(category);

            ConfigRowList.jumpToGroupId = null;
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

            this.jumpToGroupFromTweak(tweak);
            this.jumpToTweak(tweak);

            ConfigRowList.jumpToTweakId = null;
            ConfigRowList.overTweakId = null;
        }

        // Debugging

        if (NostalgicTweaks.isDebugging())
            drawString(poseStack, this.font, "Debug: §2ON", 2, this.height - 10, 0xFFFF00);
    }
}
