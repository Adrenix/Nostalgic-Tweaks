package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityFabricMixin
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
    private void NT$onMergeWithNeighbors(CallbackInfo callback, List<ItemEntity> entities, Iterator<ItemEntity> iterator, ItemEntity entity)
    {
        ItemServerUtil.mergeWithNeighbors(callback, entities, entity);
    }
}
