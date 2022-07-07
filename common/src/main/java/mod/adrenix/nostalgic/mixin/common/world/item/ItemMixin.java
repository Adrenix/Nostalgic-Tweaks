package mod.adrenix.nostalgic.mixin.common.world.item;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin
{
    /* Shadows */

    @Shadow @Final private @Nullable FoodProperties foodProperties;

    /* Injections */

    /**
     * Changes food stack size to be 1 (or 8 if a cookie).
     * Controlled by the old food stacking tweak.
     */
    @Inject(method = "getMaxStackSize", at = @At("TAIL"), cancellable = true)
    private void NT$onGetMaxStackSize(CallbackInfoReturnable<Integer> callback)
    {
        Item item = ((Item) (Object) this);

        if (!ModConfig.Gameplay.oldFoodStacking() || this.foodProperties == null || item.equals(Items.ROTTEN_FLESH))
            return;

        if (item.equals(Items.COOKIE))
            callback.setReturnValue(8);
        else
            callback.setReturnValue(1);
    }

    /**
     * Simulates the old instant eating mechanics.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void NT$onGetUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantEat())
            callback.setReturnValue(1);
    }
}
