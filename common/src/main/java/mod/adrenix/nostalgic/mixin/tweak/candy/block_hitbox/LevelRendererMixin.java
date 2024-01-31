package mod.adrenix.nostalgic.mixin.tweak.candy.block_hitbox;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.mixin.util.HitboxMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.RenderOrder;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Injected Fields */

    @Shadow @Nullable private ClientLevel level;
    @Unique @Nullable private Runnable nt$renderOverlay;

    /* Injected Methods */

    /**
     * Applies changes to the voxel shape, hitbox color, and/or prepares an overlay buffer used by the hitbox outline
     * renderer. If a block overlay is needed, then it is rendered here as well if the game is using the "fabulous"
     * video setting.
     */
    @WrapOperation(
        method = "renderHitOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"
        )
    )
    private void nt_block_hitbox$wrapRenderShape(PoseStack poseStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double x, double y, double z, float red, float green, float blue, float alpha, Operation<Void> renderShape, PoseStack arg1, VertexConsumer arg2, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state)
    {
        boolean isEnabled = ModTweak.ENABLED.get();
        VoxelShape hitbox = HitboxMixinHelper.getShape(voxelShape, state.getBlock());
        BufferBuilder buffer = null;

        float[] rgba = HexUtil.parseFloatRGBA(CandyTweak.BLOCK_OUTLINE_COLOR.get());
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];

        if (isEnabled)
        {
            buffer = RenderUtil.getAndBeginLine(CandyTweak.BLOCK_OUTLINE_THICKNESS.get());

            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderStateShard.VIEW_OFFSET_Z_LAYERING.setupRenderState();
        }

        renderShape.call(poseStack, isEnabled ? buffer : vertexConsumer, hitbox, x, y, z, r, g, b, a);

        if (isEnabled)
        {
            RenderUtil.endLine(buffer);
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderStateShard.VIEW_OFFSET_Z_LAYERING.clearRenderState();
        }

        if (CandyTweak.OLD_BLOCK_OVERLAY.get() && this.level != null)
        {
            Matrix4f matrix = new Matrix4f(poseStack.last().pose());
            final double rx = (double) pos.getX() - camX;
            final double ry = (double) pos.getY() - camY;
            final double rz = (double) pos.getZ() - camZ;

            if (Minecraft.useShaderTransparency())
                HitboxMixinHelper.renderOverlay(matrix, hitbox, rx, ry, rz);
            else
                this.nt$renderOverlay = () -> HitboxMixinHelper.renderOverlay(matrix, hitbox, rx, ry, rz);
        }
    }

    /**
     * Renders the hitbox overlay before translucency, clouds, particles, and weather effects. This will let the overlay
     * be seen behind translucent blocks, but will not respect transparency depth correctly.
     */
    @Inject(
        method = "renderLevel",
        at = @At(
            ordinal = 0,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"
        )
    )
    private void nt_block_hitbox$renderOverlayFirst(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        if (this.nt$renderOverlay != null && CandyTweak.BLOCK_OVERLAY_RENDER_ORDER.get() == RenderOrder.FIRST)
            this.nt$renderOverlay.run();
    }

    /**
     * Renders the hitbox overlay after translucency, clouds, particles, and weather effects. This will prevent the
     * overlay from being seen behind translucent blocks, but the overlay will respect transparency depth correctly.
     */
    @Inject(
        method = "renderLevel",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V"
        )
    )
    private void nt_block_hitbox$renderOverlayLast(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        if (this.nt$renderOverlay != null && CandyTweak.BLOCK_OVERLAY_RENDER_ORDER.get() == RenderOrder.LAST)
            this.nt$renderOverlay.run();
    }

    /**
     * Ensures the memory reserved for the render overlay buffer is cleared whenever the render level method returns.
     */
    @Inject(
        method = "renderLevel",
        at = @At("RETURN")
    )
    private void nt_block_hitbox$clearRenderedOverlay(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        this.nt$renderOverlay = null;
    }
}
