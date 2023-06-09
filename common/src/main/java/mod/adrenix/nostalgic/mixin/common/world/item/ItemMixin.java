package mod.adrenix.nostalgic.mixin.common.world.item;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.MaxSizeChanger;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Item.class)
public abstract class ItemMixin implements MaxSizeChanger
{
    /* Shadows & Uniques */

    @Shadow @Final private @Nullable FoodProperties foodProperties;
    @Shadow @Final private int maxStackSize;
    @Unique private int NT$maxStackSize;

    /* Max Size Implementation */

    @Override
    public float NT$getOriginalSize() { return this.NT$maxStackSize; }

    /* Injections */

    /**
     * Saves the original item properties so that it can be referenced later to ensure that no improper stack sizes
     * are returned.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void NT$onCreateItem(Item.Properties properties, CallbackInfo callback)
    {
        this.NT$maxStackSize = this.maxStackSize;
    }

    /**
     * Changes item stack sizes.
     * Controlled by custom item stacking tweaks.
     */
    @Inject(method = "getMaxStackSize", at = @At("TAIL"), cancellable = true)
    private void NT$onGetMaxStackSize(CallbackInfoReturnable<Integer> callback)
    {
        Item item = ((Item) (Object) this);

        boolean isFoodDisabled = !ModConfig.Gameplay.oldFoodStacking() && this.foodProperties != null;
        boolean isStackSizeLocked = this.NT$maxStackSize == 1;

        if (ItemServerUtil.isDroppingLoot || isFoodDisabled || isStackSizeLocked)
            return;

        Map.Entry<String, Integer> foodEntry = ModConfig.Gameplay.getFoodStacking().getEntryFromItem(item);
        Map.Entry<String, Integer> itemEntry = ModConfig.Gameplay.getItemStacking().getEntryFromItem(item);

        if (foodEntry != null)
            callback.setReturnValue(foodEntry.getValue());
        else if (itemEntry != null)
            callback.setReturnValue(itemEntry.getValue());
    }

    /**
     * Simulates the old instant eating mechanics.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void NT$onGetUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantEat() && stack.isEdible())
            callback.setReturnValue(1);
    }
}
