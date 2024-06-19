package mod.adrenix.nostalgic.client.gui.screen.vanilla.progress;

import mod.adrenix.nostalgic.mixin.access.ProgressScreenAccess;
import mod.adrenix.nostalgic.mixin.access.ReceivingLevelScreenAccess;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class NostalgicProgressScreen extends ProgressScreen implements ProgressListener
{
    /* Static */

    public static final NullableHolder<ResourceKey<Level>> PREVIOUS_DIMENSION = NullableHolder.empty();
    public static final NullableHolder<ResourceKey<Level>> CURRENT_DIMENSION = NullableHolder.empty();

    /* Fields */

    @Nullable private Component header;
    @Nullable private Component stage;
    private int progress = -1;
    private boolean renderProgress = true;
    private boolean stop = false;
    private final ProgressScreenAccess progressScreen;
    @Nullable private final ReceivingLevelScreenAccess levelScreen;

    /* Constructor */

    /**
     * Create a new old progress screen.
     *
     * @param progressScreen A {@link ProgressScreen} instance.
     */
    public NostalgicProgressScreen(ProgressScreen progressScreen)
    {
        super(((ProgressScreenAccess) progressScreen).nt$clearScreenAfterStop());

        this.progressScreen = (ProgressScreenAccess) progressScreen;
        this.levelScreen = null;
    }

    /**
     * Create a new old progress screen using a receiving level state.
     *
     * @param progressScreen A {@link ProgressScreen} instance.
     * @param levelScreen    A {@link ReceivingLevelScreen} instance.
     */
    public NostalgicProgressScreen(ProgressScreen progressScreen, ReceivingLevelScreen levelScreen)
    {
        super(((ProgressScreenAccess) progressScreen).nt$clearScreenAfterStop());

        this.progressScreen = (ProgressScreenAccess) progressScreen;
        this.levelScreen = (ReceivingLevelScreenAccess) levelScreen;
    }

    /* Methods */

    /**
     * Set the header message for the progress screen.
     *
     * @param header The {@link Component} header.
     */
    public void setHeader(@Nullable Component header)
    {
        this.header = header;
    }

    /**
     * Set the stage message for the progress screen.
     *
     * @param stage The {@link Component} stage.
     */
    public void setStage(@Nullable Component stage)
    {
        this.stage = stage;
    }

    /**
     * @return Whether the progress screen has a stage message.
     */
    public boolean hasStage()
    {
        return this.stage != null;
    }

    /**
     * This method changes the title and subtitle of the progress screen. Different headers and stages are used
     * depending on whether the screen is being used for saving or whether the player is moving to a different
     * dimension.
     */
    private void setHeaderAndStage()
    {
        if (this.minecraft == null)
            return;

        if (this.header != null && this.header.getString().equals(Lang.Vanilla.SAVE_LEVEL.getString()))
        {
            this.setHeader(null);
            this.setStage(Lang.Level.SAVING.get());
        }

        ResourceKey<Level> currentDimension = CURRENT_DIMENSION.get();
        ResourceKey<Level> previousDimension = PREVIOUS_DIMENSION.get();

        boolean isTextNeeded = this.header == null && this.stage == null;
        boolean isMultiplayer = this.minecraft.getConnection() != null;
        boolean isConnectedLevel = this.minecraft.level != null && isMultiplayer;
        boolean isChangingLevel = this.minecraft.player != null && currentDimension != null && previousDimension != null;

        if (isTextNeeded && (!isMultiplayer || isConnectedLevel) && isChangingLevel)
        {
            if (currentDimension == Level.NETHER)
            {
                this.setHeader(Lang.Level.ENTER_NETHER.get());
                this.setStage(Lang.Level.BUILDING.get());
            }
            else if (currentDimension == Level.END)
            {
                this.setHeader(Lang.Level.ENTER_END.get());
                this.setStage(Lang.Level.BUILDING.get());
            }
            else if (currentDimension == Level.OVERWORLD)
            {
                if (previousDimension == Level.NETHER)
                {
                    this.setHeader(Lang.Level.LEAVING_NETHER.get());
                    this.setStage(Lang.Level.BUILDING.get());
                }
                else if (previousDimension == Level.END)
                {
                    this.setHeader(Lang.Level.LEAVING_END.get());
                    this.setStage(Lang.Level.BUILDING.get());
                }
            }

            if (this.stage == null)
            {
                this.setHeader(Lang.Level.LOADING.get());
                this.setStage(Lang.Level.BUILDING.get());
            }
        }
    }

    /**
     * Set whether the progress bar is visible.
     *
     * @param state The progress bar visibility state.
     */
    public void setProgressVisibility(boolean state)
    {
        this.renderProgress = state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop()
    {
        this.stop = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removed()
    {
        this.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick()
    {
        if (this.progress < 100)
            this.progress++;

        super.tick();

        if (this.levelScreen != null)
        {
            boolean isLevelReceived = this.levelScreen.nt$getLevelReceived().getAsBoolean();
            boolean isTimeoutFinished = System.currentTimeMillis() > this.levelScreen.nt$getCreatedAt() + 30000L;

            if (isLevelReceived || isTimeoutFinished)
                this.onClose();
        }
    }

    /**
     * Renders the header and stage if they are non-null.
     *
     * @param graphics The {@link GuiGraphics} instance.
     */
    private void renderText(GuiGraphics graphics)
    {
        if (this.header != null)
            ProgressRenderer.drawHeaderText(graphics, this.header, this.width);

        if (this.stage != null)
            ProgressRenderer.drawStageText(graphics, this.stage, this.width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft == null)
            return;

        if (this.stop)
        {
            if (this.progressScreen.nt$clearScreenAfterStop())
                this.minecraft.setScreen(null);

            return;
        }

        this.setHeaderAndStage();

        if (this.header == null && this.stage == null)
            return;

        this.renderMenuBackground(graphics);

        if (this.renderProgress)
            ProgressRenderer.renderProgressWithInt(this.progress);

        this.renderText(graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressStartNoAbort(Component component)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressStart(Component header)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressStage(Component stage)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressStagePercentage(int progress)
    {
        this.progress = progress;
    }
}
