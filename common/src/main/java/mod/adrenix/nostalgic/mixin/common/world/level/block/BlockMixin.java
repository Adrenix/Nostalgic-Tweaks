package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.function.Supplier;

@Mixin(Block.class)
public abstract class BlockMixin
{
    /**
     * Multiplayer:
     *
     * Splits up the item stack that comes from mining blocks.
     * Controlled by the old item merging tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;")
    )
    private static void NT$onPopResource(Level level, Supplier<ItemEntity> supplier, ItemStack itemStack, CallbackInfo callback)
    {
        if (!MixinConfig.Candy.oldItemMerging())
            return;

        ItemEntity entity = supplier.get();
        ArrayList<ItemEntity> entities = new ArrayList<>();

        for (int i = 0; i < itemStack.getCount(); i++)
        {
            double x = (double) ((float) entity.getX() + 0.01f) + Mth.nextDouble(level.random, -0.04, 0.04);
            double y = (double) ((float) entity.getY() + 0.01f) + Mth.nextDouble(level.random, -0.04, 0.04) - (double) EntityType.ITEM.getHeight() / 2.0f;
            double z = (double) ((float) entity.getZ() + 0.01f) + Mth.nextDouble(level.random, -0.04, 0.04);

            entities.add(new ItemEntity(level, x, y, z, itemStack));
        }

        for (ItemEntity instance : entities)
        {
            instance.getItem().setCount(1);
            instance.setDefaultPickUpDelay();
            level.addFreshEntity(instance);
        }

        entities.clear();

        // The first loop accounted for the original item entity, so cancelling here is fine
        callback.cancel();
    }
}
