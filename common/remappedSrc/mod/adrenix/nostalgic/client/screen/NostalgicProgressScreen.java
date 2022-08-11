package mod.adrenix.nostalgic.client.screen;

import mod.adrenix.nostalgic.mixin.widen.IMixinProgressScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NostalgicProgressScreen extends Screen implements ProgressListener
{
    /* Fields */

    protected @Nullable Text header;
    protected @Nullable Text stage;
    protected int progress = 0;
    protected double pauseTicking = 0.98;
    protected boolean renderProgressBar = true;
    protected boolean ticking = false;
    protected boolean stop;
    protected IMixinProgressScreen progressScreen;
    protected static RegistryKey<World> previousDimension;
    protected static RegistryKey<World> currentDimension;
    protected static final int MAX = 100;
    public static final double NO_PAUSES = 1.0;

    /* Constructor */

    public NostalgicProgressScreen(ProgressScreen progressScreen)
    {
        super(NarratorManager.EMPTY);
        this.progressScreen = (IMixinProgressScreen) progressScreen;
    }

    /* Setters / Getters */

    public boolean isTicking() { return this.ticking; }
    public void setRenderProgressBar(boolean state) { this.renderProgressBar = state; }
    public void setPauseTicking(double pause) { this.pauseTicking = pause; }
    public void setHeader(@Nullable Text header) { this.header = header; }
    public void setStage(@Nullable Text stage) { this.stage = stage; }
    public static void setPreviousDimension(RegistryKey<World> setter) { previousDimension = setter; }
    public static void setCurrentDimension(RegistryKey<World> setter) { currentDimension = setter; }
    public static @Nullable RegistryKey<World> getPreviousDimension() { return previousDimension; }
    public static @Nullable RegistryKey<World> getCurrentDimension() { return currentDimension; }

    /* Overrides */

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public void setTitle(Text component) {}

    @Override
    public void setTitleAndTask(Text header) {}

    @Override
    public void setTask(Text stage) {}

    @Override
    public void progressStagePercentage(int progress) {}

    @Override
    public void setDone() { this.stop = true; }

    @Override
    public void removed() { this.setDone(); }

    @Override
    public void render(MatrixStack poses, int mouseX, int mouseY, float partialTick)
    {
        if (this.client == null) return;

        this.generateText();

        if (this.header == null && this.stage == null)
        {
            this.ticking = false;
            return;
        }

        this.renderBackgroundTexture(0);
        if (this.renderProgressBar)
            ProgressRenderer.renderProgressWithInt(this.progress);

        this.renderDrawableText(poses);

        if (this.stop)
        {
            if (this.progressScreen.NT$getClearScreenAfterStop())
                this.client.setScreen(null);
        }
    }

    /* Helpers */

    public void renderProgress()
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();

        this.ticking = true;
        this.progress = -1;

        while (minecraft.isRunning() && this.ticking && this.progress < NostalgicProgressScreen.MAX)
        {
            long start = Util.getMeasuringTimeMs();
            double pause = Math.random();
            double wait = (long) ((Math.random()) + (pause > pauseTicking ? Math.random() * 1000 : 0));

            while (Util.getMeasuringTimeMs() - start < wait)
                NostalgicUtil.Run.nothing();

            this.progress++;
            minecraft.setScreenAndRender(this);
        }

        this.ticking = false;
    }

    protected void generateText()
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        MutableText header = (MutableText) this.header;
        RegistryKey<World> currentLevel = getCurrentDimension();
        RegistryKey<World> previousLevel = getPreviousDimension();

        if (header != null && header.getString().equals(Text.translatable("menu.savingLevel").getString()))
        {
            this.setHeader(null);
            this.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_SAVING));
            this.setPauseTicking(NO_PAUSES);
        }

        boolean isTextNeeded = header == null && this.stage == null;
        boolean isMultiplayer = minecraft.getNetworkHandler() != null;
        boolean isConnectedLevel = isMultiplayer && minecraft.getNetworkHandler().getWorld() != null;
        boolean isChangingLevel = minecraft.player != null && currentLevel != null && previousLevel != null;

        if (isTextNeeded && (!isMultiplayer || isConnectedLevel) && isChangingLevel)
        {
            if (currentLevel == World.NETHER)
            {
                this.setHeader(Text.translatable(NostalgicLang.Gui.LEVEL_ENTER_NETHER));
                this.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_BUILDING));
            }
            else if (currentLevel == World.END)
            {
                this.setHeader(Text.translatable(NostalgicLang.Gui.LEVEL_ENTER_END));
                this.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_BUILDING));
            }
            else if (currentLevel == World.OVERWORLD)
            {
                if (previousLevel == World.NETHER)
                {
                    this.setHeader(Text.translatable(NostalgicLang.Gui.LEVEL_LEAVING_NETHER));
                    this.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_BUILDING));
                }
                else if (previousLevel == World.END)
                {
                    this.setHeader(Text.translatable(NostalgicLang.Gui.LEVEL_LEAVING_END));
                    this.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_BUILDING));
                }
            }

            if (this.stage == null)
            {
                this.setHeader(Text.translatable(NostalgicLang.Gui.LEVEL_LOADING));
                this.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_BUILDING));
            }
        }
    }

    protected void renderDrawableText(MatrixStack poses)
    {
        if (this.header != null)
            ProgressRenderer.drawTitleText(poses, this, this.header);

        if (this.stage != null)
            ProgressRenderer.drawSubtitleText(poses, this, this.stage);
    }
}
