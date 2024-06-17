package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_minecart;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin
{
    /**
     * Allows minecarts that connect with each other to push each other away at maximum speed.
     */
    @ModifyArg(
        method = "push",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;abs(D)D"
        )
    )
    private double nt_mechanics_minecart$modifyPushCheck(double delta)
    {
        return GameplayTweak.CART_BOOSTING.get() ? 1.0D : delta;
    }

    /**
     * Keeps minecarts moving at their current momentum.
     */
    @WrapOperation(
        method = "moveAlongTrack",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D"
        )
    )
    private double nt_mechanics_minecart$modifyDeltaMovement(double a, double b, Operation<Double> operation)
    {
        return GameplayTweak.CART_BOOSTING.get() ? b : operation.call(a, b);
    }

    /**
     * Changes the momentum of carts going over powered rails.
     */
    @ModifyExpressionValue(
        method = "moveAlongTrack",
        at = @At(
            value = "CONSTANT",
            args = "doubleValue=0.06D"
        )
    )
    private double nt_mechanics_minecart$modifyPoweredRailDelta(double delta)
    {
        return GameplayTweak.CART_BOOSTING.get() ? 0.04D : delta;
    }
}
