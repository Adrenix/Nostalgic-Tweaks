package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin
{
    /**
     * Allows minecarts that connect with each other to push each other away at maximum speed. Controlled by the old
     * minecart boosting mechanic tweak.
     */
    @ModifyArg(
        method = "push",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;abs(D)D"
        )
    )
    private double NT$onPreventPushCheck(double vanilla)
    {
        return ModConfig.Gameplay.cartBoosting() ? 1.0D : vanilla;
    }

    /**
     * Keeps cart moving at its current momentum if old minecart boosting tweak is enabled.
     */
    @Redirect(
        method = "moveAlongTrack",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D"
        )
    )
    private double NT$onCalculateDeltaMovement(double a, double b)
    {
        return ModConfig.Gameplay.cartBoosting() ? b : Math.min(a, b);
    }

    /**
     * Changes the momentum of carts going over powered rails if the old minecart boosting tweak is enabled.
     */
    @ModifyConstant(
        method = "moveAlongTrack",
        constant = @Constant(doubleValue = 0.06D)
    )
    private double NT$onPoweredRailMovement(double vanilla)
    {
        return ModConfig.Gameplay.cartBoosting() ? 0.04D : vanilla;
    }
}
