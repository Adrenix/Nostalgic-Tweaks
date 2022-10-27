package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

public class NostalgicLoadingScreen extends Screen
{
    /* Fields */

    protected Component title;
    protected Component subtitle;
    protected final StoringChunkProgressListener progressListener;
    protected boolean done;

    /* Constructors */

    public NostalgicLoadingScreen(StoringChunkProgressListener progressListener, Component title, Component subtitle)
    {
        super(Component.empty());
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
        this.triggerImmediateNarration(true);
    }

    @Override
    protected void updateNarratedWidget(NarrationElementOutput narrationElementOutput)
    {
        if (this.done)
            narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narrator.loading.done"));
    }

    @Override
    public void render(PoseStack poses, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft == null)
            return;

        this.renderDirtBackground(0);

        ProgressRenderer.drawTitleText(poses, this, this.title);
        ProgressRenderer.drawSubtitleText(poses, this, this.subtitle);
        NostalgicLoadingScreen.renderProgress(this.progressListener);
    }

    /* Classic Rendering */

    protected static void renderProgress(StoringChunkProgressListener progressListener)
    {
        ProgressRenderer.renderProgressWithChunks(progressListener);
    }
}
