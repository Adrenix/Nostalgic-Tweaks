package mod.adrenix.nostalgic.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GearSpinner
{
    /* Static */

    private final static String GEAR_FOLDER = NostalgicTweaks.MOD_ID + ":textures/gear/";
    private final static HashMap<Integer, ResourceLocation> GEAR_IMAGES = new HashMap<>();

    /* Fields */

    private final SimpleTimer timer;
    private int frame;

    /* Singleton */

    private GearSpinner()
    {
        this.timer = SimpleTimer.create(30L, TimeUnit.MILLISECONDS).build();
        this.frame = 0;

        if (GEAR_IMAGES.isEmpty())
        {
            for (int i = 0; i < 16; i++)
                GEAR_IMAGES.put(i, new ResourceLocation(GEAR_FOLDER + String.format("%s.png", i)));
        }
    }

    private final static GearSpinner GEAR_SPINNER = new GearSpinner();

    /**
     * The spinner is used by multiple user interfaces. A singleton instance keeps the spinning in sync.
     *
     * @return The singleton gear spinner instance.
     */
    public static GearSpinner getInstance()
    {
        return GEAR_SPINNER;
    }

    /* Methods */

    /**
     * Render the spinning gear logo.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param scale    The scale to render the gear at.
     * @param x        The x-coordinate of the top-left part of the gear.
     * @param y        The y-coordinate of the top-left part of the gear.
     */
    public void render(GuiGraphics graphics, float scale, int x, int y)
    {
        if (this.frame > 15)
            this.frame = 0;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0D);
        graphics.pose().scale(scale, scale, scale);
        graphics.blit(GEAR_IMAGES.get(this.frame), 0, 0, 0, 0, 512, 512, 512, 512);
        graphics.pose().popPose();

        RenderSystem.disableBlend();

        if (this.timer.hasElapsed())
            this.frame++;
    }
}
