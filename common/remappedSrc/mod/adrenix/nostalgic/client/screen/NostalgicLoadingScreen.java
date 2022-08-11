package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class NostalgicLoadingScreen extends Screen
{
    /* Fields */

    protected Text title;
    protected Text subtitle;
    protected final WorldGenerationProgressTracker progressListener;
    protected boolean done;

    /* Constructors */

    public NostalgicLoadingScreen(WorldGenerationProgressTracker progressListener, Text title, Text subtitle)
    {
        super(NarratorManager.EMPTY);
        this.progressListener = progressListener;
        this.title = title;
        this.subtitle = subtitle;
    }

    /* Overrides */

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public void removed()
    {
        this.done = true;
        this.narrateScreenIfNarrationEnabled(true);
    }

    @Override
    protected void addElementNarrations(NarrationMessageBuilder narrationElementOutput)
    {
        if (this.done)
            narrationElementOutput.put(NarrationPart.TITLE, Text.translatable("narrator.loading.done"));
    }

    @Override
    public void render(MatrixStack poses, int mouseX, int mouseY, float partialTick)
    {
        if (this.client == null)
            return;

        this.renderBackgroundTexture(0);

        ProgressRenderer.drawTitleText(poses, this, this.title);
        ProgressRenderer.drawSubtitleText(poses, this, this.subtitle);
        NostalgicLoadingScreen.renderProgress(this.progressListener);
    }

    /* Classic Rendering */

    protected static void renderProgress(WorldGenerationProgressTracker progressListener)
    {
        ProgressRenderer.renderProgressWithChunks(progressListener);
    }
}
