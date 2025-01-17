package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_boat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import mod.adrenix.nostalgic.helper.gameplay.BoatHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin extends VehicleEntity
{
    /* Fake Constructor */

    private AbstractBoatMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Shadows */

    @Shadow private AbstractBoat.Status status;
    @Shadow private float invFriction;

    /* Injections */

    /**
     * Prevents a boat from ejecting passengers if the "out of control ticks" exceeds 60 ticks.
     */
    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;ejectPassengers()V"
        )
    )
    private boolean nt_mechanics_boat$shouldEjectPassengersWhenOutOfControl(AbstractBoat boat)
    {
        return !GameplayTweak.OLD_BOAT_WATER_LIFT.get();
    }

    /**
     * Applies frictional changes to the boat's flotation movement to assist with old water lifts.
     */
    @Inject(
        method = "floatBoat",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private void nt_mechanics_boat$setWaterLiftFriction(CallbackInfo callback)
    {
        if (GameplayTweak.OLD_BOAT_WATER_LIFT.get() && BoatHelper.isWaterLift(this.status))
            this.invFriction = 0.9F;
    }

    /**
     * Changes the boat's gravity so the boat can lift upwards.
     */
    @ModifyArg(
        index = 1,
        method = "floatBoat",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;setDeltaMovement(DDD)V"
        )
    )
    private double nt_mechanics_boat$modifyGravity(double y, @Share("deltaY") LocalDoubleRef deltaY)
    {
        deltaY.set(GameplayTweak.OLD_BOAT_WATER_LIFT.get() ? BoatHelper.getGravityAmount((AbstractBoat) (Object) this, this.status) : y);

        return deltaY.get();
    }

    /**
     * Changes the boat's vertical movement if the boat meets the criteria for the old water lift mechanic.
     */
    @Inject(
        method = "floatBoat",
        at = @At("RETURN")
    )
    private void nt_mechanics_boat$setWaterLiftVerticalMovement(CallbackInfo callback, @Share("deltaY") LocalDoubleRef deltaY)
    {
        if (GameplayTweak.OLD_BOAT_WATER_LIFT.get() && BoatHelper.isWaterLift(this.status))
        {
            double dx = this.getDeltaMovement().x;
            double dy = BoatHelper.getLiftAmount(deltaY.get());
            double dz = this.getDeltaMovement().z;

            this.setDeltaMovement(dx, dy, dz);
        }
    }

    /**
     * Adds splash particles to a boat based on its speed.
     */
    @Inject(
        method = "floatBoat",
        at = @At("RETURN")
    )
    private void nt_mechanics_boat$addSpeedParticles(CallbackInfo callback)
    {
        if (this.level().isClientSide() && CandyTweak.OLD_BOAT_MOVEMENT_PARTICLES.get())
            BoatHelper.applyParticles((AbstractBoat) (Object) this);
    }

    /**
     * Changes the items dropped by boats when they are destroyed.
     */
    @ModifyReturnValue(
        method = "getDropItem",
        at = @At("RETURN")
    )
    private Item nt_mechanics_boat$modifyDroppedItems(Item dropItem)
    {
        if (!GameplayTweak.OLD_BOAT_DROPS.get())
            return dropItem;

        String itemKey = ItemUtil.getResourceKey(dropItem);
        String itemLocation = TextUtil.extract(itemKey, "(.+):", 1);
        String boatType = TextUtil.extract(itemKey, ":(.+)_", 1) + "_planks";

        Item planks = BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath(itemLocation, boatType));

        if (this.level() instanceof ServerLevel level && planks != Items.AIR)
        {
            this.spawnAtLocation(level, new ItemStack(planks));
            this.spawnAtLocation(level, new ItemStack(planks));
            this.spawnAtLocation(level, new ItemStack(Items.STICK));

            return ItemStack.EMPTY.getItem();
        }

        return dropItem;
    }
}
