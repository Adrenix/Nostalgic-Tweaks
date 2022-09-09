package mod.adrenix.nostalgic.client.config.gui.screen.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryList;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.button.GroupButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.KeyBindButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
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

public class ConfigScreen extends Screen
{
    /* Configuration Tabs */

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

    /* Search Tags */

    public enum SearchTag
    {
        CLIENT, SERVER, CONFLICT, RESET, NEW, SAVE, ALL;

        @Override
        public String toString() { return super.toString().toLowerCase(); }
    }

    /* Instance Fields */

    final Map<String, TweakClientCache<?>> search = new TreeMap<>();
    public final ArrayList<Runnable> renderLast = new ArrayList<>();
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

    /* Setters */

    public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget)
    {
        return super.addRenderableWidget(widget);
    }

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
                this.getRenderer().generateAllList();
                CategoryList.OVERLAY.open(this.getWidgets().getConfigRowList());
            }
        }
        else if (configTab == ConfigTab.SEARCH)
            this.getWidgets().checkSearch(this.getWidgets().getSearchInput().getValue());
    }

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
                TweakSide.EntryStatus entryStatus = CommonReflect.getAnnotation(tweak, TweakSide.EntryStatus.class);

                if (entryStatus != null && tweak.getStatus() == StatusType.WAIT)
                    tweak.setStatus(StatusType.FAIL);
            });

            TweakServerCache.all().forEach((key, tweak) -> {
                TweakSide.EntryStatus entryStatus = CommonReflect.getAnnotation(tweak, TweakSide.EntryStatus.class);

                if (entryStatus != null && tweak.getStatus() == StatusType.WAIT)
                    tweak.setStatus(StatusType.FAIL);
            });
        }
    }

    /* Methods */

    @Override
    protected void init()
    {
        this.widgetProvider = new ConfigWidgets(this);
        this.rendererProvider = new ConfigRenderer(this);
        this.getWidgets().addWidgets();
    }

    @Override
    public void tick() { this.getWidgets().getSearchInput().tick(); }

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

    @Override
    public boolean charTyped(char code, int modifiers)
    {
        if (this.configTab == ConfigTab.SEARCH && this.getWidgets().getSearchInput().isFocused())
        {
            boolean isCharTyped = this.getWidgets().getSearchInput().charTyped(code, modifiers);

            if (isCharTyped && !isModifierDown())
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

    public static boolean isModifierDown() { return Screen.hasShiftDown() || Screen.hasControlDown() || Screen.hasAltDown(); }
    private static boolean isSearching(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_F; }
    private static boolean isAll(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_A; }
    private static boolean isSaving(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_S; }
    private static boolean isGoingLeft(int key) { return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_LEFT; }
    private static boolean isGoingRight(int key) { return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_RIGHT; }
    public static boolean isEnter(int key) { return key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER; }
    public static boolean isTab(int key) { return key == GLFW.GLFW_KEY_TAB; }
    public static boolean isEsc(int key) { return key == GLFW.GLFW_KEY_ESCAPE; }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Debugging
        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_D)
            this.parentScreen.keyPressed(keyCode, scanCode, modifiers);

        // Overlays
        if (Overlay.isOpened())
            return Overlay.keyPressed(keyCode, scanCode, modifiers);

        // Config list key support
        if (this.getWidgets().getConfigRowList().keyPressed(keyCode, scanCode, modifiers))
            return true;

        /* Config Screen */

        KeyBindButton mappingInput = this.getMappingInput();
        EditBox editBox = this.getEditBox();

        if (isEsc(keyCode) && this.shouldCloseOnEsc() && mappingInput == null)
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
        else if (isSaving(keyCode))
        {
            this.onClose();
            return true;
        }
        else if ((isGoingLeft(keyCode) || isGoingRight(keyCode)) && !this.getWidgets().getSearchInput().isFocused())
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

                if (isGoingLeft(keyCode) && this.configTab == tab)
                {
                    this.setConfigTab(this.configTab != last ? last : tabs[tabs.length - 2]);
                    break;
                }
                else if (isGoingRight(keyCode) && this.configTab == tab)
                {
                    this.setConfigTab(i + 1 < tabs.length - 1 ? tabs[i + 1] : tabs[1]);
                    break;
                }

                last = tab;
            }
        }

        if (this.configTab == ConfigTab.SEARCH && this.getWidgets().getSearchInput().isFocused() && !isEsc(keyCode))
        {
            boolean isInputChanged = this.getWidgets().getSearchInput().keyPressed(keyCode, scanCode, modifiers);
            if (keyCode != GLFW.GLFW_KEY_LEFT && keyCode != GLFW.GLFW_KEY_RIGHT)
            {
                if (isSearching(keyCode))
                {
                    this.getWidgets().getSearchInput().setValue("");
                    this.getWidgets().getConfigRowList().setScrollAmount(0);
                }
                else if (isModifierDown() || isInputChanged)
                {
                    if (isInputChanged)
                        this.getWidgets().checkSearch(this.getWidgets().getSearchInput().getValue());

                    return true;
                }
            }

            return isInputChanged;
        }

        if (isSearching(keyCode))
        {
            this.setConfigTab(ConfigTab.SEARCH);
            this.getWidgets().focusInput = true;
            return true;
        }
        else if (isAll(keyCode))
        {
            this.setConfigTab(ConfigTab.ALL);

            if (!Overlay.isOpened())
                CategoryList.OVERLAY.open(this.getWidgets().getConfigRowList());

            return true;
        }
        else
        {
            if (!isTab(keyCode) && super.keyPressed(keyCode, scanCode, modifiers))
                return true;
            return keyCode == 257 || keyCode == 335;
        }
    }

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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (Overlay.isOpened())
            return Overlay.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (Overlay.isOpened())
            return Overlay.mouseClicked(mouseX, mouseY, button);

        if (this.getWidgets().getSearchInput().isFocused())
            this.getWidgets().getSearchInput().mouseClicked(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (Overlay.isOpened())
            return Overlay.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (Overlay.isOpened())
            Overlay.onRelease(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /* On-click Handlers */

    void onCancel()
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

    void onClose(boolean isCancelled)
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

    public boolean isSavable()
    {
        boolean isCacheDifferent = false;

        for (TweakClientCache<?> cache : TweakClientCache.all().values())
        {
            if (isCacheDifferent) break;
            if (cache.isSavable())
                isCacheDifferent = true;
        }

        return isCacheDifferent;
    }

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

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        String title = Component.translatable(this.configTab.getLangKey()).getString() + " " + Component.translatable(LangUtil.Cloth.CONFIG_TITLE).getString();

        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(0);

        if (this.getWidgets().getConfigRowList().children().isEmpty())
            this.getRenderer().generateGroupList(poseStack, mouseX, mouseY, partialTick);

        this.getWidgets().getConfigRowList().render(poseStack, mouseX, mouseY, partialTick);

        for (Button button : this.getWidgets().getCategories())
            button.active = !Overlay.isOpened();

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
            this.getWidgets().setSearchFocus();
            this.getWidgets().focusInput = false;
        }

        this.getWidgets().getClear().active = this.getWidgets().getSearchInput().getValue().length() > 0;
        this.getWidgets().getSearchControls().forEach((button) -> button.visible = this.configTab == ConfigTab.SEARCH);
        this.getWidgets().getSearchInput().render(poseStack, mouseX, mouseY, partialTick);

        RenderSystem.setShaderTexture(0, ModUtil.Resource.WIDGETS_LOCATION);
        this.blit(poseStack, this.getWidgets().getSearch().x + 5, this.getWidgets().getSearch().y + 4, 0, 15, 12, 12);

        if (!Overlay.isOpened())
            this.renderLast.forEach(Runnable::run);
        this.renderLast.clear();

        // Overlay Rendering
        Overlay.render(poseStack, mouseX, mouseY, partialTick);

        // Debugging
        if (NostalgicTweaks.isDebugging())
            drawString(poseStack, this.font, "Debug: §2ON", 2, this.height - 10, 0xFFFF00);
    }
}
