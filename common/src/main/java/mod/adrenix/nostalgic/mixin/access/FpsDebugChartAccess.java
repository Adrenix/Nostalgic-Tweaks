package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FpsDebugChart.class)
public interface FpsDebugChartAccess
{
    @Invoker("getSampleHeight")
    int nt$getSampleHeight(double value);

    @Invoker("getSampleColor")
    int nt$getSampleColor(long value);
}
