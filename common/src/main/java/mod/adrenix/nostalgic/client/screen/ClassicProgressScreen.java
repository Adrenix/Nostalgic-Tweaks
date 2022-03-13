package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.widen.IMixinProgressScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClassicProgressScreen extends Screen implements ProgressListener
{
    /* Fields */

    protected @Nullable Component header;
    protected @Nullable Component stage;
    protected int progress = 0;
    protected double pauseTicking = 0.98;
    protected boolean renderProgressBar = true;
    protected boolean ticking = false;
    protected boolean stop;
    protected IMixinProgressScreen progressScreen;
    protected static ResourceKey<Level> previousDimension;
    protected static ResourceKey<Level> currentDimension;
    protected static final int MAX = 100;
    public static final double NO_PAUSES = 1.0;

    /* Constructor */

    public ClassicProgressScreen(ProgressScreen progressScreen)
    {
        super(NarratorChatListener.NO_TITLE);
        this.progressScreen = (IMixinProgressScreen) progressScreen;
    }

    /* Setters / Getters */

    public boolean isTicking() { return this.ticking; }
    public void setRenderProgressBar(boolean state) { this.renderProgressBar = state; }
    public void setPauseTicking(double pause) { this.pauseTicking = pause; }
    public void setHeader(@Nullable Component header) { this.header = header; }
    public void setStage(@Nullable Component stage) { this.stage = stage; }
    public static void setPreviousDimension(ResourceKey<Level> setter) { previousDimension = setter; }
    public static void setCurrentDimension(ResourceKey<Level> setter) { currentDimension = setter; }
    public static @Nullable ResourceKey<Level> getPreviousDimension() { return previousDimension; }
    public static @Nullable ResourceKey<Level> getCurrentDimension() { return currentDimension; }

    /* Overrides */

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public void progressStartNoAbort(Component component) {}

    @Override
    public void progressStart(Component header) {}

    @Override
    public void progressStage(Component stage) {}

    @Override
    public void progressStagePercentage(int progress) {}

    @Override
    public void stop() { this.stop = true; }

    @Override
    public void removed() { this.stop(); }

    @Override
    public void render(PoseStack poses, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft == null) return;

        this.generateText();

        if (this.header == null && this.stage == null)
        {
            this.ticking = false;
            return;
        }

        this.renderDirtBackground(0);
        if (this.renderProgressBar)
            ProgressRenderer.renderProgressWithInt(this.progress);

        this.renderDrawableText(poses);

        if (this.stop)
        {
            if (this.progressScreen.getClearScreenAfterStop())
                this.minecraft.setScreen(null);
        }
    }

    /* Helpers */

    private static void doNothing() {}
    public void renderProgress()
    {
        Minecraft minecraft = Minecraft.getInstance();

        this.ticking = true;
        this.progress = -1;

        while (minecraft.isRunning() && this.ticking && this.progress < ClassicProgressScreen.MAX)
        {
            long start = Util.getMillis();
            double pause = Math.random();
            double wait = (long) ((Math.random()) + (pause > pauseTicking ? Math.random() * 1000 : 0));
            while (Util.getMillis() - start < wait) { ClassicProgressScreen.doNothing(); }
            this.progress++;
            minecraft.forceSetScreen(this);
        }

        this.ticking = false;
    }

    protected void generateText()
    {
        Minecraft minecraft = Minecraft.getInstance();
        TranslatableComponent header = (TranslatableComponent) this.header;
        ResourceKey<Level> currentLevel = getCurrentDimension();
        ResourceKey<Level> previousLevel = getPreviousDimension();

        if (header != null && header.getKey().equals("menu.savingLevel"))
        {
            this.setHeader(null);
            this.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_SAVING));
            this.setPauseTicking(NO_PAUSES);
        }

        boolean isTextNeeded = header == null && this.stage == null;
        boolean isMultiplayer = minecraft.getConnection() != null;
        boolean isConnectedLevel = isMultiplayer && minecraft.getConnection().getLevel() != null;
        boolean isChangingLevel = minecraft.player != null && currentLevel != null && previousLevel != null;

        if (isTextNeeded && (!isMultiplayer || isConnectedLevel) && isChangingLevel)
        {
            if (currentLevel == Level.NETHER)
            {
                this.setHeader(new TranslatableComponent(NostalgicLang.Gui.LEVEL_ENTER_NETHER));
                this.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_BUILDING));
            }
            else if (currentLevel == Level.END)
            {
                this.setHeader(new TranslatableComponent(NostalgicLang.Gui.LEVEL_ENTER_END));
                this.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_BUILDING));
            }
            else if (currentLevel == Level.OVERWORLD)
            {
                if (previousLevel == Level.NETHER)
                {
                    this.setHeader(new TranslatableComponent(NostalgicLang.Gui.LEVEL_LEAVING_NETHER));
                    this.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_BUILDING));
                }
                else if (previousLevel == Level.END)
                {
                    this.setHeader(new TranslatableComponent(NostalgicLang.Gui.LEVEL_LEAVING_END));
                    this.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_BUILDING));
                }
            }

            if (this.stage == null)
            {
                this.setHeader(new TranslatableComponent(NostalgicLang.Gui.LEVEL_LOADING));
                this.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_BUILDING));
            }
        }
    }

    protected void renderDrawableText(PoseStack poses)
    {
        if (this.header != null)
            ProgressRenderer.drawTitleText(poses, this, this.header);

        if (this.stage != null)
            ProgressRenderer.drawSubtitleText(poses, this, this.stage);
    }
}
