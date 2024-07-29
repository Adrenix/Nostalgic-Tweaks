package mod.adrenix.nostalgic.helper.candy.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;

class DebugChartRenderer
{
    /* Fields */

    final int width;
    final int height;
    final GuiGraphics graphics;
    final FrameTimer frameTimer;

    /* Constructor */

    DebugChartRenderer(GuiGraphics graphics, FrameTimer frameTimer)
    {
        this.width = Math.min(graphics.guiWidth() / 2, FrameTimer.LOGGING_LENGTH);
        this.height = 76;
        this.graphics = graphics;
        this.frameTimer = frameTimer;
    }

    /* Methods */

    public void render()
    {
        long minVal = Integer.MIN_VALUE;
        long maxVal = Integer.MAX_VALUE;
        long[] logs = this.frameTimer.getLog();
        int maxSize = Math.max(0, logs.length - this.width);
        int numOfValues = logs.length - maxSize;
        int logStart = this.frameTimer.getLogStart();
        int wrapIndex = this.frameTimer.wrapIndex(Math.max(0, logStart + maxSize - 1));

        this.graphics.pose().pushPose();
        this.graphics.pose().translate(0.0F, this.graphics.guiHeight() - (this.height / 2.0F), 0.0F);
        this.graphics.pose().scale(0.5F, 0.5F, 0.5F);
        this.graphics.fill(RenderType.guiOverlay(), 0, 0, this.width, this.height, 0x90200000);
        this.graphics.fill(RenderType.guiOverlay(), 0, 36, this.width, this.height, 0x90000000);

        for (int i = 0; i < numOfValues; i++)
        {
            long value = logs[this.frameTimer.wrapIndex(wrapIndex + i)];

            minVal = Math.min(minVal, value);
            maxVal = Math.max(maxVal, value);

            int minY = this.getSampleHeight((double) value);
            int color = this.getSampleColor(value);

            this.graphics.fill(RenderType.guiOverlay(), i, this.height - minY, i + 1, this.height, color);
        }

        int fpsLimit = Minecraft.getInstance().options.framerateLimit().get();

        if (fpsLimit > 0 && fpsLimit <= 250)
            this.graphics.hLine(RenderType.guiOverlay(), 0, this.width - 1, this.height - this.getSampleHeight(1.0E9 / (double) fpsLimit) - 1, 0xFFFF0000);

        this.graphics.pose().popPose();
    }

    private int getSampleHeight(double value)
    {
        return (int) Math.round(this.toMillis(value) * 60.0D / (100.0D / 3.0D));
    }

    private int getSampleColor(long value)
    {
        long clamp = (long) Mth.clamp(this.toMillis((double) value), 0.0D, 56.0D);
        int min = FastColor.ARGB32.lerp((float) (clamp / (28.0D - 0.0D)), -16711936, -256);
        int max = FastColor.ARGB32.lerp((float) ((clamp - 28.0D) / (56.0D - 28.0D)), -256, -65536);

        return clamp < 28.0D ? min : max;
    }

    private double toMillis(double value)
    {
        return value / 1000000.0D;
    }
}
