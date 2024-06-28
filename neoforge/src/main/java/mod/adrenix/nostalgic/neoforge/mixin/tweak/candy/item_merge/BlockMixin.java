package mod.adrenix.nostalgic.neoforge.mixin.tweak.candy.item_merge;

import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.mixin.util.candy.ItemMixinHelper;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

@Mixin(Block.class)
public abstract class BlockMixin
{
    /* Shadows */

    @Shadow private static List<ItemEntity> capturedDrops;

    /* Injections */

    /**
     * Splits up the item stack spawned from breaking blocks.
     */
    @Inject(
        method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
        )
    )
    private static void nt_neoforge_item_merge$wrapCapturedDrops(Level level, Supplier<ItemEntity> supplier, ItemStack itemStack, CallbackInfo callback, @Local ItemEntity itemEntity)
    {
        if (ModTweak.ENABLED.get())
            ItemMixinHelper.splitEntity(level, itemEntity, capturedDrops::add);
    }
}
