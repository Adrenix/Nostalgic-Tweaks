package mod.adrenix.nostalgic.mixin.common.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(FoodData.class)
public abstract class FoodDataMixin
{
    /* Shadows & Uniques */

    @Shadow
    public abstract void eat(int foodLevelModifier, float saturationLevelModifier);

    @Unique
    private Player NT$player = null;

    /* Injections */

    /**
     * Caches the player associated with this food data instance after the first tick. Also prevents the ticking logic
     * for food data.
     *
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void NT$onTick(Player player, CallbackInfo callback)
    {
        if (this.NT$player == null || this.NT$player != player)
            this.NT$player = player;

        if (ModConfig.Gameplay.disableHunger())
            callback.cancel();
    }

    /**
     * Instead of updating the food level and saturation level, immediately heal the player.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "eat(IF)V", at = @At("HEAD"), cancellable = true)
    private void NT$onEat(int foodLevelModifier, float saturationLevelModifier, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHunger() && this.NT$player != null)
        {
            this.NT$player.heal(foodLevelModifier);
            callback.cancel();
        }
    }

    /**
     * Updates a player's health or saturation level based on the given item and item stack.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void NT$onEatItem(Item item, ItemStack itemStack, CallbackInfo callback)
    {
        FoodProperties properties = item.getFoodProperties();
        boolean isFood = item.isEdible() && properties != null;

        if (ModConfig.Gameplay.disableHunger() && isFood)
        {
            Map.Entry<String, Integer> entry = ModConfig.Gameplay.getFoodHealth().getEntryFromItem(item);
            int nutrition = entry != null ? entry.getValue() : properties.getNutrition();

            this.eat(nutrition, properties.getSaturationModifier());
            callback.cancel();
        }
    }

    /**
     * Overrides the food level.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
    private void NT$onGetFoodLevel(CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.setReturnValue(20);
    }

    /**
     * Overrides the last food level.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "getLastFoodLevel", at = @At("HEAD"), cancellable = true)
    private void NT$onGetLastFoodLevel(CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.setReturnValue(20);
    }

    /**
     * Changes the food needed logic to only be true when the player's health is less than the max health.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "needsFood", at = @At("HEAD"), cancellable = true)
    private void NT$onNeedsFood(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.disableHunger() && this.NT$player != null)
            callback.setReturnValue(this.NT$player.getHealth() < this.NT$player.getMaxHealth());
    }

    /**
     * Prevents the addition of exhaustion.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void NT$onAddExhaustion(float exhaustion, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.cancel();
    }

    /**
     * Changes the amount of exhaustion the player has.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "getExhaustionLevel", at = @At("HEAD"), cancellable = true)
    private void NT$onGetExhaustionLevel(CallbackInfoReturnable<Float> callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.setReturnValue(0.0F);
    }

    /**
     * Changes the amount of saturation the player has.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "getSaturationLevel", at = @At("HEAD"), cancellable = true)
    private void NT$onGetSaturationLevel(CallbackInfoReturnable<Float> callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.setReturnValue(20.0F);
    }

    /**
     * Prevents the ability to set the player's food level.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "setFoodLevel", at = @At("HEAD"), cancellable = true)
    private void NT$onSetFoodLevel(int foodLevel, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.cancel();
    }

    /**
     * Prevents the ability to set the player's saturation.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "setSaturation", at = @At("HEAD"), cancellable = true)
    private void NT$onSetSaturation(float saturationLevel, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.cancel();
    }

    /**
     * Prevents the ability to set the player's exhaustion.
     * Controlled by the disable hunger tweak.
     */
    @Inject(method = "setExhaustion", at = @At("HEAD"), cancellable = true)
    private void NT$onSetExhaustion(float exhaustionLevel, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHunger())
            callback.cancel();
    }
}
