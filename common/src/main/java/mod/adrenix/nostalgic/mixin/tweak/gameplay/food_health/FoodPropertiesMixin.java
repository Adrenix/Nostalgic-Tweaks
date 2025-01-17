package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public abstract class FoodPropertiesMixin
{
    /**
     * Restores the player's health after eating food.
     */
    @Inject(
        method = "onConsume",
        at = @At("HEAD")
    )
    private void nt_food_health$onConsumerFood(Level level, LivingEntity entity, ItemStack itemStack, Consumable consumable, CallbackInfo callback)
    {
        if (GameplayTweak.DISABLE_HUNGER.get() && ItemUtil.isEdible(itemStack))
        {
            FoodProperties food = itemStack.get(DataComponents.FOOD);
            int healAmount = NullableResult.getOrElse(food, 0, FoodProperties::nutrition);

            if (GameplayTweak.CUSTOM_FOOD_HEALTH.get().containsItem(itemStack))
                healAmount = GameplayTweak.CUSTOM_FOOD_HEALTH.get().valueFrom(itemStack);

            entity.heal(healAmount);
        }
    }

    /**
     * Prevents food data from receiving updates.
     */
    @WrapWithCondition(
        method = "onConsume",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"
        )
    )
    private boolean nt_food_health$shouldFoodDataUpdate(FoodData foodData, FoodProperties foodProperties)
    {
        return !GameplayTweak.DISABLE_HUNGER.get();
    }

    /**
     * Prevents the "burp" sound that plays after a player finishes eating an item.
     */
    @WrapWithCondition(
        method = "onConsume",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    private boolean nt_food_health$shouldPlayBurpSound(Level level, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        return !GameplayTweak.INSTANT_EAT.get();
    }
}
