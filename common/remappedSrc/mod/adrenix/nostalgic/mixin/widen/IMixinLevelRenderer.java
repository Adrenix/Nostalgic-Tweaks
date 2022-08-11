package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface IMixinLevelRenderer
{
    @SuppressWarnings("unused")
    @Invoker("renderShape")
    static void NT$invokeRenderShape(MatrixStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha)
    {
        throw new AssertionError();
    }
}
