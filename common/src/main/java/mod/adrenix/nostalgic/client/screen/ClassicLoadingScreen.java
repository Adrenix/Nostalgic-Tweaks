package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

public class ClassicLoadingScreen extends Screen
{
    /* Fields */

    protected Component title;
    protected Component subtitle;
    protected final StoringChunkProgressListener progressListener;
    protected boolean done;

    /* Constructors */

    public ClassicLoadingScreen(StoringChunkProgressListener progressListener, Component title, Component subtitle)
    {
        super(NarratorChatListener.NO_TITLE);
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
            narrationElementOutput.add(NarratedElementType.TITLE, new TranslatableComponent("narrator.loading.done"));
    }

    @Override
    public void render(PoseStack poses, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft == null)
            return;

        this.renderDirtBackground(0);

        ProgressRenderer.drawTitleText(poses, this, this.title);
        ProgressRenderer.drawSubtitleText(poses, this, this.subtitle);
        ClassicLoadingScreen.renderProgress(this.progressListener);
    }

    /* Classic Rendering */

    protected static void renderProgress(StoringChunkProgressListener progressListener)
    {
        ProgressRenderer.renderProgressWithChunks(progressListener);
    }
}
