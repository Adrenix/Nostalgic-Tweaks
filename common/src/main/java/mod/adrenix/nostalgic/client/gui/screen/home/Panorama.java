package mod.adrenix.nostalgic.client.gui.screen.home;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.common.array.CycleIndex;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public enum Panorama
{
    ALPHA,
    BETA,
    CAVE,
    NETHER,
    VILLAGE,
    CLASSIC,
    SLIDE,
    DEEP;

    /* Static */

    private static final Animation FADE_IN_ANIMATION = Animation.linear(4L, TimeUnit.SECONDS);
    private static final SimpleTimer SWITCH_TIMER = SimpleTimer.create(15L, TimeUnit.SECONDS).immediate().build();
    private static final CycleIndex CYCLE_INDEX = new CycleIndex(Panorama.values());

    /**
     * @return The {@link Panorama} to display.
     */
    private static Panorama getDisplaying()
    {
        return Panorama.values()[CYCLE_INDEX.get()];
    }

    /**
     * @return The {@link Panorama} that was last displayed.
     */
    private static Panorama getLastDisplaying()
    {
        return Panorama.values()[CYCLE_INDEX.getLast()];
    }

    /**
     * Render the cycled panorama.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param partialTick The normalized progress made between two ticks (0.0F-1.0F).
     */
    public static void render(GuiGraphics graphics, float partialTick)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (FADE_IN_ANIMATION.isNotFinished())
            Panorama.getLastDisplaying().draw(graphics, partialTick);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) FADE_IN_ANIMATION.getValue());
        Panorama.getDisplaying().draw(graphics, partialTick);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Ticking instructions that handles the index cycle and fade animation.
     */
    public static void onTick()
    {
        if (SWITCH_TIMER.hasElapsed())
        {
            CYCLE_INDEX.cycle();

            FADE_IN_ANIMATION.reset();
            FADE_IN_ANIMATION.play();
        }
    }

    /* Fields */

    private final PanoramaRenderer panorama;

    /* Constructor */

    Panorama()
    {
        String root = NostalgicTweaks.MOD_ID + ":textures/panorama/";
        String branch = this.toString().toLowerCase(Locale.ROOT);

        this.panorama = new PanoramaRenderer(new CubeMap(new ResourceLocation(root + branch + "/panorama")));
    }

    /* Methods */

    /**
     * Render this panorama.
     *
     * @param graphics    The current pose stack.
     * @param partialTick The normalized progress made between two ticks (0.0F-1.0F).
     */
    private void draw(GuiGraphics graphics, float partialTick)
    {
        this.panorama.render(partialTick, 1.0F);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableBlend();

        graphics.blit(TextureLocation.PANORAMA_OVERLAY, 0, 0, GuiUtil.getScreenWidth(), GuiUtil.getScreenHeight(), 16, 128, 16, 128);
    }
}