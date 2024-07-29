package mod.adrenix.nostalgic.helper.candy.debug;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * This utility class is used only by the client.
 */
public abstract class DebugOverlayHelper
{
    /**
     * Determine if an entity's debug id should be shown.
     *
     * @param entity An {@link Entity} instance.
     * @return Whether an entity's debug id should be shown.
     */
    public static boolean shouldShowDebugId(Entity entity)
    {
        Minecraft minecraft = Minecraft.getInstance();
        boolean isDebugging = minecraft.options.renderDebug && !minecraft.options.hideGui;
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
     * @param graphics   The {@link GuiGraphics} instance.
     * @param frameTimer The {@link FrameTimer} instance.
     */
    public static void renderFpsChart(GuiGraphics graphics, FrameTimer frameTimer)
    {
        new DebugChartRenderer(graphics, frameTimer).render();
    }
}
