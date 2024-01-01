package mod.adrenix.nostalgic.util.server.world;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The server uses this utility. For safety, keep client-only code out. For a client only utility use
 * {@link mod.adrenix.nostalgic.util.client.world.ItemClientUtil}.
 */
public abstract class ItemServerUtil
{
    /**
     * Used to disable old food stacking when food is being dropped from a loot table.
     */
    public static boolean isDroppingLoot = false;

    /**
     * Splits an item stack into separate item entities.
     *
     * @param stack    The item stack to separate.
     * @param consumer A consumer that accepts an item stack.
     */
    private static void split(ItemStack stack, Consumer<ItemStack> consumer)
    {
        int count = stack.getCount();

        if (count >= CandyTweak.ITEM_MERGE_LIMIT.get())
            consumer.accept(stack);
        else
        {
            ItemStack instance = stack.copy();
            instance.setCount(1);

            for (int i = 0; i < count; i++)
                consumer.accept(instance);
        }
    }

    /**
     * Used to handle differences between forge and fabric when separating merged items.
     *
     * @param consumer The current item stack consumer.
     * @return A modified consumer if the old item merging tweak is enabled.
     */
    public static Consumer<ItemStack> splitConsumer(Consumer<ItemStack> consumer)
    {
        return CandyTweak.OLD_ITEM_MERGING.get() ? stack -> split(stack, consumer) : consumer;
    }

    /**
     * Used to handle the splitting up of loot from the given dropped item stack. This helper method will set the flag
     * for {@link ItemServerUtil#isDroppingLoot}.
     *
     * @param callback The mixin callback.
     * @param entity   The entity dropping the loot.
     * @param drop     The item stack being dropped.
     */
    public static void splitLoot(CallbackInfo callback, Entity entity, ItemStack drop)
    {
        if (CandyTweak.OLD_ITEM_MERGING.get())
            split(drop, entity::spawnAtLocation);
        else
            entity.spawnAtLocation(drop);

        isDroppingLoot = false;
        callback.cancel();
    }

    /**
     * Used to handle item merging with similar neighbors nearby.
     *
     * @param callback The mixin callback.
     * @param entities A list of entities near the given <code>entity</code>.
     * @param entity   The entity to check for nearby neighbors.
     */
    public static void mergeWithNeighbors(CallbackInfo callback, List<ItemEntity> entities, ItemEntity entity)
    {
        boolean isBelowLimit = entities.size() + 1 < CandyTweak.ITEM_MERGE_LIMIT.get() && entity.getItem()
            .getCount() == 1;
        boolean isNeighborStacked = false;

        for (ItemEntity neighbor : entities)
        {
            if (neighbor.getItem().getCount() > 1)
                isNeighborStacked = true;
        }

        if (CandyTweak.OLD_ITEM_MERGING.get() && isBelowLimit && !isNeighborStacked)
            callback.cancel();
    }

    /* Combat Damage */

    private static final Map<Class<? extends TieredItem>, Function<TieredItem, Float>> OLD_DAMAGE = Map.of(SwordItem.class, item -> item.getTier()
        .getAttackDamageBonus() + 4.0F, AxeItem.class, item -> item.getTier()
        .getAttackDamageBonus() + 3.0F, PickaxeItem.class, item -> item.getTier()
        .getAttackDamageBonus() + 2.0F, ShovelItem.class, item -> item.getTier()
        .getAttackDamageBonus() + 1.0F, HoeItem.class, item -> item.getTier().getAttackDamageBonus());

    /**
     * Get the old damage value based on the provided tiered item instance.
     *
     * @param item The tiered item instance.
     * @return An old damage value, if it is in the old damage map, 0.0F otherwise.
     */
    public static float getOldDamage(TieredItem item)
    {
        Class<? extends TieredItem> tierClass = item.getClass();

        if (OLD_DAMAGE.containsKey(tierClass))
            return OLD_DAMAGE.get(tierClass).apply(item);

        return 0.0F;
    }

    /**
     * Checks if the given tiered item instance has a tier and is within the old damage map.
     *
     * @param item The tiered item instance to check.
     * @return Whether the tiered item instance is within the old damage map.
     */
    public static boolean isVanillaTiered(TieredItem item)
    {
        return OLD_DAMAGE.containsKey(item.getClass());
    }
}
