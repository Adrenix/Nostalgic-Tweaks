package mod.adrenix.nostalgic.client.screen;

import mod.adrenix.nostalgic.mixin.widen.ProgressScreenAccessor;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;

/**
 * This screen defines the instructions that are performed while progress is being made.
 *
 * Since these types of screens no longer serve a purpose in vanilla, the nostalgic progress screen simulates the old
 * style of loading by randomly ticking up a progress bar.
 */

public class NostalgicProgressScreen extends Screen implements ProgressListener
{
    /* Fields */

    private Component header;
    private Component stage;

    private int progress = 0;
    private double pauseTicking = 0.98;
    private boolean renderProgressBar = true;
    private boolean ticking = false;
    private boolean stop;
    private final ProgressScreenAccessor progressScreen;

    private static ResourceKey<Level> previousDimension;
    private static ResourceKey<Level> currentDimension;

    public static final double NO_PAUSES = 1.0;

    /* Constructor */

    /**
     * Create a new nostalgic progress screen instance.
     * @param progressScreen A vanilla progress screen.
     */
    public NostalgicProgressScreen(ProgressScreen progressScreen)
    {
        super(Component.empty());

        this.progressScreen = (ProgressScreenAccessor) progressScreen;
    }

    /* Setters / Getters */

    /**
     * Retrieves the current state of the ticking flag.
     * @return Whether the progress screen is ticking progress.
     */
    public boolean isTicking() { return this.ticking; }

    /**
     * Set the state of the render progress bar flag.
     * @param state A boolean.
     */
    public void setRenderProgressBar(boolean state) { this.renderProgressBar = state; }

    /**
     * Set the amount of milliseconds to wait per tick pause.
     * @param pause A time to wait after a pause in ticking in milliseconds.
     */
    public void setPauseTicking(double pause) { this.pauseTicking = pause; }

    /**
     * Set the header for the progress screen.
     * @param header A header component.
     */
    public void setHeader(@Nullable Component header) { this.header = header; }

    /**
     * Set the stage (subtitle) for the progress screen.
     * @param stage A subtitle component.
     */
    public void setStage(@Nullable Component stage) { this.stage = stage; }

    /**
     * Set the previous dimension that was loaded.
     * @param setter A level resource key.
     */
    public static void setPreviousDimension(ResourceKey<Level> setter) { previousDimension = setter; }

    /**
     * Set the current dimension that player is in.
     * @param setter A level resource key.
     */
    public static void setCurrentDimension(ResourceKey<Level> setter) { currentDimension = setter; }

    /**
     * Gets the last dimension that the player was in.
     * @return A level resource key.
     */
    @CheckReturnValue
    public static ResourceKey<Level> getPreviousDimension() { return previousDimension; }

    /**
     * Gets the current dimension that the player is in.
     * @return A level resource key.
     */
    @CheckReturnValue
    public static ResourceKey<Level> getCurrentDimension() { return currentDimension; }

    /* Overrides */

    /**
     * Handler method that prevents the screen from closing when the Esc key is pressed.
     * @return Always returns <code>false</code>.
     */
    @Override
    public boolean shouldCloseOnEsc() { return false; }

    /**
     * Handler method that provides instructions for when progress is stopped.
     */
    @Override
    public void stop() { this.stop = true; }

    /**
     * Handler method that provides instructions for when the screen is closed.
     */
    @Override
    public void removed() { this.stop(); }

    /**
     * Handler method that provides instructions for rendering this screen.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft == null)
            return;

        this.setHeaderAndStage();

        if (this.header == null && this.stage == null)
        {
            this.ticking = false;
            return;
        }

        this.renderDirtBackground(graphics);

        if (this.renderProgressBar)
            ProgressRenderer.renderProgressWithInt(this.progress);

        this.renderDrawableText(graphics);

        if (this.stop)
        {
            if (this.progressScreen.NT$getClearScreenAfterStop())
                this.minecraft.setScreen(null);
        }
    }

    /* Required Screen Overrides */

    @Override
    public void progressStartNoAbort(Component component) { }

    @Override
    public void progressStart(Component header) { }

    @Override
    public void progressStage(Component stage) { }

    @Override
    public void progressStagePercentage(int progress) { }

    /* Helpers */

    /**
     * Progresses through an entire progress bar. This is done randomly and the random behavior can be controlled by
     * various fields.
     */
    public void load()
    {
        Minecraft minecraft = Minecraft.getInstance();

        this.ticking = true;
        this.progress = -1;

        while (minecraft.isRunning() && this.ticking && this.progress < 100)
        {
            long start = Util.getMillis();
            double pause = Math.random();
            double wait = (long) ((Math.random()) + (pause > this.pauseTicking ? Math.random() * 1000 : 0));

            while (Util.getMillis() - start < wait)
                RunUtil.nothing();

            this.progress++;

            minecraft.forceSetScreen(this);
        }

        this.ticking = false;
    }

    /**
     * This method changes the title and subtitle of the nostalgic progress screen.
     *
     * Different titles/subtitles are used depending on whether the screen is being used for saving or whether the
     * player is moving to a new a dimension.
     */
    private void setHeaderAndStage()
    {
        Minecraft minecraft = Minecraft.getInstance();
        MutableComponent header = (MutableComponent) this.header;
        ResourceKey<Level> currentLevel = getCurrentDimension();
        ResourceKey<Level> previousLevel = getPreviousDimension();

        if (header != null && header.getString().equals(Component.translatable("menu.savingLevel").getString()))
        {
            this.setHeader(null);
            this.setStage(Component.translatable(LangUtil.Gui.LEVEL_SAVING));
            this.setPauseTicking(NO_PAUSES);
        }

        boolean isTextNeeded = header == null && this.stage == null;
        boolean isMultiplayer = minecraft.getConnection() != null;
        boolean isConnectedLevel = isMultiplayer && minecraft.level != null;
        boolean isChangingLevel = minecraft.player != null && currentLevel != null && previousLevel != null;

        if (isTextNeeded && (!isMultiplayer || isConnectedLevel) && isChangingLevel)
        {
            if (currentLevel == Level.NETHER)
            {
                this.setHeader(Component.translatable(LangUtil.Gui.LEVEL_ENTER_NETHER));
                this.setStage(Component.translatable(LangUtil.Gui.LEVEL_BUILDING));
            }
            else if (currentLevel == Level.END)
            {
                this.setHeader(Component.translatable(LangUtil.Gui.LEVEL_ENTER_END));
                this.setStage(Component.translatable(LangUtil.Gui.LEVEL_BUILDING));
            }
            else if (currentLevel == Level.OVERWORLD)
            {
                if (previousLevel == Level.NETHER)
                {
                    this.setHeader(Component.translatable(LangUtil.Gui.LEVEL_LEAVING_NETHER));
                    this.setStage(Component.translatable(LangUtil.Gui.LEVEL_BUILDING));
                }
                else if (previousLevel == Level.END)
                {
                    this.setHeader(Component.translatable(LangUtil.Gui.LEVEL_LEAVING_END));
                    this.setStage(Component.translatable(LangUtil.Gui.LEVEL_BUILDING));
                }
            }

            if (this.stage == null)
            {
                this.setHeader(Component.translatable(LangUtil.Gui.LEVEL_LOADING));
                this.setStage(Component.translatable(LangUtil.Gui.LEVEL_BUILDING));
            }
        }
    }

    /**
     * Renders any text that is eligible for being drawn.
     * @param graphics The current GuiGraphics object.
     */
    private void renderDrawableText(GuiGraphics graphics)
    {
        if (this.header != null)
            ProgressRenderer.drawTitleText(graphics, this, this.header);

        if (this.stage != null)
            ProgressRenderer.drawSubtitleText(graphics, this, this.stage);
    }
}
