package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.widen.IMixinScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

public class NostalgicPauseScreen extends Screen
{
    /* Fields */

    private final TweakVersion.PauseLayout layout;
    private final Component mods = Component.translatable(NostalgicLang.Gui.CANDY_TITLE_MODS);
    private final Component lan = Component.translatable(NostalgicLang.Vanilla.MENU_LAN);
    private final Component stats = Component.translatable(NostalgicLang.Vanilla.GUI_STATS);
    private final Component options = Component.translatable(NostalgicLang.Vanilla.MENU_OPTIONS);
    private final Component disconnect = Component.translatable(NostalgicLang.Vanilla.MENU_DISCONNECT);
    private final Component toUpperBack = Component.translatable(NostalgicLang.Vanilla.MENU_RETURN_TO_GAME);
    private final Component toLowerBack = Component.translatable(NostalgicLang.Gui.PAUSE_RETURN_LOWER);
    private final Component achievements = Component.translatable(NostalgicLang.Gui.PAUSE_ACHIEVEMENTS);

    /* Constructor Helper */

    private static String getPauseTitle()
    {
        return switch (ModConfig.Candy.getPauseLayout())
        {
            case ALPHA_BETA, ACHIEVE_LOWER, ACHIEVE_UPPER, LAN -> NostalgicLang.Gui.PAUSE_GAME;
            case ADVANCEMENT, MODERN -> NostalgicLang.Vanilla.MENU_GAME;
        };
    }

    /* Constructor */

    public NostalgicPauseScreen()
    {
        super(Component.translatable(getPauseTitle()));
        this.layout = ModConfig.Candy.getPauseLayout();
    }

    /* Overrides */

    @Override
    protected void init() { this.getLayout(); }

    @Override
    public void tick() { super.tick(); }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        Screen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 40, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    /* Methods */

    private void getLayout()
    {
        if (this.layout.equals(TweakVersion.PauseLayout.ALPHA_BETA))
            this.getAlphaLayout();
        else if (this.layout.equals(TweakVersion.PauseLayout.ACHIEVE_LOWER))
            this.getLowerAchieveLayout();
        else if (this.layout.equals(TweakVersion.PauseLayout.ACHIEVE_UPPER))
            this.getUpperAchieveLayout();
        else if (this.layout.equals(TweakVersion.PauseLayout.LAN))
            this.getLanLayout();
        else if (this.layout.equals(TweakVersion.PauseLayout.ADVANCEMENT))
            this.getAdvancementLayout();

        for (Widget widget : ((IMixinScreen) this).NT$getRenderables())
        {
            if (widget instanceof Button button && button.getMessage().getString().equals(this.lan.getString()))
                ((Button) widget).active = !NetClientUtil.isMultiplayer();
        }
    }

    private boolean isMultiplayer()
    {
        return NetClientUtil.isMultiplayer() && !NetClientUtil.isLocalHost();
    }

    private Component getSave(boolean isLower)
    {
        String lower = NostalgicLang.Gui.PAUSE_SAVE_LOWER;
        String upper = NostalgicLang.Vanilla.MENU_RETURN_TO_TITLE;
        return this.isMultiplayer() ? this.disconnect : Component.translatable(isLower ? lower : upper);
    }

    /* Button Lambdas */

    private void returnToGame(Button ignored)
    {
        if (this.minecraft == null)
            return;

        this.minecraft.setScreen(null);
        this.minecraft.mouseHandler.grabMouse();
    }

    private void returnToTitle(Button button)
    {
        if (this.minecraft == null || this.minecraft.level == null)
            return;

        boolean isLocalServer = this.minecraft.isLocalServer();
        boolean isRealms = this.minecraft.isConnectedToRealms();

        button.active = false;
        this.minecraft.level.disconnect();

        if (isLocalServer)
            this.minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable(NostalgicLang.Vanilla.SAVE_LEVEL)));
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

    private void gotoOptions(Button ignored)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
    }

    private void gotoAchievements(Button ignored)
    {
        if (this.minecraft != null && this.minecraft.player != null)
            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
    }

    private void gotoStats(Button ignored)
    {
        if (this.minecraft != null && this.minecraft.player != null)
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
    }

    private void gotoLan(Button ignored)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new ShareToLanScreen(this));
    }

    private void gotoMods(Button ignored)
    {
        if (this.minecraft != null && ModClientUtil.Gui.modScreen != null)
            this.minecraft.setScreen(ModClientUtil.Gui.modScreen.apply(this.minecraft.screen));
    }

    /* Positioning */

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

    /* Menu Layouts */

    private void getAlphaLayout()
    {
        // Back to game
        this.addRenderableWidget(new Button(this.getX(), this.getY() + 24, this.getBigWidth(), this.getHeight(), this.toLowerBack, this::returnToGame));

        // Save and quit to title
        this.addRenderableWidget(new Button(this.getX(), this.getY() + (24 * 2), this.getBigWidth(), this.getHeight(), this.getSave(true), this::returnToTitle));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null)
        {
            this.addRenderableWidget(new Button(this.getX(), this.getY() + (24 * 4), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addRenderableWidget(new Button(this.getSmallX(), this.getY() + (24 * 4), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addRenderableWidget(new Button(this.getX(), this.getY() + (24 * 4), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));
    }

    private void getLowerAchieveLayout()
    {
        // Back to game
        this.addRenderableWidget(new Button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toLowerBack, this::returnToGame));

        // Achievements
        this.addRenderableWidget(new Button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(new Button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null)
        {
            this.addRenderableWidget(new Button(this.getX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addRenderableWidget(new Button(this.getSmallX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addRenderableWidget(new Button(this.getX(), this.getFourthRow(), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Save and quit to title
        this.addRenderableWidget(new Button(this.getX(), this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(true), this::returnToTitle));
    }

    private void getUpperAchieveLayout()
    {
        // Back to Game
        this.addRenderableWidget(new Button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Achievements
        this.addRenderableWidget(new Button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(new Button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight() , this.stats, this::gotoStats));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null)
        {
            this.addRenderableWidget(new Button(this.getX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addRenderableWidget(new Button(this.getSmallX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addRenderableWidget(new Button(this.getX(), this.getFourthRow(), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Save and Quit to Title
        this.addRenderableWidget(new Button(this.getX(), this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::returnToTitle));
    }

    private void getLanLayout()
    {
        // Back to Game
        this.addRenderableWidget(new Button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Achievements
        this.addRenderableWidget(new Button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(new Button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null;
        if (isMods)
            this.addRenderableWidget(new Button(this.getX(), this.getThirdRow(), this.getBigWidth(), this.getHeight(), this.mods, this::gotoMods));

        // Options...
        this.addRenderableWidget(new Button(this.getX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Open to LAN
        this.addRenderableWidget(new Button(this.getSmallX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.lan, this::gotoLan));

        // Save and Quit to Title
        this.addRenderableWidget(new Button(this.getX(), isMods ? this.getSixthRow() : this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::returnToTitle));
    }

    private void getAdvancementLayout()
    {
        Component advance = Component.translatable(NostalgicLang.Vanilla.GUI_ADVANCEMENTS);

        // Back to Game
        this.addRenderableWidget(new Button(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Advancements
        this.addRenderableWidget(new Button(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), advance, this::gotoAchievements));

        // Statistics
        this.addRenderableWidget(new Button(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null;
        if (isMods)
            this.addRenderableWidget(new Button(this.getX(), this.getThirdRow(), this.getBigWidth(), this.getHeight(), this.mods, this::gotoMods));

        // Options...
        this.addRenderableWidget(new Button(this.getX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Open to LAN
        this.addRenderableWidget(new Button(this.getSmallX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.lan, this::gotoLan));

        // Save and Quit to Title
        this.addRenderableWidget(new Button(this.getX(), isMods ? this.getSixthRow() : this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::returnToTitle));
    }
}
