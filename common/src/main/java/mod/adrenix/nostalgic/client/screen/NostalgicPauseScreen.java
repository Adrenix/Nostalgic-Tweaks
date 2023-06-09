package mod.adrenix.nostalgic.client.screen;

import com.mojang.realmsclient.RealmsMainScreen;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.widen.ScreenAccessor;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

/**
 * This class overrides the vanilla pause screen.
 * Different layout styles are set up based on the user's current configuration.
 */

public class NostalgicPauseScreen extends PauseScreen
{
    /* Fields */

    private final TweakVersion.PauseLayout layout;
    private final Component mods = Component.translatable(LangUtil.Gui.CANDY_TITLE_MODS);
    private final Component lan = Component.translatable(LangUtil.Vanilla.MENU_LAN);
    private final Component stats = Component.translatable(LangUtil.Vanilla.GUI_STATS);
    private final Component options = Component.translatable(LangUtil.Vanilla.MENU_OPTIONS);
    private final Component disconnect = Component.translatable(LangUtil.Vanilla.MENU_DISCONNECT);
    private final Component toUpperBack = Component.translatable(LangUtil.Vanilla.MENU_RETURN_TO_GAME);
    private final Component toLowerBack = Component.translatable(LangUtil.Gui.PAUSE_RETURN_LOWER);
    private final Component achievements = Component.translatable(LangUtil.Gui.PAUSE_ACHIEVEMENTS);
    private boolean isFirstRender = false;

    /* Constructor */

    /**
     * Create a new nostalgic pause screen instance.
     */
    public NostalgicPauseScreen()
    {
        super(true);

        this.layout = ModConfig.Candy.getPauseLayout();
    }

    /* Utility */

    /**
     * Helper that provides a title language key based on pause layout.
     * @return A language file key.
     */
    private static String getPauseTitle()
    {
        return switch (ModConfig.Candy.getPauseLayout())
        {
            case ALPHA_BETA, ACHIEVE_LOWER, ACHIEVE_UPPER, LAN -> LangUtil.Gui.PAUSE_GAME;
            case ADVANCEMENT, MODERN -> LangUtil.Vanilla.MENU_GAME;
        };
    }

    /* Overrides */

    /**
     * Handler method that provides instructions for when the screen is initialized.
     */
    @Override
    protected void init()
    {
        ((ScreenAccessor) this).NT$setTitle(Component.translatable(getPauseTitle()));
        this.setLayout();
    }

    /**
     * Handler method that provides instructions for when the screen is ticked.
     */
    @Override
    public void tick() { super.tick(); }

    /**
     * Handler method that provides instructions for screen rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (!this.isFirstRender)
        {
            if (ModConfig.Candy.removeExtraPauseButtons())
            {
                this.clearWidgets();
                this.init();
            }

            this.isFirstRender = true;
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4 - 20, 0xFFFFFF);
    }

    /* Methods */

    /**
     * Changes the pause screen layout which is based on the current value of the pause layout tweak.
     * Disabling of the LAN button in a non-multiplayer environment is done here.
     */
    private void setLayout()
    {
        switch (this.layout)
        {
            case LAN -> this.setLanLayout();
            case ALPHA_BETA -> this.setAlphaLayout();
            case ADVANCEMENT -> this.setAdvancementLayout();
            case ACHIEVE_LOWER -> this.setLowerAchieveLayout();
            case ACHIEVE_UPPER -> this.setUpperAchieveLayout();
        }

        for (Renderable widget : ((ScreenAccessor) this).NT$getRenderables())
        {
            if (widget instanceof Button button && button.getMessage().getString().equals(this.lan.getString()))
                ((Button) widget).active = !NetUtil.isMultiplayer();
        }
    }

    /**
     * Checks if the current network is multiplayer and is not a local host session.
     * @return Whether the current network environment is connected to a dedicated server.
     */
    private boolean isMultiplayer() { return NetUtil.isMultiplayer() && !NetUtil.isLocalHost(); }

    /**
     * Changes the save button title which is dependent on pause screen layout and multiplayer.
     * @param isLower Whether the title should be lowercase.
     * @return A save button title component.
     */
    private Component getSave(boolean isLower)
    {
        String lower = LangUtil.Gui.PAUSE_SAVE_LOWER;
        String upper = LangUtil.Vanilla.MENU_RETURN_TO_TITLE;

        return this.isMultiplayer() ? this.disconnect : Component.translatable(isLower ? lower : upper);
    }

    /* Button Press Instructions */

    /**
     * Instructions for when the return to game button is pressed.
     * @param unused The button instance is not used.
     */
    private void returnToGame(Button unused)
    {
        if (this.minecraft == null)
            return;

        this.minecraft.setScreen(null);
        this.minecraft.mouseHandler.grabMouse();
    }

    /**
     * Instructions for when the save button or quit to title screen button is pressed.
     * @param button Button instance so that it can be set as inactive.
     */
    private void saveOrQuit(Button button)
    {
        if (this.minecraft == null || this.minecraft.level == null)
            return;

        boolean isLocalServer = this.minecraft.isLocalServer();
        boolean isRealms = this.minecraft.isConnectedToRealms();

        button.active = false;

        this.minecraft.level.disconnect();

        if (isLocalServer)
            this.minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable(LangUtil.Vanilla.SAVE_LEVEL)));
        else
            this.minecraft.clearLevel();

        TitleScreen title = new TitleScreen();

        if (isLocalServer)
            this.minecraft.setScreen(title);
        else if (isRealms)
            this.minecraft.setScreen(new RealmsMainScreen(title));
        else
            this.minecraft.setScreen(new JoinMultiplayerScreen(title));
    }

    /**
     * Instructions that goes to the vanilla options screen.
     * @param unused The button instance is not used.
     */
    private void gotoOptions(Button unused)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
    }

    /**
     * Instructions that goes to the vanilla advancements screen.
     * @param unused The button instance is not used.
     */
    private void gotoAchievements(Button unused)
    {
        if (this.minecraft != null && this.minecraft.player != null)
            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
    }

    /**
     * Instructions that goes to the vanilla statistics screen.
     * @param unused The button instance is not used.
     */
    private void gotoStats(Button unused)
    {
        if (this.minecraft != null && this.minecraft.player != null)
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
    }

    /**
     * Instructions that opens a share to LAN vanilla screen.
     * @param unused The button instance is not used.
     */
    private void gotoLan(Button unused)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new ShareToLanScreen(this));
    }

    /**
     * Instructions that opens a mod loader "mods" screen.
     * @param unused The button instance is not used.
     */
    private void gotoMods(Button unused)
    {
        if (this.minecraft != null && GuiUtil.modScreen != null)
            this.minecraft.setScreen(GuiUtil.modScreen.apply(this.minecraft.screen));
    }

    /* Positioning Getters */

    private int getX() { return this.width / 2 - 100; }
    private int getY() { return this.height / 4; }
    private int getSmallX() { return this.getX() + this.getSmallWidth() + 4; }
    private int getHeight() { return 20; }
    private int getBigWidth() { return 200; }
    private int getSmallWidth() { return 98; }

    private int getFirstRow() { return this.getY() + 8; }
    private int getSecondRow() { return this.getFirstRow() + 24; }
    private int getThirdRow() { return this.getSecondRow() + 24; }
    private int getFourthRow() { return this.getThirdRow() + 24; }
    private int getFifthRow() { return this.getFourthRow() + 24; }
    private int getSixthRow() { return this.getFifthRow() + 24; }

    /* Button Builder */

    /**
     * Shortcut for building a button.
     * @param x The x-position of where the button starts on the screen.
     * @param y The y-position of where the button starts on the screen.
     * @param width The button width.
     * @param height The button height.
     * @param title The button title.
     * @param onPress Instructions to perform when pressed.
     * @return A button instance.
     */
    private Button button(int x, int y, int width, int height, Component title, Button.OnPress onPress)
    {
        return Button.builder(title, onPress)
            .pos(x, y)
            .size(width, height)
            .build()
        ;
    }

    /* Menu Layouts */

    private void setAlphaLayout()
    {
        // Back to game
        this.addRenderableWidget(this.button(this.getX(), this.getY() + 24, this.getBigWidth(), this.getHeight(), this.toLowerBack, this::returnToGame));

        // Save and quit to title
        this.addRenderableWidget(this.button(this.getX(), this.getY() + (24 * 2), this.getBigWidth(), this.getHeight(), this.getSave(true), this::saveOrQuit));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && GuiUtil.modScreen != null)
        {
            this.addRenderableWidget(this.button(this.getX(), this.getY() + (24 * 4), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addRenderableWidget(this.button(this.getSmallX(), this.getY() + (24 * 4), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addRenderableWidget(this.button(this.getX(), this.getY() + (24 * 4), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));
    }

    private void setLowerAchieveLayout()
    {
        // Back to game
        this.addRenderableWidget(this.button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toLowerBack, this::returnToGame));

        // Achievements
        this.addRenderableWidget(this.button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(this.button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && GuiUtil.modScreen != null)
        {
            this.addRenderableWidget(this.button(this.getX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addRenderableWidget(this.button(this.getSmallX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addRenderableWidget(this.button(this.getX(), this.getFourthRow(), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Save and quit to title
        this.addRenderableWidget(this.button(this.getX(), this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(true), this::saveOrQuit));
    }

    private void setUpperAchieveLayout()
    {
        // Back to Game
        this.addRenderableWidget(this.button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Achievements
        this.addRenderableWidget(this.button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(this.button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight() , this.stats, this::gotoStats));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && GuiUtil.modScreen != null)
        {
            this.addRenderableWidget(this.button(this.getX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addRenderableWidget(this.button(this.getSmallX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addRenderableWidget(this.button(this.getX(), this.getFourthRow(), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Save and Quit to Title
        this.addRenderableWidget(this.button(this.getX(), this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::saveOrQuit));
    }

    private void setLanLayout()
    {
        // Back to Game
        this.addRenderableWidget(this.button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Achievements
        this.addRenderableWidget(this.button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(this.button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnPause() && GuiUtil.modScreen != null;
        if (isMods)
            this.addRenderableWidget(this.button(this.getX(), this.getThirdRow(), this.getBigWidth(), this.getHeight(), this.mods, this::gotoMods));

        // Options...
        this.addRenderableWidget(this.button(this.getX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Open to LAN
        this.addRenderableWidget(this.button(this.getSmallX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.lan, this::gotoLan));

        // Save and Quit to Title
        this.addRenderableWidget(this.button(this.getX(), isMods ? this.getSixthRow() : this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::saveOrQuit));
    }

    private void setAdvancementLayout()
    {
        Component advance = Component.translatable(LangUtil.Vanilla.GUI_ADVANCEMENTS);

        // Back to Game
        this.addRenderableWidget(this.button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Advancements
        this.addRenderableWidget(this.button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), advance, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(this.button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnPause() && GuiUtil.modScreen != null;

        if (isMods)
            this.addRenderableWidget(this.button(this.getX(), this.getThirdRow(), this.getBigWidth(), this.getHeight(), this.mods, this::gotoMods));

        // Options...
        this.addRenderableWidget(this.button(this.getX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Open to LAN
        this.addRenderableWidget(this.button(this.getSmallX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.lan, this::gotoLan));

        // Save and Quit to Title
        this.addRenderableWidget(this.button(this.getX(), isMods ? this.getSixthRow() : this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::saveOrQuit));
    }
}
