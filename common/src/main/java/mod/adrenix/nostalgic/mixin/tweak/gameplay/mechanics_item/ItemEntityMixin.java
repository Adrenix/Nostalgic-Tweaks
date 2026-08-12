package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
    /**
     * Trick the physics logic into thinking the item never entered a fluid.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D"
        )
    )
    private double nt_mechanics_item$modifyGetFluidHeight(double fluidHeight)
    {
        if (GameplayTweak.OLD_ITEM_FLUID_PHYSICS.get())
            return 0.0D;

        return fluidHeight;
    }

    /**
     * Prevents impulse if the item entity entered a fluid.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;updateInWaterStateAndDoFluidPushing()Z"
        )
    )
    private boolean nt_mechanics_item$modifyInWaterStateAndDoFluidPushing(boolean shouldUpdateAndDoPushing)
    {
        if (GameplayTweak.OLD_ITEM_FLUID_PHYSICS.get())
            return false;

        return shouldUpdateAndDoPushing;
    }
}
