package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
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
    private static void NT$onPopResource(World level, Supplier<ItemEntity> supplier, ItemStack itemStack, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldItemMerging())
            return;

        ItemEntity entity = supplier.get();
        ArrayList<ItemEntity> entities = new ArrayList<>();

        for (int i = 0; i < itemStack.getCount(); i++)
        {
            double x = (double) ((float) entity.getX() + 0.01f) + MathHelper.nextDouble(level.random, -0.04, 0.04);
            double y = (double) ((float) entity.getY() + 0.01f) + MathHelper.nextDouble(level.random, -0.04, 0.04) - (double) EntityType.ITEM.getHeight() / 2.0f;
            double z = (double) ((float) entity.getZ() + 0.01f) + MathHelper.nextDouble(level.random, -0.04, 0.04);

            entities.add(new ItemEntity(level, x, y, z, itemStack));
        }

        for (ItemEntity instance : entities)
        {
            instance.getStack().setCount(1);
            instance.setToDefaultPickupDelay();
            level.spawnEntity(instance);
        }

        entities.clear();

        // The first loop accounted for the original item entity, so cancelling here is fine
        callback.cancel();
    }
}
