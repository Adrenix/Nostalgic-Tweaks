package mod.adrenix.nostalgic.mixin.util.candy.debug;

import mod.adrenix.nostalgic.mixin.access.FpsDebugChartAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.debugchart.LocalSampleLogger;

import java.util.function.Function;

class DebugChartRenderer
{
    /* Fields */

    final int width;
    final int height;
    final LocalSampleLogger logger;
    final GuiGraphics graphics;
    final Function<Double, Integer> getSampleHeight;
    final Function<Long, Integer> getSampleColor;

    /* Constructor */

    DebugChartRenderer(FpsDebugChart fpsChart, LocalSampleLogger logger, GuiGraphics graphics)
    {
        this.width = Math.min(fpsChart.getWidth(graphics.guiWidth() / 2), LocalSampleLogger.CAPACITY);
        this.height = 76;
        this.logger = logger;
        this.graphics = graphics;
        this.getSampleHeight = ((FpsDebugChartAccess) fpsChart)::nt$getSampleHeight;
        this.getSampleColor = ((FpsDebugChartAccess) fpsChart)::nt$getSampleColor;
    }

    /* Methods */

    public void render()
    {
        long minVal = Integer.MIN_VALUE;
        long maxVal = Integer.MAX_VALUE;
        int maxSize = Math.max(0, this.logger.capacity() - this.width);
        int numOfValues = this.logger.size() - maxSize;

        this.graphics.pose().pushPose();
        this.graphics.pose().translate(0.0F, this.graphics.guiHeight() - (this.height / 2.0F), 0.0F);
        this.graphics.pose().scale(0.5F, 0.5F, 0.5F);
        this.graphics.fill(RenderType.guiOverlay(), 0, 0, this.width, this.height, 0x90200000);
        this.graphics.fill(RenderType.guiOverlay(), 0, 36, this.width, this.height, 0x90000000);

        for (int i = 0; i < numOfValues; i++)
        {
            long value = this.logger.get(maxSize + i);

            minVal = Math.min(minVal, value);
            maxVal = Math.max(maxVal, value);

            int minY = this.getSampleHeight.apply((double) value);
            int color = this.getSampleColor.apply(value);

            this.graphics.fill(RenderType.guiOverlay(), i, this.height - minY, i + 1, this.height, color);
        }

        int fpsLimit = Minecraft.getInstance().options.framerateLimit().get();

        if (fpsLimit > 0 && fpsLimit <= 250)
            this.graphics.hLine(RenderType.guiOverlay(), 0, this.width - 1, this.height - this.getSampleHeight.apply(1.0E9 / (double) fpsLimit) - 1, 0xFFFF0000);

        this.graphics.pose().popPose();
    }
}
