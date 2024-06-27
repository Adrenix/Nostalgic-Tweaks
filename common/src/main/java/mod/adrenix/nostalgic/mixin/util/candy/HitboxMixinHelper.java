package mod.adrenix.nostalgic.mixin.util.candy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.ColorType;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

import java.util.concurrent.TimeUnit;

/**
 * This utility class is used only by the client.
 */
public abstract class HitboxMixinHelper
{
    /* Fields */

    /**
     * Tracks if the level renderer is rendering a hitbox outline.
     */
    public static final FlagHolder CUSTOM_HITBOX_OUTLINE = FlagHolder.off();

    /**
     * Global animation instance for pulsating a hitbox overlay color.
     */
    public static final Animation PULSATE_ANIMATION = Animate.linear(1L, TimeUnit.SECONDS);

    /**
     * An overlay render type used to buffer the hitbox overlay in fabulous mode.
     */
    public static final RenderType OVERLAY_RENDER_TYPE = RenderType.create("nt_hitbox_overlay", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder()
        .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
        .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
        .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
        .setCullState(RenderStateShard.NO_CULL)
        .createCompositeState(false));

    /* Methods */

    /**
     * Get the correct hitbox voxel shape for the given block.
     *
     * @param original The original {@link VoxelShape} the block uses.
     * @param block    The {@link Block} to check if it uses an old outline.
     * @return A new {@link VoxelShape} instance or the given original.
     */
    public static VoxelShape getShape(VoxelShape original, Block block)
    {
        if (CandyTweak.OLD_BLOCK_OUTLINES.get().containsBlock(block) || ChestMixinHelper.isOld(block))
            return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

        return original;
    }

    /**
     * Build the vertices for a hitbox overlay.
     *
     * @param matrix The position {@link Matrix4f}.
     * @param shape  The {@link VoxelShape} of the hitbox.
     * @param rx     The x-coordinate relative from the camera to the block.
     * @param ry     The y-coordinate relative from the camera to the block.
     * @param rz     The z-coordinate relative from the camera to the block.
     */
    public static void renderOverlay(Matrix4f matrix, VoxelShape shape, double rx, double ry, double rz)
    {
        float pulseMin = CandyTweak.MINIMUM_PULSATION_TRANSPARENCY.get();
        float pulseMax = CandyTweak.MAXIMUM_PULSATION_TRANSPARENCY.get();
        float minAlpha = Math.min(pulseMin, pulseMax);
        float maxAlpha = Math.max(pulseMin, pulseMax);

        PULSATE_ANIMATION.animateWith(CandyTweak.PULSATE_OVERLAY_ANIMATION.get().getAnimation());
        PULSATE_ANIMATION.setDuration((long) (CandyTweak.BLOCK_OVERLAY_SPEED.get() * 1000.0F), TimeUnit.MILLISECONDS);
        PULSATE_ANIMATION.playOrRewind();

        NullableHolder<VertexConsumer> vertexHolder = NullableHolder.empty();
        IntegerHolder top = IntegerHolder.create(HexUtil.parseInt(CandyTweak.BLOCK_OVERLAY_COLOR.get()));
        IntegerHolder bottom = IntegerHolder.create(top.get().intValue());
        BufferBuilder builder = null;

        if (Minecraft.useShaderTransparency())
            vertexHolder.set(Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(OVERLAY_RENDER_TYPE));
        else
        {
            builder = RenderUtil.getAndBeginFill();
            vertexHolder.set(builder);
        }

        if (CandyTweak.BLOCK_OVERLAY_COLOR_TYPE.get() == ColorType.GRADIENT)
        {
            top.set(HexUtil.parseInt(CandyTweak.BLOCK_OVERLAY_GRADIENT_TOP.get()));
            bottom.set(HexUtil.parseInt(CandyTweak.BLOCK_OVERLAY_GRADIENT_BOTTOM.get()));
        }

        if (CandyTweak.PULSATE_BLOCK_OVERLAY.get())
        {
            double alpha = Mth.lerp(PULSATE_ANIMATION.getValue(), minAlpha, maxAlpha);

            top.set(new Color(top.get()).fromAlpha(alpha).get());
            bottom.set(new Color(bottom.get()).fromAlpha(alpha).get());
        }

        shape.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
            VertexConsumer buffer = vertexHolder.getOrThrow();
            int argbTop = top.get();
            int argbBottom = bottom.get();

            float rx0 = (float) (x0 + rx);
            float ry0 = (float) (y0 + ry);
            float rz0 = (float) (z0 + rz);

            float rx1 = (float) (x1 + rx);
            float ry1 = (float) (y1 + ry);
            float rz1 = (float) (z1 + rz);

            float dx = (float) (x1 - x0);
            float dy = (float) (y1 - y0);
            float dz = (float) (z1 - z0);

            // South
            buffer.addVertex(matrix, rx0, ry1, rz0).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry1, rz1 - dz).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry0, rz1 - dz).setColor(argbBottom);
            buffer.addVertex(matrix, rx0, ry0, rz0).setColor(argbBottom);

            // North
            buffer.addVertex(matrix, rx0, ry1, rz0 + dz).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry1, rz1).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry0, rz1).setColor(argbBottom);
            buffer.addVertex(matrix, rx0, ry0, rz0 + dz).setColor(argbBottom);

            // East
            buffer.addVertex(matrix, rx0, ry1, rz0).setColor(argbTop);
            buffer.addVertex(matrix, rx1 - dx, ry1, rz1).setColor(argbTop);
            buffer.addVertex(matrix, rx1 - dx, ry0, rz1).setColor(argbBottom);
            buffer.addVertex(matrix, rx0, ry0, rz0).setColor(argbBottom);

            // West
            buffer.addVertex(matrix, rx0 + dx, ry1, rz0).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry1, rz1).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry0, rz1).setColor(argbBottom);
            buffer.addVertex(matrix, rx0 + dx, ry0, rz0).setColor(argbBottom);

            // Top
            buffer.addVertex(matrix, rx0, ry1, rz0).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry1, rz1 - dz).setColor(argbTop);
            buffer.addVertex(matrix, rx1, ry0 + dy, rz1).setColor(argbTop);
            buffer.addVertex(matrix, rx0, ry0 + dy, rz0 + dz).setColor(argbTop);

            // Bottom
            buffer.addVertex(matrix, rx0, ry1 - dy, rz0 + dz).setColor(argbBottom);
            buffer.addVertex(matrix, rx1, ry1 - dy, rz1).setColor(argbBottom);
            buffer.addVertex(matrix, rx1, ry0, rz1 - dz).setColor(argbBottom);
            buffer.addVertex(matrix, rx0, ry0, rz0).setColor(argbBottom);
        });

        if (Minecraft.useShaderTransparency())
        {
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(OVERLAY_RENDER_TYPE);
            Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        }

        if (builder == null)
            return;

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        if (!Minecraft.useShaderTransparency())
            RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderStateShard.VIEW_OFFSET_Z_LAYERING.setupRenderState();

        RenderUtil.endFill(builder);

        if (!Minecraft.useShaderTransparency())
            RenderSystem.depthMask(true);

        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderStateShard.VIEW_OFFSET_Z_LAYERING.clearRenderState();
    }
}