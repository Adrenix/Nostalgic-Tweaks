package mod.adrenix.nostalgic.mixin.tweak.candy.item_merge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.mixin.util.candy.ItemMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public abstract class BlockMixin
{
    /**
     * Splits up the item stack spawned from breaking blocks.
     */
    @WrapOperation(
        method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    private static boolean nt_item_merge$wrapAddFreshEntity(Level level, Entity entity, Operation<Boolean> operation)
    {
        if (entity instanceof ItemEntity itemEntity)
            ItemMixinHelper.splitEntity(level, itemEntity);

        return operation.call(level, entity);
    }
}
