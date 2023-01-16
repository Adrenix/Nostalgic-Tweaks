package mod.adrenix.nostalgic.forge.mixin.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(FoodData.class)
public abstract class FoodDataForgeMixin
{
    /* Shadows */

    @Shadow public abstract void eat(Item item, ItemStack stack);

    /* Injections */

    /**
     * This method is unique to the Forge version of the FoodData class. It needs to be cancelled at the head so that
     * the custom food health values can be used. This is only cancelled if the disable hunger tweak is disabled.
     *
     * Controlled by the disable hunger tweak and custom food health tweak.
     */
    @Inject
    (
        method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private void NT$onEat(Item item, ItemStack itemStack, @Nullable LivingEntity entity, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHunger())
        {
            this.eat(item, itemStack);
            callback.cancel();
        }
    }
}
