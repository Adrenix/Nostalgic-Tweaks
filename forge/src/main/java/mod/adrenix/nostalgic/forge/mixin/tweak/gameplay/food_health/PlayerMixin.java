package mod.adrenix.nostalgic.forge.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    /**
     * Prevents food data from receiving update.
     */
    @WrapWithCondition(
        method = "eat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V"
        )
    )
    private boolean nt_neoforge_food_health$shouldPlayerEat(FoodData foodData, Item item, ItemStack itemStack, LivingEntity livingEntity)
    {
        return !GameplayTweak.DISABLE_HUNGER.get();
    }
}
