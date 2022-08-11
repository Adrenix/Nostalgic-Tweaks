package mod.adrenix.nostalgic.client.screen;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.widen.IMixinScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class NostalgicPauseScreen extends Screen
{
    /* Fields */

    private final TweakVersion.PauseLayout layout;
    private final Text mods = Text.translatable(NostalgicLang.Gui.CANDY_TITLE_MODS);
    private final Text lan = Text.translatable(NostalgicLang.Vanilla.MENU_LAN);
    private final Text stats = Text.translatable(NostalgicLang.Vanilla.GUI_STATS);
    private final Text options = Text.translatable(NostalgicLang.Vanilla.MENU_OPTIONS);
    private final Text disconnect = Text.translatable(NostalgicLang.Vanilla.MENU_DISCONNECT);
    private final Text toUpperBack = Text.translatable(NostalgicLang.Vanilla.MENU_RETURN_TO_GAME);
    private final Text toLowerBack = Text.translatable(NostalgicLang.Gui.PAUSE_RETURN_LOWER);
    private final Text achievements = Text.translatable(NostalgicLang.Gui.PAUSE_ACHIEVEMENTS);

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
        super(Text.translatable(getPauseTitle()));
        this.layout = ModConfig.Candy.getPauseLayout();
    }

    /* Overrides */

    @Override
    protected void init() { this.getLayout(); }

    @Override
    public void tick() { super.tick(); }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        Screen.drawCenteredText(poseStack, this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
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

        for (Drawable widget : ((IMixinScreen) this).NT$getRenderables())
        {
            if (widget instanceof ButtonWidget button && button.getMessage().getString().equals(this.lan.getString()))
                ((ButtonWidget) widget).active = !NetClientUtil.isMultiplayer();
        }
    }

    private boolean isMultiplayer()
    {
        return NetClientUtil.isMultiplayer() && !NetClientUtil.isLocalHost();
    }

    private Text getSave(boolean isLower)
    {
        String lower = NostalgicLang.Gui.PAUSE_SAVE_LOWER;
        String upper = NostalgicLang.Vanilla.MENU_RETURN_TO_TITLE;
        return this.isMultiplayer() ? this.disconnect : Text.translatable(isLower ? lower : upper);
    }

    /* Button Lambdas */

    private void returnToGame(ButtonWidget ignored)
    {
        if (this.client == null)
            return;

        this.client.setScreen(null);
        this.client.mouse.lockCursor();
    }

    private void returnToTitle(ButtonWidget button)
    {
        if (this.client == null || this.client.world == null)
            return;

        boolean isLocalServer = this.client.isInSingleplayer();
        boolean isRealms = this.client.isConnectedToRealms();

        button.active = false;
        this.client.world.disconnect();

        if (isLocalServer)
            this.client.disconnect(new SaveLevelScreen(Text.translatable(NostalgicLang.Vanilla.SAVE_LEVEL)));
        else
            this.client.disconnect();

        TitleScreen title = new TitleScreen();

        if (isLocalServer)
            this.client.setScreen(title);
        else if (isRealms)
            this.client.setScreen(new RealmsMainScreen(title));
        else
            this.client.setScreen(new MultiplayerScreen(title));
    }

    private void gotoOptions(ButtonWidget ignored)
    {
        if (this.client != null)
            this.client.setScreen(new OptionsScreen(this, this.client.options));
    }

    private void gotoAchievements(ButtonWidget ignored)
    {
        if (this.client != null && this.client.player != null)
            this.client.setScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()));
    }

    private void gotoStats(ButtonWidget ignored)
    {
        if (this.client != null && this.client.player != null)
            this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
    }

    private void gotoLan(ButtonWidget ignored)
    {
        if (this.client != null)
            this.client.setScreen(new OpenToLanScreen(this));
    }

    private void gotoMods(ButtonWidget ignored)
    {
        if (this.client != null && ModClientUtil.Gui.modScreen != null)
            this.client.setScreen(ModClientUtil.Gui.modScreen.apply(this.client.currentScreen));
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
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getY() + 24, this.getBigWidth(), this.getHeight(), this.toLowerBack, this::returnToGame));

        // Save and quit to title
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getY() + (24 * 2), this.getBigWidth(), this.getHeight(), this.getSave(true), this::returnToTitle));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null)
        {
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getY() + (24 * 4), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getY() + (24 * 4), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getY() + (24 * 4), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));
    }

    private void getLowerAchieveLayout()
    {
        // Back to game
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toLowerBack, this::returnToGame));

        // Achievements
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null)
        {
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getFourthRow(), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Save and quit to title
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(true), this::returnToTitle));
    }

    private void getUpperAchieveLayout()
    {
        // Back to Game
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Achievements
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight() , this.stats, this::gotoStats));

        // Mods and/or Options...
        if (ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null)
        {
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));
            this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.mods, this::gotoMods));
        }
        else
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getFourthRow(), this.getBigWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Save and Quit to Title
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::returnToTitle));
    }

    private void getLanLayout()
    {
        // Back to Game
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Achievements
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.achievements, this::gotoAchievements));

        // Statistics
        this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null;
        if (isMods)
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getThirdRow(), this.getBigWidth(), this.getHeight(), this.mods, this::gotoMods));

        // Options...
        this.addDrawableChild(new ButtonWidget(this.getX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Open to LAN
        this.addDrawableChild(new ButtonWidget(this.getSmallX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.lan, this::gotoLan));

        // Save and Quit to Title
        this.addDrawableChild(new ButtonWidget(this.getX(), isMods ? this.getSixthRow() : this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::returnToTitle));
    }

    private void getAdvancementLayout()
    {
        Text advance = Text.translatable(NostalgicLang.Vanilla.GUI_ADVANCEMENTS);

        // Back to Game
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getFirstRow(), this.getBigWidth(), this.getHeight(), this.toUpperBack, this::returnToGame));

        // Advancements
        this.addDrawableChild(new ButtonWidget(this.getX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), advance, this::gotoAchievements));

        // Statistics
        this.addDrawableChild(new ButtonWidget(this.getSmallX(), this.getSecondRow(), this.getSmallWidth(), this.getHeight(), this.stats, this::gotoStats));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnPause() && ModClientUtil.Gui.modScreen != null;
        if (isMods)
            this.addDrawableChild(new ButtonWidget(this.getX(), this.getThirdRow(), this.getBigWidth(), this.getHeight(), this.mods, this::gotoMods));

        // Options...
        this.addDrawableChild(new ButtonWidget(this.getX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.options, this::gotoOptions));

        // Open to LAN
        this.addDrawableChild(new ButtonWidget(this.getSmallX(), isMods ? this.getFifthRow() : this.getFourthRow(), this.getSmallWidth(), this.getHeight(), this.lan, this::gotoLan));

        // Save and Quit to Title
        this.addDrawableChild(new ButtonWidget(this.getX(), isMods ? this.getSixthRow() : this.getFifthRow(), this.getBigWidth(), this.getHeight(), this.getSave(false), this::returnToTitle));
    }
}
