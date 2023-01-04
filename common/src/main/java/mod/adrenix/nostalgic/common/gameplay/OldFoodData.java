package mod.adrenix.nostalgic.common.gameplay;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * This class overrides the vanilla food data system.
 * Since the class will be used by the server, keep vanilla client-only code out.
 */

public class OldFoodData extends FoodData
{
    /* Fields */

    private static final int MAX_FOOD_LEVEL = 20;
    private final FoodData vanilla;
    private final Player player;

    /* Constructor */

    /**
     * Create a new old food data instance.
     * @param player A player instance.
     */
    public OldFoodData(Player player)
    {
        this.player = player;
        this.vanilla = new FoodData();
    }

    /* Methods */

    /**
     * Prevents food stat updates and instead replenishes the player's hearts.
     * @param foodLevelModifier Amount of 0.5 hearts to fill.
     * @param saturationLevelModifier A saturation level modifier if old food is off.
     */
    public void eat(int foodLevelModifier, float saturationLevelModifier)
    {
        if (ModConfig.Gameplay.disableHunger())
            player.heal(foodLevelModifier);
        else
            this.vanilla.eat(foodLevelModifier, saturationLevelModifier);
    }

    /**
     * Updates a player's health or saturation level based on the given item and item stack.
     * @param item An item instance.
     * @param stack An item stack instance.
     */
    public void eat(Item item, ItemStack stack)
    {
        FoodProperties properties = item.getFoodProperties();
        boolean isFood = item.isEdible() && properties != null;

        if (ModConfig.Gameplay.disableHunger() && isFood)
        {
            Map.Entry<String, Integer> entry = ModConfig.Gameplay.getFoodHealth().getEntryFromItem(item);
            int nutrition = entry != null ? entry.getValue() : properties.getNutrition();

            this.eat(nutrition, properties.getSaturationModifier());
        }
        else
            this.vanilla.eat(item, stack);
    }

    /**
     * Disables food game logic.
     * @param player A player instance.
     */
    public void tick(Player player)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.tick(player);
    }

    /**
     * Reads the food data for the player.
     * @param compoundTag A compound tag instance.
     */
    public void readAdditionalSaveData(CompoundTag compoundTag)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.readAdditionalSaveData(compoundTag);
    }

    /**
     * Writes the food data for the player.
     * @param compoundTag A compound tag instance.
     */
    public void addAdditionalSaveData(CompoundTag compoundTag)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.addAdditionalSaveData(compoundTag);
    }

    /**
     * Replaces current player's food level with the maxed value.
     * If hunger is disabled, then the max food level is returned.
     *
     * @return The player's current food level.
     */
    public int getFoodLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getFoodLevel();
    }

    /**
     * Get the previously known food level. If hunger is disabled, then the max food level is returned.
     * @return The player's last known food level.
     */
    public int getLastFoodLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? MAX_FOOD_LEVEL : this.vanilla.getLastFoodLevel();
    }

    /**
     * @return Get whether the player must eat food.
     */
    public boolean needsFood()
    {
        if (ModConfig.Gameplay.disableHunger())
            return player.getHealth() < player.getMaxHealth();
        else
            return this.vanilla.needsFood();
    }

    /**
     * Adds input to food exhaustion level to a max of 40.
     */
    public void addExhaustion(float exhaustion)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.addExhaustion(exhaustion);
    }

    /**
     * Retrieves the player's exhaustion level. If hunger is disabled, then the value of 0.0F is returned.
     * @return The player's exhaustion level.
     */
    public float getExhaustionLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? 0.0F : this.vanilla.getExhaustionLevel();
    }

    /**
     * Get the player's food saturation level. If hunger is disabled, then the max of 20.0F is returned.
     * @return The player's saturation level.
     */
    public float getSaturationLevel()
    {
        return ModConfig.Gameplay.disableHunger() ? 20.0F : this.vanilla.getSaturationLevel();
    }

    /**
     * Set the player's food level.
     * @param foodLevel A food level value.
     */
    public void setFoodLevel(int foodLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setFoodLevel(foodLevel);
    }

    /**
     * Set the player's saturation level.
     * @param saturationLevel A saturation level value.
     */
    public void setSaturation(float saturationLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setSaturation(saturationLevel);
    }

    /**
     * Set the player's exhaustion level.
     * @param exhaustionLevel An exhaustion level value.
     */
    public void setExhaustion(float exhaustionLevel)
    {
        if (!ModConfig.Gameplay.disableHunger())
            this.vanilla.setExhaustion(exhaustionLevel);
    }
}
