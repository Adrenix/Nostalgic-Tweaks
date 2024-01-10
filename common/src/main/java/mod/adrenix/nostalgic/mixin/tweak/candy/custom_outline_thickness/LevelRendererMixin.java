package mod.adrenix.nostalgic.mixin.tweak.candy.custom_outline_thickness;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Injection Fields */

    @Nullable @Unique private BufferBuilder NT$outlineBuffer;

    /* Injection Methods */

    /**
     * The hit outline needs a different vertex consumer since the game's buffered lines render type shader doesn't
     * properly support setting the line width. Since this is an immediate draw, the render state is also changed.
     */
    @Inject(
        method = "renderLevel",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
        )
    )
    private void NT$createHitOutlineBuffer(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        float thickness = CandyTweak.CUSTOM_OUTLINE_THICKNESS.get();

        if (Objects.equals(CandyTweak.CUSTOM_OUTLINE_THICKNESS.getDisabled(), thickness))
            return;

        this.NT$outlineBuffer = RenderUtil.getAndBeginLine(thickness);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
    }

    /**
     * Change the hit outline vertex consumer to our consumer since it supports line width changes correctly.
     */
    @ModifyArg(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
        )
    )
    private VertexConsumer NT$setHitOutlineBuffer(VertexConsumer vanilla)
    {
        boolean isDisabled = Objects.equals(CandyTweak.CUSTOM_OUTLINE_THICKNESS.get(), CandyTweak.CUSTOM_OUTLINE_THICKNESS.getDisabled());

        if (isDisabled || this.NT$outlineBuffer == null)
            return vanilla;

        return this.NT$outlineBuffer;
    }

    /**
     * Restore the render state back to what it was before the outline draw.
     */
    @Inject(
        method = "renderLevel",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
        )
    )
    private void NT$clearRenderHitOutlineBuffer(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        if (this.NT$outlineBuffer == null || !this.NT$outlineBuffer.building())
            return;

        RenderUtil.endLine(this.NT$outlineBuffer);
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }
}
