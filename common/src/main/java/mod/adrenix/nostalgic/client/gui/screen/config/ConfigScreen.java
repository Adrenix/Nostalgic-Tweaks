package mod.adrenix.nostalgic.client.gui.screen.config;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.GroupRow;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
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
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ConfigScreen extends EnhancedScreen<ConfigScreen, ConfigWidgets>
{
    /* Fields */

    private ConfigWidgets configWidgets;
    private Container category;
    private final FlagTimer timer;
    private final Cache cache;

    /* Constructor */

    public ConfigScreen(@Nullable Screen parentScreen)
    {
        super(ConfigWidgets::new, parentScreen, Lang.Home.TITLE.get());

        this.category = Category.MOD;
        this.timer = FlagTimer.create(1L, TimeUnit.SECONDS).build();
        this.cache = new Cache();

        if (this.minecraft.level != null)
            TweakPool.setAllFail();

        TweakPool.setAllCacheModes();
    }

    /* Methods */

    @Override
    protected void init()
    {
        super.init();

        if (this.cache.rowProvider.equals(RowProvider.DEFAULT))
            this.configWidgets.getTabs().stream().findFirst().ifPresent(this::setFocused);
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
     * @return The current cache instance for this screen.
     */
    public Cache getCache()
    {
        return this.cache;
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
        this.cache.push();
        super.resize(minecraft, width, height);
        this.cache.pop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_Q)
            this.configWidgets.getFavorite().onPress();

        if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_A)
            this.configWidgets.getAll().onPress();

        if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_S)
            this.saveConfig();

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
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
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
        if (this.minecraft.level != null)
            graphics.fillGradient(0, 0, this.width, this.height, 0x32101010, 0x01000000);
        else
            Panorama.render(graphics, partialTick);

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

        RunUtil.onSave.forEach(Runnable::run);
        NostalgicTweaks.LOGGER.debug("Ran (%s) onSave functions", RunUtil.onSave.size());
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
                    ConfigScreen.this.cache.pop();
                }
            }
        }

        this.cache.push();

        Component title = Lang.Affirm.QUIT_TITLE.get();
        Component body = Lang.Affirm.QUIT_BODY.get();
        Component discard = Lang.Affirm.QUIT_DISCARD.get();
        Component cancel = Lang.Affirm.QUIT_CANCEL.get();
        ConfirmScreen confirmScreen = new ConfirmScreen(new CancelConsumer(), title, body, discard, cancel);

        this.minecraft.setScreen(confirmScreen);
    }

    /* Cache */

    public class Cache
    {
        private final HashSet<Container> containers = new HashSet<>();
        private RowProvider rowProvider = RowProvider.get();
        private boolean pushed = false;
        private double scrollAmount = 0.0D;
        private String search = "";

        private Cache()
        {
        }

        /**
         * @return Whether the cache has already been pushed into memory.
         */
        public boolean isPushed()
        {
            return this.pushed;
        }

        /**
         * @return The config widgets instance from the parent config screen.
         */
        private ConfigWidgets getWidgets()
        {
            return ConfigScreen.this.getWidgetManager();
        }

        /**
         * Caches important widget values so that those values can be restored at a later point in time.
         */
        public void push()
        {
            this.pushed = true;

            this.containers.clear();
            this.getWidgets().getRowList().getRows().forEach(this::saveOpenedGroups);

            this.search = this.getWidgets().getQuery();
            this.scrollAmount = this.getWidgets().getRowList().getScrollAmount();
            this.rowProvider = RowProvider.get();
        }

        /**
         * Restores important widget values cached from an earlier point in time.
         */
        public void pop()
        {
            this.pushed = false;
            this.rowProvider.useAndThen(() -> this.getWidgets().populateFromProvider());

            if (!this.search.isEmpty())
                this.getWidgets().setQuery(this.search);

            this.openSavedGroups();
            this.getWidgets().getRowList().setSmoothScrollAmount(this.scrollAmount);
        }

        /**
         * Checks if the given row is a group row, and if so, adds the group row's container to the opened containers
         * cache if the group row is expanded.
         *
         * @param row A row list row instance to check.
         */
        private void saveOpenedGroups(AbstractRow<?, ?> row)
        {
            ClassUtil.cast(row, GroupRow.class)
                .stream()
                .filter(GroupRow::isExpanded)
                .map(GroupRow::getContainer)
                .forEach(this.containers::add);
        }

        /**
         * Opens all previously expanded group rows that had their containers cached.
         */
        private void openSavedGroups()
        {
            if (this.containers.isEmpty())
                return;

            int openedSize = this.containers.size();
            HashSet<AbstractRow<?, ?>> rows = new HashSet<>(this.getWidgets().getRowList().getRows());

            CollectionUtil.fromCast(rows, GroupRow.class)
                .filter(group -> this.containers.contains(group.getContainer()))
                .forEachOrdered(group -> {
                    this.containers.remove(group.getContainer());
                    group.expand();
                });

            if (openedSize != this.containers.size())
                this.openSavedGroups();
        }
    }
}
