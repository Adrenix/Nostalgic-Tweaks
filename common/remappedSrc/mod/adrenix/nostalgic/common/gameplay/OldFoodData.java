package mod.adrenix.nostalgic.common.gameplay;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

/**
 * This class overrides the vanilla food data system.
 * Since the class will be used by the server, keep vanilla client-only code out.
 */

public class OldFoodData extends HungerManager
{
    private static final int MAX_FOOD_LEVEL = 20;
    private final HungerManager vanilla;
    private final PlayerEntity player;

    public OldFoodData(PlayerEntity player)
    {
        this.player = player;
        this.vanilla = new HungerManager();
    }

    /**
     * Prevent food stat updates - instead replenish player hearts.
     */
    public void add(int foodLevelModifier, float saturationLevelModifier)
    {
        if (ModConfig.Gameplay.disableHunger())
            player.heal(foodLevelModifier);
        else
            this.vanilla.add(foodLevelModifier, saturationLevelModifier);
    }

    public void eat(Item item, ItemStack stack)
    {
        if (ModConfig.Gameplay.disableHunger())
        {
            if (item.isFood())
            {
                FoodComponent foodProperties = item.getFoodComponent();
                if (foodProperties != null)
                {
                    int nutrition = foodProperties.getHunger();

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

                    this.add(nutrition, foodProperties.getSaturationModifier());
                }
            }
        }
        else
            this.vanilla.eat(item, stack);
    }

    /**
     * Disables food game logic.
     */
    public void update(PlayerEntity player)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.update(player);
    }

    /**
     * Reads the food data for the player.
     */
    public void readNbt(NbtCompound compoundTag)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.readNbt(compoundTag);
    }

    /**
     * Writes the food data for the player.
     */
    public void writeNbt(NbtCompound compoundTag)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.writeNbt(compoundTag);
    }

    /**
     * Replaces current player's food level with the maxed value.
     */
    public int getFoodLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getFoodLevel();
    }

    public int getPrevFoodLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getPrevFoodLevel();
    }

    /**
     * Get whether the player must eat food.
     */
    public boolean isNotFull()
    {
        if (ModConfig.Gameplay.disableHunger())
            return player.getHealth() < player.getMaxHealth();
        else
            return this.vanilla.isNotFull();
    }

    /**
     * Adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float exhaustion)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.addExhaustion(exhaustion);
    }

    public float getExhaustion()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getExhaustion();
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

    public void setSaturationLevel(float saturationLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setSaturationLevel(saturationLevel);
    }

    public void setExhaustion(float exhaustionLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setExhaustion(exhaustionLevel);
    }
}
