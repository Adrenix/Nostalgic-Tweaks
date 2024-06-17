package mod.adrenix.nostalgic.mixin.tweak.candy.block_lighting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Lighting.class)
public abstract class LightingMixin
{
    /**
     * Brings back the old inverted inventory block lighting.
     */
    @WrapOperation(
        method = "setupFor3DItems",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupGui3DDiffuseLighting(Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V"
        )
    )
    private static void nt_block_lighting$wrapSetupFor3DItems(Vector3f startDirection, Vector3f endDirection, Operation<Void> operation)
    {
        if (CandyTweak.INVERTED_BLOCK_LIGHTING.get())
            RenderSystem.setupGui3DDiffuseLighting(new Vector3f(0.0F, 2.0F, 1.0F), new Vector3f(-1.7F, 4.0F, 1.5F));
        else
            operation.call(startDirection, endDirection);
    }
}
