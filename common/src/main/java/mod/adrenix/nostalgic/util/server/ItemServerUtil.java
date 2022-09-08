package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

/**
 * This utility is used by the server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.ItemClientUtil}.
 */

public abstract class ItemServerUtil
{
    /**
     * Used to disable old food stacking when food is being dropped from a loot table.
     */
    public static boolean isDroppingLoot = false;

    /**
     * Used to handle differences between forge and fabric when separating merged items.
     * @param consumer The current item stack consumer.
     * @return A modified consumer if the old item merging tweak is enabled.
     */
    public static Consumer<ItemStack> explodeStack(Consumer<ItemStack> consumer)
    {
        if (!ModConfig.Candy.oldItemMerging())
            return consumer;
        return stack ->
        {
            int count = stack.getCount();

            if (count >= ModConfig.Candy.getItemMergeLimit())
                consumer.accept(stack);
            else
            {
                ItemStack instance = stack.copy();
                instance.setCount(1);

                for (int i = 0; i < count; i++)
                    consumer.accept(instance);
            }
        };
    }

    /**
     * Used to handle item merging with similar neighbors nearby.
     * @param callback The mixin callback.
     * @param entities A list of entities near the given <code>entity</code>.
     * @param entity The entity to check for nearby neighbors.
     */
    public static void mergeWithNeighbors(CallbackInfo callback, List<ItemEntity> entities, ItemEntity entity)
    {
        boolean isBelowLimit = entities.size() + 1 < ModConfig.Candy.getItemMergeLimit() && entity.getItem().getCount() == 1;
        boolean isNeighborStacked = false;

        for (ItemEntity neighbor : entities)
        {
            if (neighbor.getItem().getCount() > 1)
                isNeighborStacked = true;
        }

        if (ModConfig.Candy.oldItemMerging() && isBelowLimit && !isNeighborStacked)
            callback.cancel();
    }
}
