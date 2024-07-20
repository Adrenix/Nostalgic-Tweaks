package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_minecart;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin is only applied to the client.
 */
@Mixin(Entity.class)
public abstract class EntityMixin
{
    /* Unique */

    @Unique private float nt$riderXRotDelta;
    @Unique private float nt$riderYRotDelta;

    /* Shadows */

    @Shadow
    public abstract @Nullable Entity getVehicle();

    @Shadow
    public abstract void setXRot(float xRot);

    @Shadow
    public abstract void setYRot(float yRot);

    @Shadow
    public abstract float getXRot();

    @Shadow
    public abstract float getYRot();

    /* Injections */

    /**
     * Rotates the rider's body to follow the direction of the vehicle.
     */
    @Inject(
        method = "rideTick",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;positionRider(Lnet/minecraft/world/entity/Entity;)V"
        )
    )
    private void nt_mechanics_minecart$onRideTick(CallbackInfo callback)
    {
        if (NostalgicTweaks.isServer() || !GameplayTweak.CART_RIDER_TURNING.get())
            return;

        if (NetUtil.isMultiplayer() && !NostalgicTweaks.isNetworkVerified() && GameplayTweak.CART_SAFE_RIDER_TURNING.get())
            return;

        Entity vehicle = this.getVehicle();
        Entity self = (Entity) (Object) this;

        if (vehicle == null || vehicle.getControllingPassenger() == self)
            return;

        if (vehicle instanceof LivingEntity living)
            this.nt$riderYRotDelta = this.nt$riderYRotDelta + living.getVisualRotationYInDegrees() - living.yBodyRotO;
        else
            this.nt$riderYRotDelta = this.nt$riderYRotDelta + vehicle.getYRot() - vehicle.yRotO;

        this.nt$riderXRotDelta = this.nt$riderXRotDelta + vehicle.getXRot() - vehicle.xRotO;

        this.nt$riderXRotDelta = MathUtil.normalizeInRange(this.nt$riderXRotDelta, -180.0F, 180.0F);
        this.nt$riderYRotDelta = MathUtil.normalizeInRange(this.nt$riderYRotDelta, -180.0F, 180.0F);

        float xRotFinal = Mth.clamp(this.nt$riderXRotDelta * 0.5F, -10.0F, 10.0F);
        float yRotFinal = Mth.clamp(this.nt$riderYRotDelta * 0.5F, -10.0F, 10.0F);

        this.nt$riderXRotDelta -= xRotFinal;
        this.nt$riderYRotDelta -= yRotFinal;

        this.setXRot(this.getXRot() + xRotFinal);
        this.setYRot(this.getYRot() + yRotFinal);
    }

    /**
     * Resets the rider's rotation deltas when the rider starts riding a vehicle.
     */
    @Inject(
        method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
        at = @At("HEAD")
    )
    private void nt_mechanics_minecart$onStartRiding(CallbackInfoReturnable<Boolean> callback)
    {
        this.nt$riderXRotDelta = 0.0F;
        this.nt$riderYRotDelta = 0.0F;
    }
}
