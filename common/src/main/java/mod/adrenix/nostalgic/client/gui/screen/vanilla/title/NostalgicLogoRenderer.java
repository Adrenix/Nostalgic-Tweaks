package mod.adrenix.nostalgic.client.gui.screen.vanilla.title;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.MatrixUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;

class NostalgicLogoRenderer
{
    /* Fields */

    private static final String[] MINECRAFT = {
        " *   * * *   * *** *** *** *** *** ***",
        " ** ** * **  * *   *   * * * * *    * ",
        " * * * * * * * **  *   **  *** **   * ",
        " *   * * *  ** *   *   * * * * *    * ",
        " *   * * *   * *** *** * * * * *    * "
    };

    /**
     * A two-dimensional array that holds falling block effect data. Array is set up in [x][y] format.
     */
    private LogoEffectRandomizer[][] logoEffects;

    /**
     * Stores the game's singleton instance.
     */
    private final Minecraft minecraft = Minecraft.getInstance();

    /* Constructor */

    /**
     * Create a new {@link NostalgicLogoRenderer} instance.
     *
     * @param isEasterEgged Whether the title screen should be modified to show an Easter egg.
     */
    public NostalgicLogoRenderer(boolean isEasterEgged)
    {
        if (isEasterEgged)
            MINECRAFT[2] = " * * * * * * * *   **  **  *** **   * ";
        else
            MINECRAFT[2] = " * * * * * * * **  *   **  *** **   * ";
    }

    /* Methods */

    /**
     * Renders the old logo and its falling animation.
     *
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    public void render(float partialTick)
    {
        BakedModel blockModel = this.minecraft.getItemRenderer()
            .getItemModelShaper()
            .getItemModel(Blocks.STONE.asItem());

        if (blockModel == null)
            return;

        if (this.logoEffects == null)
        {
            this.logoEffects = new LogoEffectRandomizer[MINECRAFT[0].length()][MINECRAFT.length];

            for (int x = 0; x < this.logoEffects.length; x++)
            {
                for (int y = 0; y < this.logoEffects[x].length; y++)
                    this.logoEffects[x][y] = new LogoEffectRandomizer(x, y);
            }
        }

        for (LogoEffectRandomizer[] logoEffect : this.logoEffects)
        {
            for (LogoEffectRandomizer logoEffectRandomizer : logoEffect)
                logoEffectRandomizer.update(partialTick);
        }

        Window window = GuiUtil.getWindow();
        int scaleHeight = (int) (120 * window.getGuiScale());

        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().perspective(70.341F, window.getWidth() / (float) scaleHeight, 0.05F, 100.0F), VertexSorting.DISTANCE_TO_ORIGIN);
        RenderSystem.viewport(0, window.getHeight() - scaleHeight, window.getWidth(), scaleHeight);

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        float zOffset = MatrixUtil.getZ(modelViewStack);

        modelViewStack.translate(-0.05F, 0.78F, (-1.0F * zOffset) - 10.0F);
        modelViewStack.scale(1.32F, 1.32F, 1.32F);

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);

        for (int pass = 0; pass < 3; pass++)
        {
            BufferBuilder builder = Tesselator.getInstance().getBuilder();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

            modelViewStack.pushPose();

            if (pass == 0)
            {
                RenderSystem.clear(256, Minecraft.ON_OSX);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                modelViewStack.translate(0.0F, -0.4F, 0.0F);
                modelViewStack.scale(0.98F, 1.0F, 1.0F);
            }

            if (pass == 1)
            {
                RenderSystem.disableBlend();
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            if (pass == 2)
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            }

            modelViewStack.scale(1.0F, -1.0F, 1.0F);
            modelViewStack.mulPose(Axis.XP.rotationDegrees(15.0F));
            modelViewStack.scale(0.89F, 1.0F, 0.4F);
            modelViewStack.translate((float) (-MINECRAFT[0].length()) * 0.5F, (float) (-MINECRAFT.length) * 0.5F, 0.0F);

            if (pass == 0)
            {
                RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
                RenderSystem.setShaderTexture(0, TextureLocation.BLOCK_SHADOW);
            }
            else
            {
                RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            }

            for (int y = 0; y < MINECRAFT.length; y++)
            {
                for (int x = 0; x < MINECRAFT[y].length(); x++)
                {
                    if (MINECRAFT[y].charAt(x) == ' ')
                        continue;

                    modelViewStack.pushPose();

                    float z = this.logoEffects[x][y].position;
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (pass == 0)
                    {
                        scale = z * 0.04F + 1.0F;
                        alpha = 1.0F / scale;
                        z = 0.0F;
                    }

                    modelViewStack.translate(x, y, z);
                    modelViewStack.scale(scale, scale, scale);

                    this.renderBlock(modelViewStack, builder, blockModel, pass, alpha);

                    modelViewStack.popPose();
                }
            }

            Tesselator.getInstance().end();
            modelViewStack.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());

        modelViewStack.setIdentity();
        modelViewStack.translate(0.0F, 0.0F, zOffset);

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
    }

    /**
     * Renders a block for the title logo.
     *
     * @param modelViewStack The model view {@link PoseStack} instance.
     * @param vertexConsumer The {@link VertexConsumer} to write vertices to.
     * @param blockModel     The {@link BakedModel} to get quad data from.
     * @param pass           The render pass index.
     * @param alpha          The alpha transparency for the quad.
     */
    private void renderBlock(PoseStack modelViewStack, VertexConsumer vertexConsumer, BakedModel blockModel, int pass, float alpha)
    {
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

            int light = new Color(brightness, brightness, brightness).get();

            for (BakedQuad quad : blockModel.getQuads(null, direction, RandomSource.create()))
            {
                if (pass == 0)
                    RenderUtil.putTransparentBulkData(modelViewStack.last(), vertexConsumer, quad, brightness, alpha);
                else
                    vertexConsumer.putBulkData(modelViewStack.last(), quad, brightness, brightness, brightness, light, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    /* Randomizer */

    private static class LogoEffectRandomizer
    {
        /* Fields */

        public float position;
        public float speed;

        /* Constructor */

        /**
         * Create a new logo effect randomizer instance.
         *
         * @param x The starting x-position.
         * @param y The starting y-position.
         */
        public LogoEffectRandomizer(int x, int y)
        {
            this.position = (10 + y) + RandomSource.create().nextFloat() * 32.0F + x;
        }

        /**
         * Update the position of this randomizer instance.
         *
         * @param partialTick The change in game frame time.
         */
        public void update(float partialTick)
        {
            if (this.position > 0.0F)
                this.speed -= 0.4F;

            this.position += this.speed * partialTick;
            this.speed *= 0.9F;

            if (this.position < 0.0F)
            {
                this.position = 0.0F;
                this.speed = 0.0F;
            }
        }
    }
}
