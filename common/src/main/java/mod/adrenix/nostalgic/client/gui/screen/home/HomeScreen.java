package mod.adrenix.nostalgic.client.gui.screen.home;

import com.mojang.math.Axis;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.GearSpinner;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.screen.packs.PacksListScreen;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class HomeScreen extends EnhancedScreen<HomeScreen, HomeWidgets>
{
    /* Fields */

    private static final TextureLocation TITLE_LOCATION = TextureLocation.NOSTALGIC_TWEAKS;
    private static final String BETA_VERSION = NostalgicTweaks.getBetaVersion();
    private static final String MOD_VERSION = NostalgicTweaks.getTinyVersion();
    private boolean isRedirected;
    private HomeWidgets homeWidgets;
    private String splash;

    /* Constructor */

    public HomeScreen(@Nullable Screen parentScreen, boolean isRedirected)
    {
        super(HomeWidgets::new, parentScreen, Lang.TITLE.get());

        this.isRedirected = isRedirected;

        if (!ModTweak.OPENED_CONFIG_SCREEN.get())
            ModTweak.OPENED_CONFIG_SCREEN.setDiskAndSave(true);
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public HomeScreen self()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HomeWidgets getWidgetManager()
    {
        return this.homeWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidgetManager(HomeWidgets widgetManager)
    {
        this.homeWidgets = widgetManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        super.init();

        this.splash = HomeSplash.getInstance().get();
        this.focusFirst();

        if (this.isRedirected)
        {
            this.isRedirected = false;

            switch (ModTweak.DEFAULT_SCREEN.get())
            {
                case CONFIG_SCREEN -> this.minecraft.setScreen(new ConfigScreen(this));
                case PACKS_SCREEN -> this.minecraft.setScreen(new PacksListScreen(this));
                default -> this.minecraft.setScreen(this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft.level == null)
            Panorama.render(graphics, partialTick);
        else
            graphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        this.renderTextures(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Renders the mod's textures (logos and splashes) to the home screen.
     *
     * @param graphics A {@link GuiGraphics} instance.
     */
    private void renderTextures(GuiGraphics graphics)
    {
        float betaScale = (int) GuiUtil.getGuiScale() % 2 == 0 ? 0.5F : 0.7F;
        float pulseScale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
        float versionScale = pulseScale * 17.0F / (float) (this.font.width(MOD_VERSION));
        float splashScale = pulseScale * 100.0f / (float) (this.font.width(this.splash) + 32);
        float titleScale = 0.15F;
        float gearScale = 0.07F;

        int titleW = Math.round(TITLE_LOCATION.getWidth() * titleScale);
        int titleH = Math.round(TITLE_LOCATION.getHeight() * titleScale);
        int titleX = Math.round(MathUtil.center(titleW, this.width));
        int titleY = Math.round(GuiUtil.getGuiHeight() * 0.09F);
        int gearY = Math.round(GuiUtil.getGuiHeight() - (gearScale * 512.0F));
        int gearX = 0;

        float splashX = titleX + titleW - 20.0F;
        float splashY = titleY + titleH - 2.0F;
        float versionX = gearX + (gearScale * 512.0F) - 3.0F;
        float versionY = gearY + (gearScale * 512.0F) - 9.0F;
        float betaX = gearX + (gearScale * 512.0F) + 11.0F;
        float betaY = gearY + (gearScale * 512.0F) - 9.0F;

        GearSpinner.getInstance().render(graphics, gearScale, gearX, gearY);

        RenderUtil.beginBatching();

        graphics.pose().pushPose();
        graphics.pose().translate(versionX, versionY, 0.0D);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
        graphics.pose().scale(versionScale, versionScale, versionScale);

        DrawText.begin(graphics, MOD_VERSION).pos(1, -6).color(Color.YELLOW).center().draw();

        graphics.pose().popPose();

        RenderUtil.blitTexture(TITLE_LOCATION, graphics, titleScale, titleX, titleY);

        graphics.pose().pushPose();
        graphics.pose().translate(splashX, splashY, 0.0D);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
        graphics.pose().scale(splashScale, splashScale, splashScale);

        DrawText.begin(graphics, this.splash).pos(0, -8).color(Color.YELLOW).center().draw();

        graphics.pose().popPose();

        if (BETA_VERSION.isEmpty())
        {
            RenderUtil.endBatching();
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(betaX, betaY, 0.0D);
        graphics.pose().scale(betaScale, betaScale, betaScale);

        DrawText.begin(graphics, BETA_VERSION).color(Color.YELLOW).draw();

        graphics.pose().popPose();

        RenderUtil.endBatching();
    }
}
