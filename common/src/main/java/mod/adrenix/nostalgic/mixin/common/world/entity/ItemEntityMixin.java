package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
    /**
     * Multiplayer:
     *
     * Prevents the ability for items to merge into item stacks.
     * Controlled by the old item merging tweak and server optimization limits.
     */
    @Inject
    (
        cancellable = true,
        method = "mergeWithNeighbours",
        locals = LocalCapture.CAPTURE_FAILSOFT,
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;tryToMerge(Lnet/minecraft/world/entity/item/ItemEntity;)V"
        )
    )
    private void NT$onMergeWithNeighbors(CallbackInfo callback, List<ItemEntity> entities, Iterator<ItemEntity> list, ItemEntity entity)
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

    /**
     * Multiplayer:
     *
     * If server merging optimization is disabled, then this injection will completely disable item merging.
     * Controlled by the old item merging tweak and server optimization limits.
     */
    @Inject(method = "isMergable", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onIsMergable(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.oldItemMerging() && ModConfig.Candy.getItemMergeLimit() == 64)
            callback.setReturnValue(false);
    }
}
