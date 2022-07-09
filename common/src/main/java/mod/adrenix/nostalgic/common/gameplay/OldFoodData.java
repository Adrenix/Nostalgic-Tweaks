package mod.adrenix.nostalgic.common.gameplay;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * This class overrides the vanilla food data system.
 * Since the class will be used by the server, keep vanilla client-only code out.
 */

public class OldFoodData extends FoodData
{
    private static final int MAX_FOOD_LEVEL = 20;
    private final FoodData vanilla;
    private final Player player;

    public OldFoodData(Player player)
    {
        this.player = player;
        this.vanilla = new FoodData();
    }

    /**
     * Prevent food stat updates - instead replenish player hearts.
     */
    public void eat(int foodLevelModifier, float saturationLevelModifier)
    {
        if (ModConfig.Gameplay.disableHunger())
            player.heal(foodLevelModifier);
        else
            this.vanilla.eat(foodLevelModifier, saturationLevelModifier);
    }

    public void eat(Item item, ItemStack stack)
    {
        if (ModConfig.Gameplay.disableHunger())
        {
            if (item.isEdible())
            {
                FoodProperties foodProperties = item.getFoodProperties();
                if (foodProperties != null)
                {
                    int nutrition = foodProperties.getNutrition();

                    boolean isRottenFlesh = item.equals(Items.ROTTEN_FLESH);
                    boolean isSpiderEye = item.equals(Items.SPIDER_EYE);
                    boolean isZeroHearts = isRottenFlesh || isSpiderEye;

                    boolean isCarrot = item.equals(Items.CARROT);
                    boolean isMelonSlice = item.equals(Items.MELON_SLICE);
                    boolean isChorusFruit = item.equals(Items.CHORUS_FRUIT);
                    boolean isBerry = item.equals(Items.SWEET_BERRIES) || item.equals(Items.GLOW_BERRIES);
                    boolean isHalfHeart = isCarrot || isMelonSlice || isChorusFruit || isBerry;

                    boolean isMushroomStew = item.equals(Items.MUSHROOM_STEW);
                    boolean isBeetrootStew = item.equals(Items.BEETROOT_SOUP);
                    boolean isRabbitStew = item.equals(Items.RABBIT_STEW);
                    boolean isSuspiciousStew = item.equals(Items.SUSPICIOUS_STEW);
                    boolean isFiveHearts = isMushroomStew || isBeetrootStew || isRabbitStew || isSuspiciousStew;

                    boolean isGoldenApple = item.equals(Items.GOLDEN_APPLE);
                    boolean isNotchApple = item.equals(Items.ENCHANTED_GOLDEN_APPLE);
                    boolean isFullHearts = isGoldenApple || isNotchApple;

                    if (isZeroHearts)
                        nutrition = 0;
                    else if (isHalfHeart)
                        nutrition = 1;
                    else if (isFiveHearts)
                        nutrition = 10;
                    else if (isFullHearts)
                        nutrition = 20;

                    this.eat(nutrition, foodProperties.getSaturationModifier());
                }
            }
        }
        else
            this.vanilla.eat(item, stack);
    }

    /**
     * Disables food game logic.
     */
    public void tick(Player player)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.tick(player);
    }

    /**
     * Reads the food data for the player.
     */
    public void readAdditionalSaveData(CompoundTag compoundTag)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.readAdditionalSaveData(compoundTag);
    }

    /**
     * Writes the food data for the player.
     */
    public void addAdditionalSaveData(CompoundTag compoundTag)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.addAdditionalSaveData(compoundTag);
    }

    /**
     * Replaces current player's food level with the maxed value.
     */
    public int getFoodLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getFoodLevel();
    }

    public int getLastFoodLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getLastFoodLevel();
    }

    /**
     * Get whether the player must eat food.
     */
    public boolean needsFood()
    {
        if (ModConfig.Gameplay.disableHunger())
            return player.getHealth() < player.getMaxHealth();
        else
            return this.vanilla.needsFood();
    }

    /**
     * Adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float exhaustion)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.addExhaustion(exhaustion);
    }

    public float getExhaustionLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getExhaustionLevel();
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getSaturationLevel();
    }

    public void setFoodLevel(int foodLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setFoodLevel(foodLevel);
    }

    public void setSaturation(float saturationLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setSaturation(saturationLevel);
    }

    public void setExhaustion(float exhaustionLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setExhaustion(exhaustionLevel);
    }
}
