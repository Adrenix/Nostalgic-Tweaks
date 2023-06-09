package mod.adrenix.nostalgic.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

/**
 * This class overrides the vanilla loading screen.
 * The old green bar is loaded by a chunk progress listener, or is loaded randomly when a listener is not available.
 */

public class NostalgicLoadingScreen extends Screen
{
    /* Fields */

    protected Component title;
    protected Component subtitle;
    protected final StoringChunkProgressListener progressListener;
    protected boolean done;

    /* Constructor */

    /**
     * Create a new nostalgic loading screen instance.
     * @param progressListener A chunk progress listener instance.
     * @param title A level loading title component.
     * @param subtitle A level loading state subtitle component.
     */
    public NostalgicLoadingScreen(StoringChunkProgressListener progressListener, Component title, Component subtitle)
    {
        super(Component.empty());

        this.progressListener = progressListener;
        this.title = title;
        this.subtitle = subtitle;
    }

    /* Overrides */

    /**
     * Prevents the screen from closing when the Esc key is pressed.
     * @return Always returns <code>false</code>.
     */
    @Override
    public boolean shouldCloseOnEsc() { return false; }

    /**
     * Handler method that provides instructions for when the screen is closed.
     */
    @Override
    public void removed()
    {
        this.done = true;
        this.triggerImmediateNarration(true);
    }

    /**
     * Handler method that provides instructions for narration when the screen is done loading.
     * @param narrationElementOutput A narration element output.
     */
    @Override
    protected void updateNarratedWidget(NarrationElementOutput narrationElementOutput)
    {
        if (this.done)
            narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narrator.loading.done"));
    }

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

        this.renderDirtBackground(graphics);

        ProgressRenderer.drawTitleText(graphics, this, this.title);
        ProgressRenderer.drawSubtitleText(graphics, this, this.subtitle);
        ProgressRenderer.renderProgressWithChunks(this.progressListener);
    }
}
