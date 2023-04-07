package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin
{
    /**
     * Allows minecarts that connect with each other to push each other away at maximum speed.
     * Controlled by the old minecraft boosting mechanic tweak.
     */
    @ModifyConstant(method = "push", constant = @Constant(doubleValue = 0.8F))
    private double NT$onPreventPushCheck(double vanilla)
    {
        return ModConfig.Gameplay.cartBoosting() ? -Double.MAX_VALUE : vanilla;
    }
}
