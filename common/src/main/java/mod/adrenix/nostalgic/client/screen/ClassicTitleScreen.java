package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.mixin.widen.IMixinScreen;
import mod.adrenix.nostalgic.mixin.widen.IMixinTitleScreen;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

public class ClassicTitleScreen extends TitleScreen
{
    /* Fields */

    protected static final String[] MINECRAFT = {
        " *   * * *   * *** *** *** *** *** ***",
        " ** ** * **  * *   *   * * * * *    * ",
        " * * * * * * * **  *   **  *** **   * ",
        " *   * * *  ** *   *   * * * * *    * ",
        " *   * * *   * *** *** * * * * *    * "
    };

    public static boolean isGameReady = false;
    protected LogoEffectRandomizer[][] logoEffects;
    protected long updateLogoDelay;
    protected float updateCounter;
    protected static final Random RANDOM = new Random();
    private final PanoramaRenderer panorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);

    /* Constructor */

    public ClassicTitleScreen()
    {
        this.updateCounter = this.getRand().nextFloat();
        this.updateLogoDelay = 0L;
    }

    /* Overrides */

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_M && this.minecraft != null)
            this.minecraft.setScreen(new ClassicTitleScreen());
        return true;
    }

    @Override
    public void tick()
    {
        if (this.logoEffects != null)
            for (LogoEffectRandomizer[] logoEffect : this.logoEffects)
                for (LogoEffectRandomizer logoEffectRandomizer : logoEffect)
                    logoEffectRandomizer.run();
        super.tick();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (MixinConfig.Candy.oldTitleBackground())
            this.renderDirtBackground(0);
        else
            this.panorama.render(partialTick, 1.0F);

        if (this.updateLogoDelay == 0L)
            this.updateLogoDelay = Util.getMillis();

        if (this.minecraft == null || !ClassicTitleScreen.isGameReady && Util.getMillis() - this.updateLogoDelay < 1200)
            return;

        if (MixinConfig.Candy.oldAlphaLogo())
            this.renderClassicLogo(partialTick);
        else
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.MINECRAFT_LOGO);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int width = this.width / 2 - 137;
            int height = 30;

            if (MixinConfig.Candy.oldLogoOutline())
            {
                this.blit(poseStack, width, height, 0, 0, 155, 44);
                this.blit(poseStack, width + 155, height, 0, 45, 155, 44);
            }
            else
            {
                this.blitOutlineBlack(width, height, (x, y) -> {
                    this.blit(poseStack, x, y, 0, 0, 155, 44);
                    this.blit(poseStack, x + 155, y, 0, 45, 155, 44);
                });
            }
        }

        ClassicTitleScreen.isGameReady = true;
        IMixinTitleScreen accessor = (IMixinTitleScreen) this;
        IMixinScreen screen = (IMixinScreen) this;
        int color = Mth.ceil(255.0F) << 24;

        if (accessor.getSplash() != null)
        {
            poseStack.pushPose();
            poseStack.translate((float) this.width / 2 + 90, 70.0, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));

            float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
            scale = scale * 100.0F / (float) (this.font.width(accessor.getSplash()) + 32);
            poseStack.scale(scale, scale, scale);

            TitleScreen.drawCenteredString(poseStack, this.font, accessor.getSplash(), 0, -8, 0xFFFF00 | color);

            poseStack.popPose();
        }

        String minecraft = MixinConfig.Candy.getVersionText();

        if (Minecraft.checkModStatus().shouldReportAsModified() && !MixinConfig.Candy.removeTitleModLoaderText())
            minecraft = minecraft + "/" + this.minecraft.getVersionType() + I18n.get("menu.modded");

        int versionColor = MixinConfig.Candy.oldTitleBackground() && !minecraft.contains("§") ? 5263440 : 0xFFFFFF;
        TitleScreen.drawString(poseStack, this.font, minecraft, 2, 2, versionColor);
        TitleScreen.drawString(poseStack, this.font, COPYRIGHT_TEXT, this.width - this.font.width(COPYRIGHT_TEXT) - 2, this.height - 10, 0xFFFFFF);

        for (GuiEventListener child : this.children())
        {
            if (child instanceof AbstractWidget)
                ((AbstractWidget) child).setAlpha(1.0F);
        }

        for (Widget widget : screen.getRenderables())
            widget.render(poseStack, mouseX, mouseY, partialTick);

        if (accessor.getRealmsNotificationsEnabled())
            accessor.getRealmsNotificationsScreen().render(poseStack, mouseX, mouseY, partialTick);
    }

    /* Methods */

    protected void renderClassicLogo(float partialTick)
    {
        if (this.minecraft == null) return;
        if (this.logoEffects == null)
        {
            this.logoEffects = new LogoEffectRandomizer[MINECRAFT[0].length()][MINECRAFT.length];
            for (int horizontal = 0; horizontal < this.logoEffects.length; horizontal++)
                for (int vertical = 0; vertical < this.logoEffects[horizontal].length; vertical++)
                    logoEffects[horizontal][vertical] = new LogoEffectRandomizer(this, horizontal, vertical);
        }

        RenderSystem.enableDepthTest();

        Window window = this.minecraft.getWindow();
        int scaleHeight = (int) (120 * window.getGuiScale());

        RenderSystem.setProjectionMatrix(Matrix4f.perspective(70, (float) window.getWidth() / (float) scaleHeight, 0.05F, 100F));
        RenderSystem.viewport(0, window.getHeight() - scaleHeight, window.getWidth(), scaleHeight);

        PoseStack model = RenderSystem.getModelViewStack();
        model.translate(0.0, 0.0, 2001.0);

        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);

        for (int row = 0; row < 3; row++)
        {
            model.pushPose();
            model.translate(0.4F, 0.6F, -13F);

            if (row == 0)
            {
                RenderSystem.clear(256, Minecraft.ON_OSX);
                model.translate(0.0F, -0.4F, 0.0F);
                model.scale(0.98F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(770, 771);
            }

            if (row == 1)
            {
                RenderSystem.disableBlend();
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            if (row == 2)
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(768, 1);
            }

            model.scale(1.0F, -1.0F, 1.0F);
            model.mulPose(Vector3f.XP.rotationDegrees(15.0F));
            model.scale(0.89F, 1.0F, 0.4F);
            model.translate((float) (-MINECRAFT[0].length()) * 0.5F, (float) (-MINECRAFT.length) * 0.5F, 0.0F);

            if (row == 0)
            {
                RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
                RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.BLACK_RESOURCE);
            }
            else
            {
                RenderSystem.setShader(GameRenderer::getBlockShader);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            }

            for (int horizontal = 0; horizontal < MINECRAFT.length; horizontal++)
            {
                for (int vertical = 0; vertical < MINECRAFT[horizontal].length(); vertical++)
                {
                    char symbol = MINECRAFT[horizontal].charAt(vertical);
                    if (horizontal == 2 && ((double) this.updateCounter < 0.0001D))
                        symbol = MINECRAFT[horizontal].charAt(vertical == 20 ? vertical - 1 : (vertical == 16 ? vertical + 1 : vertical));

                    if (symbol == ' ')
                        continue;

                    model.pushPose();

                    LogoEffectRandomizer logo = logoEffects[vertical][horizontal];

                    float depth = (float) (logo.vertical + (logo.horizontal - logo.vertical) * (double) partialTick);
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (row == 0)
                    {
                        scale = depth * 0.04F + 1.0F;
                        alpha = 1.0F / scale;
                        depth = 0.0F;
                    }

                    model.translate(vertical, horizontal, depth);
                    model.scale(scale, scale, scale);
                    renderBlock(model, row, alpha);
                    model.popPose();
                }
            }

            model.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(Matrix4f.orthographic(0F, (float) ((double) window.getWidth() / window.getGuiScale()), 0F, (float) ((double) window.getHeight() / window.getGuiScale()), 1000F, 3000F));
        RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());
        model.setIdentity();
        model.translate(0, 0, -2000);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
    }

    public Random getRand()
    {
        return RANDOM;
    }

    private static int getColorFromRGBA(float red, float green, float blue, float alpha)
    {
        return (int) (alpha * 255.0F) << 24 | (int) (red * 255.0F) << 16 | (int) (green * 255.0F) << 8 | (int) (blue * 255.0F);
    }

    private static int getColorFromBrightness(float brightness, float alpha)
    {
        return getColorFromRGBA(brightness, brightness, brightness, alpha);
    }

    private void renderQuad(PoseStack.Pose pose, BufferBuilder bufferbuilder, BakedQuad quad, float brightness, float alpha)
    {
        int combinedLight = getColorFromBrightness(brightness, alpha);
        int[] vertices = quad.getVertices();
        Vec3i vec = quad.getDirection().getNormal();
        Vector3f vec3f = new Vector3f(vec.getX(), vec.getY(), vec.getZ());
        Matrix4f matrix = pose.pose();
        vec3f.transform(pose.normal());

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            ByteBuffer byteBuffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            for (int i = 0; i < vertices.length / 8; i++)
            {
                intBuffer.clear();
                intBuffer.put(vertices, i * 8, 8);
                float x = byteBuffer.getFloat(0);
                float y = byteBuffer.getFloat(4);
                float z = byteBuffer.getFloat(8);

                Vector4f vec4f = new Vector4f(x, y, z, 1.0F);
                vec4f.transform(matrix);

                bufferbuilder.vertex(vec4f.x(), vec4f.y(), vec4f.z(), 1.0F, 1.0F, 1.0F, alpha, byteBuffer.getFloat(16), byteBuffer.getFloat(20), OverlayTexture.NO_OVERLAY, combinedLight, vec3f.x(), vec3f.y(), vec3f.z());
            }
        }
    }

    private void renderBlock(PoseStack modelView, int row, float alpha)
    {
        modelView.translate(-0.5F, -0.5F, -0.5F);
        BakedModel model = this.itemRenderer.getItemModelShaper().getItemModel(Blocks.STONE.asItem().getDefaultInstance());

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

        for (Direction direction : Direction.values())
        {
            float brightness = switch (direction)
            {
                case DOWN -> 1.0F;
                case UP -> 0.5F;
                case NORTH -> 0.0F;
                case SOUTH -> 0.8F;
                case WEST, EAST -> 0.6F;
            };

            int color = getColorFromBrightness(brightness, alpha);

            for (BakedQuad quad : model.getQuads(null, direction, this.getRand()))
            {
                if (row == 0)
                    renderQuad(modelView.last(), bufferbuilder, quad, brightness, alpha);
                else
                    bufferbuilder.putBulkData(modelView.last(), quad, brightness, brightness, brightness, color, OverlayTexture.NO_OVERLAY);
            }
        }

        tesselator.end();
        modelView.translate(0.5F, 0.5F, 0.5F);
    }

    /* Logo Effect Randomizer */

    protected static class LogoEffectRandomizer
    {
        public double horizontal;
        public double vertical;
        public double depth;

        public LogoEffectRandomizer(ClassicTitleScreen screen, int horizontal, int vertical)
        {
            this.horizontal = this.vertical = (double) (10 + vertical) + screen.getRand().nextDouble() * 32D + (double) horizontal;
        }

        public void run()
        {
            this.vertical = this.horizontal;

            if (this.horizontal > 0.0D)
                this.depth -= 0.59D;

            this.horizontal += this.depth;
            this.depth *= 0.9D;

            if (this.horizontal < 0.0D)
            {
                this.horizontal = 0.0D;
                this.depth = 0.0D;
            }
        }
    }
}
