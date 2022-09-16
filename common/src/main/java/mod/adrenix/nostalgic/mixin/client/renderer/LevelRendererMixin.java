package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.widen.LevelRendererAccessor;
import mod.adrenix.nostalgic.util.client.*;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Private Static Helpers */

    private static boolean isStarRunnableSaved = false;
    private static boolean isBlueRunnableSaved = false;

    /* Shadows */

    @Shadow protected abstract void createStars();
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private RenderBuffers renderBuffers;
    @Unique @Nullable private VertexBuffer blueBuffer;
    @Shadow @Nullable private ClientLevel level;

    /* Unique Injected Fields */

    @Unique
    private void NT$createBlueBuffer()
    {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        if (this.blueBuffer != null)
            this.blueBuffer.close();

        float height = switch(ModConfig.Candy.getBlueVoid())
        {
            case ALPHA -> -32.0F;
            case BETA, MODERN -> -48.0F;
        };

        this.blueBuffer = new VertexBuffer();
        BufferBuilder.RenderedBuffer renderedBuffer = WorldClientUtil.buildSkyDisc(builder, height);
        this.blueBuffer.bind();
        this.blueBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }

    /* Injections */

    /**
     * Used to instantiate the vertex buffer for the old blue void color.
     */
    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/client/renderer/RenderBuffers;)V", at = @At(value = "TAIL"))
    private void NT$onInitLevelRenderer(Minecraft minecraft, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers renderBuffers, CallbackInfo callback)
    {
        this.NT$createBlueBuffer();

        if (!isStarRunnableSaved)
        {
            isStarRunnableSaved = true;
            RunUtil.onSave.add(this::createStars);
        }

        if (!isBlueRunnableSaved)
        {
            isBlueRunnableSaved = true;
            RunUtil.onSave.add(this::NT$createBlueBuffer);
        }
    }

    /**
     * Caches the model view matrix and the projection matrix.
     * This is done so the stars and sky can be overlaid with the blue void correctly.
     */
    @Inject(method = "renderSky", at = @At(value = "HEAD"))
    private void NT$onCacheSkyPose(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        WorldClientUtil.blueModelView = poseStack.last().pose().copy();
        WorldClientUtil.blueProjection = projectionMatrix.copy();
    }

    /**
     * Brings back the old blue void color.
     * This buffer needs to be rendered after the sky buffer and before the dark buffer.
     *
     * Controlled by the old blue void color tweak.
     */
    @Inject
    (
        method = "renderSky",
        at = @At
        (
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
        )
    )
    private void NT$onDrawSkyBuffer(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        if (ModConfig.Candy.getBlueVoid() != TweakVersion.Generic.MODERN)
        {
            WorldClientUtil.setBlueVoidColor();
            ShaderInstance shader = RenderSystem.getShader();

            if (this.blueBuffer != null && shader != null)
            {
                this.blueBuffer.bind();
                this.blueBuffer.drawWithShader(WorldClientUtil.blueModelView, WorldClientUtil.blueProjection, shader);
                VertexBuffer.unbind();
            }
        }
    }

    /**
     * Allows the dark void to follow the camera's height.
     * Controlled by the old dynamic void height tweak.
     */
    @ModifyArg(method = "renderSky", index = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"))
    private double NT$onTranslateDarkSkyBuffer(double y)
    {
        if (!ModConfig.Candy.oldDarkVoidHeight())
            return y;
        y = 0.0D;

        return y - Math.max(this.minecraft.gameRenderer.getMainCamera().getPosition().y - 65.0D, 0.0D);
    }

    /**
     * Disables the change in sky color when the sun is rising or setting.
     * Controlled by the disabled sunrise/sunset color tweak.
     */
    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"))
    private float[] NT$onGetSunriseColor(DimensionSpecialEffects instance, float timeOfDay, float partialTicks)
    {
        return ModConfig.Candy.disableSunriseSunsetColor() ? null : instance.getSunriseColor(timeOfDay, partialTicks);
    }

    /**
     * Sets the transparency of the sunrise/sunset colors when void fog is rendering.
     */
    @Inject
    (
        method = "renderSky",
        at = @At
        (
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
        )
    )
    private void NT$onSetSunriseColor(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        FogUtil.Void.setCelestialTransparency();
    }

    /**
     * Sets the transparency of the sun/moon when void fog is rendering.
     */
    @Inject
    (
        method = "renderSky",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F")),
        at = @At
        (
            ordinal = 0,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
        )
    )
    private void NT$onSetSunMoonShading(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        FogUtil.Void.setCelestialTransparency();
    }

    /**
     * Disables the rendering of the dark void if the blue void is enabled and its respective override is enabled.
     * Controlled by both old blue void and old blue void override tweaks.
     */
    @Redirect
    (
        method = "renderSky",
        at = @At
        (
            ordinal = 2,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lcom/mojang/math/Matrix4f;Lcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"
        )
    )
    private void NT$onRenderDarkVoid(VertexBuffer instance, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shaderInstance)
    {
        TweakVersion.Generic voidState = ModConfig.Candy.getBlueVoid();
        boolean isBlueRendered = voidState == TweakVersion.Generic.ALPHA || voidState == TweakVersion.Generic.BETA;
        boolean isDarkOverride = ModConfig.Candy.oldBlueVoidOverride();

        if (!isBlueRendered || !isDarkOverride)
        {
            final float[] DARK_RGB = RenderSystem.getShaderColor();
            final float[] VOID_RGB = FogUtil.Void.getVoidRGB();

            FogUtil.Void.setVoidRGB(DARK_RGB[0], DARK_RGB[1], DARK_RGB[2]);

            if (FogUtil.Void.isRendering())
                RenderSystem.setShaderColor(VOID_RGB[0], VOID_RGB[1], VOID_RGB[2], DARK_RGB[3]);

            instance.drawWithShader(modelViewMatrix, projectionMatrix, shaderInstance);
        }
    }

    /**
     * Used to cache the current level pose stack which is needed for disabling diffused lighting.
     */
    @Inject(method = "renderLevel", at = @At(value = "HEAD"))
    private void NT$onStartLevelRendering(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        ItemClientUtil.levelPoseStack = poseStack.last();
        ItemClientUtil.levelBufferSource = this.renderBuffers.bufferSource();
    }

    /**
     * Change the rotation of the sunrise/sunset sky disc color on ZP by 90 degrees.
     * Controlled by the old at north sunrise tweak.
     */
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", ordinal = 2, target = "Lcom/mojang/math/Vector3f;rotationDegrees(F)Lcom/mojang/math/Quaternion;"))
    private float NT$onRenderSkyDiscColor(float vanilla)
    {
        return WorldClientUtil.getSunriseRotation(vanilla);
    }

    /**
     * Change the rotation of the sun/moon renderer on YP by 90 degrees.
     * Controlled by the old at north sunrise tweak.
     */
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", ordinal = 3, target = "Lcom/mojang/math/Vector3f;rotationDegrees(F)Lcom/mojang/math/Quaternion;"))
    private float NT$onRenderSun(float vanilla)
    {
        return WorldClientUtil.getSunriseRotation(vanilla);
    }

    /**
     * Renders the old big stars in the skybox.
     * Controlled by the old stars tweak.
     */

    @ModifyConstant(method = "drawStars", constant = @Constant(floatValue = 0.15F))
    private float NT$onDrawStarsWidth(float vanilla)
    {
        return switch(ModConfig.Candy.getStars()) { case ALPHA, BETA -> 0.25F; default -> 0.15F; };
    }

    @ModifyConstant(method = "drawStars", constant = @Constant(floatValue = 0.1F))
    private float NT$onDrawStarsHeight(float vanilla)
    {
        return switch(ModConfig.Candy.getStars()) { case ALPHA, BETA -> 0.25F; default -> 0.1F; };
    }

    /**
     * Brings back the old star brightness values.
     * Controlled by the old stars tweak.
     */
    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupNoFog()V"))
    private void NT$onSetupStarColor(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        TweakVersion.Generic stars = ModConfig.Candy.getStars();
        boolean isDimmed = stars.equals(TweakVersion.Generic.MODERN) || stars.equals(TweakVersion.Generic.BETA);

        if (this.level == null)
            return;

        float rain = 1.0F - this.level.getRainLevel(partialTick);
        float transparency = this.level.getStarBrightness(partialTick) * rain;
        float color = isDimmed ? transparency : transparency / 0.5F;

        FogUtil.Void.setStarAlpha(transparency);

        if (FogUtil.Void.isRendering())
            transparency = FogUtil.Void.getStarAlpha();

        RenderSystem.setShaderColor(color, color, color, transparency);
    }

    /**
     * Renders a full block hit outline for chests. This circumvents the need of changing the voxel shape of chest blocks.
     * If a user wishes to change the voxel shape for a true nostalgic experience, the server will have to facilitate
     * a change in the chest's voxel shape.
     *
     * Controlled by various old chest tweaks.
     */
    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void NT$onRenderHitOutline(PoseStack poseStack, VertexConsumer consumer, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state, CallbackInfo callback)
    {
        if (!BlockCommonUtil.isOldChest(state.getBlock()))
            return;

        LevelRendererAccessor.NT$renderShape
        (
            poseStack,
            consumer,
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            (double) pos.getX() - camX,
            (double) pos.getY() - camY,
            (double) pos.getZ() - camZ,
            0.0F,
            0.0F,
            0.0F,
            0.4F
        );

        callback.cancel();
    }

    /**
     * Drops the skylight brightness from water related blocks by 2 levels.
     * Controlled by the old water lighting tweak.
     */
    @Redirect
    (
        method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
        at = @At
        (
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockAndTintGetter;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"
        )
    )
    private static int NT$onGetLightColor(BlockAndTintGetter level, LightLayer layer, BlockPos pos)
    {
        if (ModConfig.Candy.oldWaterLighting() && BlockCommonUtil.isInWater(level, pos))
            return BlockCommonUtil.getWaterLightBlock(level.getBrightness(layer, pos));

        return level.getBrightness(layer, pos);
    }
}
