package mod.adrenix.nostalgic.mixin.util.candy.debug;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * This utility class is used only by the client.
 */
public abstract class DebugMixinHelper
{
    /**
     * Determine if an entity's debug id should be shown.
     *
     * @param entity An {@link Entity} instance.
     * @return Whether an entity's debug id should be shown.
     */
    public static boolean shouldShowDebugId(Entity entity)
    {
        boolean isDebugging = Minecraft.getInstance().gui.getDebugOverlay().showDebugScreen();
        boolean isValidTarget = entity instanceof LivingEntity && !(entity instanceof Player);

        return CandyTweak.DEBUG_ENTITY_ID.get() && isDebugging && isValidTarget;
    }

    /**
     * Render nostalgic debug text.
     *
     * @param overlay  The vanilla {@link DebugScreenOverlay} instance.
     * @param graphics The {@link GuiGraphics} instance.
     */
    public static void renderDebugText(DebugScreenOverlay overlay, GuiGraphics graphics)
    {
        new DebugInfoRenderer(overlay, graphics).render();
    }

    /**
     * Render the nostalgic FPS chart.
     *
     * @param fpsChart The {@link FpsDebugChart} instance.
     * @param logger   The {@link LocalSampleLogger} instance.
     * @param graphics The {@link GuiGraphics} instance.
     */
    public static void renderFpsChart(FpsDebugChart fpsChart, LocalSampleLogger logger, GuiGraphics graphics)
    {
        new DebugChartRenderer(fpsChart, logger, graphics).render();
    }
}
