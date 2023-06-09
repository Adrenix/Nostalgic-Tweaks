package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class NostalgicLogoRenderer
{
    /* Fields */

    private static final String[] MINECRAFT =
    {
        " *   * * *   * *** *** *** *** *** ***",
        " ** ** * **  * *   *   * * * * *    * ",
        " * * * * * * * **  *   **  *** **   * ",
        " *   * * *  ** *   *   * * * * *    * ",
        " *   * * *   * *** *** * * * * *    * "
    };

    /**
     * This two-dimensional array holds falling block data.
     * The array is set up in [x][y] format.
     */
    private LogoEffectRandomizer[][] logoEffects;

    /**
     * Stores a pointer to the game's singleton instance.
     */
    private final Minecraft minecraft = Minecraft.getInstance();

    /* Constructor */

    public NostalgicLogoRenderer(boolean isEasterEgged)
    {
        if (isEasterEgged)
            MINECRAFT[2] = " * * * * * * * *   **  **  *** **   * ";
        else
            MINECRAFT[2] = " * * * * * * * **  *   **  *** **   * ";
    }

    /* Methods */

    /**
     * Instructions for rendering the classic logo and the introduction falling animation.
     * @param partialTick The change in game frame time.
     */
    public void render(float partialTick)
    {
        if (this.logoEffects == null)
        {
            this.logoEffects = new LogoEffectRandomizer[MINECRAFT[0].length()][MINECRAFT.length];

            for (int x = 0; x < this.logoEffects.length; x++)
                for (int y = 0; y < this.logoEffects[x].length; y++)
                    this.logoEffects[x][y] = new LogoEffectRandomizer(x, y);
        }

        for (LogoEffectRandomizer[] logoEffect : this.logoEffects)
            for (LogoEffectRandomizer logoEffectRandomizer : logoEffect)
                logoEffectRandomizer.update(partialTick);

        Window window = this.minecraft.getWindow();
        int scaleHeight = (int) (120 * window.getGuiScale());
        Matrix4f projectionMatrixCopy = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting vertexSortingCopy = RenderSystem.getVertexSorting();

        RenderSystem.setProjectionMatrix(new Matrix4f().perspective(70.341F, window.getWidth() / (float) scaleHeight, 0.05F, 100.0F), VertexSorting.DISTANCE_TO_ORIGIN);
        RenderSystem.viewport(0, window.getHeight() - scaleHeight, window.getWidth(), scaleHeight);

        PoseStack model = RenderSystem.getModelViewStack();
        model.translate(-0.05F, 1.0F, 1987.0F);
        model.scale(1.59F, 1.59F, 1.59F);

        BakedModel stone = this.minecraft.getItemRenderer().getItemModelShaper().getItemModel(Blocks.STONE.asItem());

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);

        for (int pass = 0; pass < 3; pass++)
        {
            model.pushPose();

            if (pass == 0)
            {
                RenderSystem.clear(256, Minecraft.ON_OSX);
                model.translate(0.0F, -0.4F, 0.0F);
                model.scale(0.98F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            }

            if (pass == 1)
            {
                RenderSystem.disableBlend();
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            if (pass == 2)
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(768, 1);
            }

            model.scale(1.0F, -1.0F, 1.0F);
            model.mulPose(Axis.XP.rotationDegrees(15.0F));
            model.scale(0.89F, 1.0F, 0.4F);
            model.translate((float) (-MINECRAFT[0].length()) * 0.5F, (float) (-MINECRAFT.length) * 0.5F, 0.0F);

            if (pass == 0)
            {
                RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
                RenderSystem.setShaderTexture(0, TextureLocation.BLOCK_SHADOW);
            }
            else
            {
                RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            }

            for (int y = 0; y < MINECRAFT.length; y++)
            {
                for (int x = 0; x < MINECRAFT[y].length(); x++)
                {
                    if (MINECRAFT[y].charAt(x) == ' ')
                        continue;

                    model.pushPose();

                    float z = logoEffects[x][y].position;
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (pass == 0)
                    {
                        scale = z * 0.04F + 1.0F;
                        alpha = 1.0F / scale;
                        z = 0.0F;
                    }

                    model.translate(x, y, z);
                    model.scale(scale, scale, scale);
                    renderBlock(model, stone, pass, alpha);
                    model.popPose();
                }
            }

            model.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(projectionMatrixCopy, vertexSortingCopy);
        RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());
        model.setIdentity();
        model.translate(0, 0, -2000);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
    }

    /**
     * Get a packed RGBA integer.
     * @param red R
     * @param green G
     * @param blue B
     * @param alpha A
     * @return Packed RGBA integer.
     */
    private int getColorFromRGBA(float red, float green, float blue, float alpha)
    {
        return (int) (alpha * 255.0F) << 24 | (int) (red * 255.0F) << 16 | (int) (green * 255.0F) << 8 | (int) (blue * 255.0F);
    }

    /**
     * Get a grayscale packed RGBA integer from a brightness and alpha value.
     * @param brightness Brightness
     * @param alpha Transparency
     * @return A packed grayscale RGBA integer.
     */
    private int getColorFromBrightness(float brightness, float alpha)
    {
        return this.getColorFromRGBA(brightness, brightness, brightness, alpha);
    }

    /**
     * Quad rendering instructions that allow for transparency.
     * @param modelPose Model position matrix.
     * @param builder Buffer builder instance.
     * @param quad A baked quad.
     * @param brightness The brightness of the quad.
     * @param alpha The transparency of the quad.
     */
    private void renderQuad(PoseStack.Pose modelPose, BufferBuilder builder, BakedQuad quad, float brightness, float alpha)
    {
        int combinedLight = this.getColorFromBrightness(brightness, alpha);
        int[] vertices = quad.getVertices();
        Vec3i vec = quad.getDirection().getNormal();
        Matrix4f matrix = modelPose.pose();
        Vector3f vec3f = modelPose.normal().transform(new Vector3f(vec.getX(), vec.getY(), vec.getZ()));

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

                Vector4f vec4f = matrix.transform(new Vector4f(x, y, z, 1.0F));
                builder.vertex(vec4f.x(), vec4f.y(), vec4f.z(), 1.0F, 1.0F, 1.0F, alpha, byteBuffer.getFloat(16), byteBuffer.getFloat(20), OverlayTexture.NO_OVERLAY, combinedLight, vec3f.x(), vec3f.y(), vec3f.z());
            }
        }
    }

    /**
     * Render a block to the classic title screen.
     * @param modelView Model view matrix.
     * @param stone A stone block model.
     * @param pass The rendering pass index.
     * @param alpha A transparency value.
     */
    private void renderBlock(PoseStack modelView, BakedModel stone, int pass, float alpha)
    {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

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

            int color = this.getColorFromBrightness(brightness, alpha);

            for (BakedQuad quad : stone.getQuads(null, direction, RandomSource.create()))
            {
                if (pass == 0)
                    renderQuad(modelView.last(), builder, quad, brightness, alpha);
                else
                    builder.putBulkData(modelView.last(), quad, brightness, brightness, brightness, color, OverlayTexture.NO_OVERLAY);
            }
        }

        tesselator.end();
    }

    /* Logo Effect Randomizer */

    /**
     * This class tracks individual blocks for the falling animation.
     * Updates of position values are handled by the screen renderer.
     */

    private static class LogoEffectRandomizer
    {
        /* Fields */

        public float position;
        public float speed;

        /* Constructor */

        /**
         * Create a new logo effect randomizer instance.
         * @param x The starting x-position.
         * @param y The starting y-position.
         */
        public LogoEffectRandomizer(int x, int y)
        {
            this.position = (10 + y) + RandomSource.create().nextFloat() * 32.0F + x;
        }

        /**
         * Update the position of this randomizer instance.
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
