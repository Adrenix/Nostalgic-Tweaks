package mod.adrenix.nostalgic.client.config.gui.screen.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.widget.*;
import mod.adrenix.nostalgic.client.config.gui.widget.button.KeyBindButton;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.client.MixinClientUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
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
        GENERAL(NostalgicLang.Vanilla.GENERAL),
        SOUND(NostalgicLang.Cloth.SOUND_TITLE),
        CANDY(NostalgicLang.Cloth.CANDY_TITLE),
        ANIMATION(NostalgicLang.Cloth.ANIMATION_TITLE),
        SWING(NostalgicLang.Cloth.SWING_TITLE),
        SEARCH(NostalgicLang.Vanilla.SEARCH);

        ConfigTab(String langKey)
        {
            this.langKey = langKey;
        }

        private final String langKey;

        String getLangKey()
        {
            return this.langKey;
        }
    }

    /* Search Tags */

    public enum SearchTag
    {
        NEW,
        CONFLICT,
        RESET,
        CLIENT,
        SERVER;

        @Override
        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }

    /* Instance Fields */

    final Set<TweakClientCache<?>> search = new HashSet<>();
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
        this.configTab = configTab;
        this.getWidgets().getConfigRowList().children().clear();
        this.getWidgets().getConfigRowList().setScrollAmount(0);
    }

    public boolean isScrollbarVisible() { return this.getWidgets().getConfigRowList().getMaxScroll() > 0; }
    public void resetScrollbar() { this.getWidgets().getConfigRowList().setScrollAmount(0); }

    /* Constructor */

    public ConfigScreen(Screen parentScreen)
    {
        super(Component.translatable(NostalgicLang.Cloth.CONFIG_TITLE));

        this.minecraft = Minecraft.getInstance();
        this.parentScreen = parentScreen;

        if (Minecraft.getInstance().level != null && !ConfigScreen.isCacheReflected)
        {
            ConfigScreen.isCacheReflected = true;
            TweakClientCache.all().forEach((key, tweak) -> {
                TweakSide.EntryStatus entryStatus = CommonReflect.getAnnotation(
                    tweak.getGroup(),
                    tweak.getKey(),
                    TweakSide.EntryStatus.class
                );

                if (entryStatus != null && tweak.getStatus() == StatusType.WAIT)
                    tweak.setStatus(StatusType.FAIL);
            });
        }
    }

    /* Overrides */

    @Override
    protected void init()
    {
        this.widgetProvider = new ConfigWidgets(this);
        this.rendererProvider = new ConfigRenderer(this);
        this.getWidgets().addWidgets();
    }

    @Override
    public void tick()
    {
        this.getWidgets().getSearchInput().tick();
    }

    @Override
    public void onClose()
    {
        this.save();
        AutoConfig.getConfigHolder(ClientConfig.class).save();
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

    private boolean isModifierDown() { return Screen.hasShiftDown() || Screen.hasControlDown() || Screen.hasAltDown(); }
    private boolean isEscaping(int key) { return key == GLFW.GLFW_KEY_ESCAPE; }
    private boolean isSearching(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_F; }
    private boolean isSaving(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_S; }
    private boolean isGoingLeft(int key) { return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_LEFT; }
    private boolean isGoingRight(int key) { return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_RIGHT; }
    private boolean isTabbing(int key) { return key == GLFW.GLFW_KEY_TAB; }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        KeyBindButton mappingInput = this.getMappingInput();
        EditBox editBox = this.getEditBox();

        if (isEscaping(keyCode) && this.shouldCloseOnEsc() && mappingInput == null)
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
        else if (this.isSaving(keyCode))
        {
            this.onClose();
            return true;
        }
        else if (this.configTab != ConfigTab.SEARCH && (this.isGoingLeft(keyCode) || this.isGoingRight(keyCode)))
        {
            ConfigTab[] tabs = ConfigTab.values();
            ConfigTab last = ConfigTab.GENERAL;

            for (int i = 0; i < tabs.length; i ++)
            {
                ConfigTab tab = tabs[i];

                if (tab == ConfigTab.SEARCH)
                    continue;
                if (this.isGoingLeft(keyCode) && this.configTab == tab)
                {
                    this.setConfigTab(this.configTab != last ? last : tabs[tabs.length - 2]);
                    break;
                }
                else if (this.isGoingRight(keyCode) && this.configTab == tab)
                {
                    this.setConfigTab(i + 1 < tabs.length - 1 ? tabs[i + 1] : tabs[0]);
                    break;
                }

                last = tab;
            }

            if (this.configTab == ConfigTab.SEARCH)
                this.getWidgets().focusInput = true;
        }

        if (this.configTab == ConfigTab.SEARCH && this.getWidgets().getSearchInput().isFocused() && !isEscaping(keyCode))
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
                    {
                        this.getWidgets().getConfigRowList().children().clear();
                        this.getWidgets().checkSearch(this.getWidgets().getSearchInput().getValue());
                    }

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
        else
        {
            if (!this.isTabbing(keyCode) && super.keyPressed(keyCode, scanCode, modifiers))
                return true;
            return keyCode == 257 || keyCode == 335;
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        String searching = this.getWidgets().getSearchInput().getValue();

        super.resize(minecraft, width, height);

        if (this.configTab == ConfigTab.SEARCH)
        {
            this.getWidgets().focusInput = true;
            this.getWidgets().getSearchInput().setValue(searching);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.getWidgets().getSearchInput().isFocused())
            this.getWidgets().getSearchInput().mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
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
                Component.translatable(NostalgicLang.Cloth.QUIT_CONFIG),
                Component.translatable(NostalgicLang.Cloth.QUIT_CONFIG_SURE),
                Component.translatable(NostalgicLang.Cloth.QUIT_DISCARD),
                Component.translatable(NostalgicLang.Vanilla.GUI_CANCEL)
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

        MixinClientUtil.Run.onSave.forEach(Runnable::run);
    }

    /* Rendering */

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        String title = Component.translatable(this.configTab.getLangKey()).getString() + " " + Component.translatable(NostalgicLang.Cloth.CONFIG_TITLE).getString();

        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(0);

        if (this.getWidgets().getConfigRowList().children().isEmpty())
            this.getRenderer().generateGroupList(poseStack, mouseX, mouseY, partialTick);

        this.getWidgets().getConfigRowList().render(poseStack, mouseX, mouseY, partialTick);

        for (Button button : this.getWidgets().getCategories())
            button.active = true;

        switch (this.configTab)
        {
            case GENERAL -> this.getWidgets().getGeneral().active = false;
            case SOUND -> this.getWidgets().getSound().active = false;
            case CANDY -> this.getWidgets().getCandy().active = false;
            case ANIMATION -> this.getWidgets().getAnimation().active = false;
            case SWING -> this.getWidgets().getSwing().active = false;
            case SEARCH -> this.getWidgets().getSearch().active = false;
        }

        this.getWidgets().getSave().active = this.isSavable();

        for (Widget widget : this.getWidgets().children)
        {
            if (!(widget instanceof ConfigRowList))
                widget.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (this.configTab != ConfigTab.SEARCH)
        {
            this.getWidgets().getSearchInput().setVisible(false);
            ConfigScreen.drawCenteredString(poseStack, this.font, title, this.width / 2, 7, 0xFFFFFF);
        }
        else if (this.getWidgets().focusInput)
        {
            this.getWidgets().getSearchInput().setVisible(true);
            this.getWidgets().getSearchInput().setFocus(true);
            this.getWidgets().getSearchInput().setEditable(true);
            this.getWidgets().focusInput = false;
        }

        this.getWidgets().getSearchInput().render(poseStack, mouseX, mouseY, partialTick);

        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        this.blit(poseStack, this.getWidgets().getSearch().x + 5, this.getWidgets().getSearch().y + 4, 0, 15, 12, 12);

        this.renderLast.forEach(Runnable::run);
        this.renderLast.clear();
    }
}
