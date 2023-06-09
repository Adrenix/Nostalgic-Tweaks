package mod.adrenix.nostalgic.client.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.TimeWatcher;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GearSpinner
{
    /* Static Fields */

    private final static String GEAR_FOLDER = NostalgicTweaks.MOD_ID + ":textures/gear/";
    private final static Map<Integer, ResourceLocation> GEAR_RESOURCE = new HashMap<>();

    /* Fields */

    private final TimeWatcher timer;
    private int frame;

    /* Singleton Construction */

    private GearSpinner()
    {
        this.timer = new TimeWatcher(30L);
        this.frame = 0;

        this.timer.setDebug(false);

        if (GEAR_RESOURCE.isEmpty())
        {
            for (int i = 0; i < 16; i++)
                GEAR_RESOURCE.put(i, new ResourceLocation(GEAR_FOLDER + String.format("%s.png", i)));
        }
    }

    /**
     * Singleton instance of the gear spinner.
     */
    private final static GearSpinner GEAR_SPINNER = new GearSpinner();

    /**
     * The spinner is used by the settings screen and nostalgic toasts.
     * @return The singleton gear spinner instance.
     */
    public static GearSpinner getInstance() { return GEAR_SPINNER; }

    /* Methods */

    /**
     * Render the spinning gear logo.
     * @param graphics The current GuiGraphics object.
     * @param scale The scale of the gear.
     * @param x The starting x-position of where to render the gear logo.
     * @param y The starting y-position of where to render the gear logo.
     */
    public void render(GuiGraphics graphics, float scale, int x, int y)
    {
        if (this.frame > 15)
            this.frame = 0;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float gearScale = scale / 512.0F;

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.translate(x, y, 1.0D);
        poseStack.scale(gearScale, gearScale, gearScale);

        graphics.blit(GEAR_RESOURCE.get(this.frame), 0, 0, 0, 0, 512, 512, 512, 512);

        poseStack.popPose();

        if (this.timer.isReady())
            this.frame++;
    }
}
