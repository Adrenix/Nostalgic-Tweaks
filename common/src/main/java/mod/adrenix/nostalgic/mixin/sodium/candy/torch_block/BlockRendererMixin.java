package mod.adrenix.nostalgic.mixin.sodium.candy.torch_block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import mod.adrenix.nostalgic.helper.candy.block.TorchHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin
{
    @Unique private static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();
    @Unique private static final RenderMaterial STANDARD_MATERIAL = RENDERER.materialFinder().shadeMode(ShadeMode.VANILLA).find();
    @Unique private static final RenderMaterial NO_AO_MATERIAL = RENDERER.materialFinder().shadeMode(ShadeMode.VANILLA).ambientOcclusion(TriState.FALSE).find();

    /**
     * Removes the bottom face quads for a wall torch model.
     */
    @WrapOperation(
            method = "renderModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V"
            )
    )
    private void nt_sodium_torch_block$wrapEmitBlockQuads(FabricBakedModel model, BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSource, RenderContext renderContext, Operation<Void> emitBlockQuads)
    {
        if (TorchHelper.isLikeTorch(blockState))
        {
            if (!TorchHelper.isSheared(blockState))
            {
                emitBlockQuads.call(model, blockAndTintGetter, blockState, blockPos, randomSource, renderContext);
                return;
            }

            final BakedModel vanillaModel = (BakedModel) model;
            final RandomSource random = randomSource.get();
            final QuadEmitter emitter = renderContext.getEmitter();
            final RenderMaterial defaultMaterial = vanillaModel.useAmbientOcclusion() ? STANDARD_MATERIAL : NO_AO_MATERIAL;

            boolean isBottomDisabled = CandyTweak.OLD_TORCH_BOTTOM.get();

            Matrix4f matrix4f = new Matrix4f();
            TorchHelper.applyShear(matrix4f, blockState);

            for (BakedQuad quad : TorchHelper.getModel(blockState).getQuads(blockState, null, random))
            {
                if (isBottomDisabled && quad.getDirection() == Direction.DOWN)
                {
                    continue;
                }

                emitter.fromVanilla(quad, defaultMaterial, null);

                if (emitter instanceof MutableQuadViewImpl mEmitter)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        float x = mEmitter.x(i);
                        float y = mEmitter.y(i);
                        float z = mEmitter.z(i);

                        Vector3f transVec = matrix4f.transformPosition(x, y, z, new Vector3f());
                        mEmitter.pos(i, transVec.x(), transVec.y(), transVec.z());
                    }
                }

                emitter.emit();
            }
        }
        else
            emitBlockQuads.call(model, blockAndTintGetter, blockState, blockPos, randomSource, renderContext);
    }
}