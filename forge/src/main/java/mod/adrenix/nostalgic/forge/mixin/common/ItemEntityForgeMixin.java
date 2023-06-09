package mod.adrenix.nostalgic.forge.mixin.common;

import mod.adrenix.nostalgic.mixin.widen.ItemEntityAccessor;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityForgeMixin extends Entity
{
    /* Dummy Constructor */

    private ItemEntityForgeMixin(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    /* Uniques */

    @Unique
    private boolean NT$getNeighbors(ItemEntity item)
    {
        return (item != (Object) this) && ((ItemEntityAccessor) item).NT$isMergable();
    }

    /* Injections */

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
    private void NT$onMergeWithNeighbors(CallbackInfo callback, Iterator<ItemEntity> iterator, ItemEntity entity)
    {
        AABB aabb = this.getBoundingBox().inflate(0.5, 0.0, 0.5);
        List<ItemEntity> entities = this.level().getEntitiesOfClass(ItemEntity.class, aabb, this::NT$getNeighbors);

        ItemServerUtil.mergeWithNeighbors(callback, entities, entity);
    }
}
