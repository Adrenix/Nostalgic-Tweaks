package mod.adrenix.nostalgic.mixin.tweak.candy.item_merge;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.adrenix.nostalgic.helper.candy.ItemHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
    /* Shadows */

    @Shadow
    public abstract ItemStack getItem();

    /* Injections */

    /**
     * If server merging optimization is disabled, then this injection will completely disable item merging.
     */
    @ModifyReturnValue(
        method = "isMergable",
        at = @At("RETURN")
    )
    private boolean nt_item_merge$isMergable(boolean isMergable)
    {
        if (CandyTweak.OLD_ITEM_MERGING.get() && CandyTweak.ITEM_MERGE_LIMIT.get() == 64)
            return false;

        return isMergable;
    }

    /**
     * Stores the neighbors that will be attempted to be merged in a local ref.
     */
    @ModifyExpressionValue(
        method = "mergeWithNeighbours",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
        )
    )
    private List<ItemEntity> nt_item_merge$onTryToMerge(List<ItemEntity> entities, @Share("neighbors") LocalRef<List<ItemEntity>> neighbors)
    {
        neighbors.set(entities);

        return entities;
    }

    /**
     * Prevents the ability for items to merge.
     */
    @ModifyExpressionValue(
        method = "mergeWithNeighbours",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;isMergable()Z"
        )
    )
    private boolean nt_item_merge$isMergableWithNeighbors(boolean isMergable, @Share("neighbors") LocalRef<List<ItemEntity>> neighbors)
    {
        if (!CandyTweak.OLD_ITEM_MERGING.get())
            return isMergable;

        if (!isMergable)
            return false;

        return ItemHelper.canMergeWithNeighbors(this.getItem(), neighbors.get());
    }
}
