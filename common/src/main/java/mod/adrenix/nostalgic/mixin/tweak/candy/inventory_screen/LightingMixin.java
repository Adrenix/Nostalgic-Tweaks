package mod.adrenix.nostalgic.mixin.tweak.candy.inventory_screen;

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
     * Brings back the old inverted inventory player lighting.
     */
    @WrapOperation(
        method = "setupForEntityInInventory",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderLights(Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V"
        )
    )
    private static void nt_inventory_screen$wrapSetupForEntityInInventory(Vector3f startDirection, Vector3f endDirection, Operation<Void> operation)
    {
        if (CandyTweak.INVERTED_PLAYER_LIGHTING.get())
            RenderSystem.setShaderLights(new Vector3f(-2.0F, -3.0F, -1.0F), new Vector3f(-0.8F, -5.0F, -1.5F));
        else
            operation.call(startDirection, endDirection);
    }
}
