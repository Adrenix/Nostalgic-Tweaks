package mod.adrenix.nostalgic.client.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.TimeWatcher;
import net.minecraft.client.gui.GuiComponent;
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

    /* Constructor */

    public GearSpinner()
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

    /* Methods */

    /**
     * Render the spinning gear logo.
     * @param poseStack The current pose stack.
     * @param x The starting x-position of where to render the gear logo.
     * @param y The starting y-position of where to render the gear logo.
     */
    public void render(PoseStack poseStack, int x, int y)
    {
        if (this.frame > 15)
            this.frame = 0;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GEAR_RESOURCE.get(this.frame));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float gearScale = 44.279F / 512.0F;

        poseStack.pushPose();
        poseStack.translate(x, y, 1.0D);
        poseStack.scale(gearScale, gearScale, gearScale);

        GuiComponent.blit(poseStack, 0, 0, 0, 0, 512, 512, 512, 512);

        poseStack.popPose();

        if (this.timer.isReady())
            this.frame++;
    }
}
