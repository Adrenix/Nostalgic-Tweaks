package mod.adrenix.nostalgic.client.gui.screen.config;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.GroupRow;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.tooltip.Tooltip;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.timer.FlagTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ConfigScreen extends EnhancedScreen<ConfigScreen, ConfigWidgets>
{
    /* Static */

    static final ScreenCache SCREEN_CACHE = new ScreenCache();

    /* Fields */

    private ConfigWidgets configWidgets;
    private Container category;
    private final FlagTimer timer;
    private boolean visible = true;
    private boolean initialized = false;

    /* Constructor */

    public ConfigScreen(@Nullable Screen parentScreen)
    {
        super(ConfigWidgets::new, parentScreen, Lang.Home.TITLE.get());

        this.category = Category.MOD;
        this.timer = FlagTimer.create(1L, TimeUnit.SECONDS).build();

        if (this.minecraft.level != null)
            TweakPool.setAllFail();

        TweakPool.setAllCacheModes();
    }

    /* Methods */

    @Override
    protected void init()
    {
        if (this.initialized && !SCREEN_CACHE.isPushed())
            SCREEN_CACHE.push(this);

        super.init();

        this.initialized = true;

        if (ModTweak.PERSISTENT_CONFIG_SCREEN.get())
            SCREEN_CACHE.pop(this);
        else
            SCREEN_CACHE.reset();

        if (SCREEN_CACHE.rowProvider.equals(RowProvider.DEFAULT))
        {
            this.configWidgets.getTabs()
                .stream()
                .filter(tab -> tab.getCategory().equals(SCREEN_CACHE.category))
                .findFirst()
                .ifPresent(this::setFocused);
        }
    }

    @Override
    protected ConfigScreen self()
    {
        return this;
    }

    @Override
    public ConfigWidgets getWidgetManager()
    {
        return this.configWidgets;
    }

    @Override
    public void setWidgetManager(ConfigWidgets configWidgets)
    {
        this.configWidgets = configWidgets;
    }

    /**
     * @return Get the currently selected tweak category.
     */
    public Container getCategory()
    {
        return this.category;
    }

    /**
     * Change the tweak category that is currently focused.
     *
     * @param category The new tweak category.
     */
    public void setCategory(Container category)
    {
        if (this.category == category && RowProvider.DEFAULT.isProviding())
            return;

        this.category = category;

        this.configWidgets.getRowList().resetScrollAmount();
        this.configWidgets.populateRowList();
    }

    /**
     * Jump the row list to a row that contains the given container.
     *
     * @param container The container to jump to.
     */
    public void jumpTo(Container container)
    {
        if (RowProvider.DEFAULT.isProviding())
        {
            if (container.isCategory())
            {
                if (this.category == container)
                    this.configWidgets.getRowList().setSmoothScrollAmount(0.0D);

                return;
            }
        }
        else
        {
            this.setCategory(container.getCategory());
            RowProvider.DEFAULT.use();
        }

        Optional<GroupRow> groupRow = CollectionUtil.fromCast(this.configWidgets.getRowList().getRows(), GroupRow.class)
            .filter(row -> row.getContainer().equals(container))
            .findFirst();

        if (groupRow.isPresent())
        {
            groupRow.get().jumpToMe();
            return;
        }

        container.getGroupSetFromCategory().forEach(this::jumpFromGroup);
    }

    /**
     * Find and jump to a group container within the row list.
     *
     * @param container The container to match a group row with.
     */
    private void jumpFromGroup(Container container)
    {
        CollectionUtil.fromCast(this.configWidgets.getRowList().getRows(), GroupRow.class)
            .filter(row -> row.getContainer().equals(container))
            .findFirst()
            .ifPresent(GroupRow::jumpToMe);
    }

    /**
     * The timer is set to a one-second delay, and the state within the timer determines when widgets should change
     * visual states to draw attention.
     *
     * @return Whether a widget should change visual states.
     */
    public boolean getTimerState()
    {
        return this.timer.getFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        SCREEN_CACHE.push(this);
        super.resize(minecraft, width, height);
        SCREEN_CACHE.pop(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (Screen.hasControlDown() && keyCode == InputConstants.KEY_Q)
            this.configWidgets.getFavorite().onPress();

        if (Screen.hasControlDown() && keyCode == InputConstants.KEY_A)
            this.configWidgets.getAll().onPress();

        if (Screen.hasControlDown() && keyCode == InputConstants.KEY_S)
            this.saveConfig();

        if (Screen.hasControlDown() && keyCode == InputConstants.KEY_L)
            this.visible = !this.visible;

        if (KeyboardUtil.isGoingLeft(keyCode))
            this.configWidgets.selectTabLeft();

        if (KeyboardUtil.isGoingRight(keyCode))
            this.configWidgets.selectTabRight();

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (!this.visible)
            return false;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        if (!this.visible)
            return false;

        int width = this.width;
        int height = this.configWidgets.getTabLeft().getEndY();

        if (MathUtil.isWithinBox(mouseX, mouseY, 0, 0, width, height))
        {
            if (deltaY > 0)
                this.configWidgets.moveTabsRight();
            else if (deltaY < 0)
                this.configWidgets.moveTabsLeft();
        }

        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft.level != null && this.visible)
            graphics.fillGradient(0, 0, this.width, this.height, 0x32101010, 0x01000000);
        else if (this.minecraft.level == null)
            Panorama.render(graphics, partialTick);

        Tooltip.setVisible(this.visible);

        if (this.visible)
            super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * @return Whether there is a savable tweak that needs saved to disk.
     */
    public boolean isSavable()
    {
        return TweakPool.stream().filter(Tweak::isNotIgnored).anyMatch(Tweak::isAnyCacheSavable);
    }

    /**
     * Handler method that saves config changes.
     */
    protected void saveConfig()
    {
        if (!this.isSavable())
            return;

        TweakPool.stream()
            .filter(Tweak::isNotIgnored)
            .filter(Tweak::isAnyCacheSavable)
            .forEach(Tweak::applyCacheAndSend);

        ConfigCache.save();
        AfterConfigSave.run();
    }

    /**
     * Handler method that undoes config changes.
     */
    protected void undoAndClose()
    {
        TweakPool.values().forEach(Tweak::sync);
        this.onClose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose()
    {
        SCREEN_CACHE.push(this);
        super.onClose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinish()
    {
        if (!this.isSavable())
        {
            this.undoAndClose();
            return;
        }

        class CancelConsumer implements BooleanConsumer
        {
            @Override
            public void accept(boolean understood)
            {
                if (understood)
                {
                    ConfigScreen.this.undoAndClose();
                    ConfigScreen.this.minecraft.setScreen(ConfigScreen.this.parentScreen);
                }
                else
                {
                    ConfigScreen.this.minecraft.setScreen(ConfigScreen.this);
                    SCREEN_CACHE.pop(ConfigScreen.this);
                }
            }
        }

        SCREEN_CACHE.push(this);

        Component title = Lang.Affirm.QUIT_TITLE.get();
        Component body = Lang.Affirm.QUIT_BODY.get();
        Component discard = Lang.Affirm.QUIT_DISCARD.get();
        Component cancel = Lang.Affirm.QUIT_CANCEL.get();
        ConfirmScreen confirmScreen = new ConfirmScreen(new CancelConsumer(), title, body, discard, cancel);

        this.minecraft.setScreen(confirmScreen);
    }
}
