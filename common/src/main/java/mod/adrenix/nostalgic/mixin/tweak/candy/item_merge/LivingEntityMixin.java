package mod.adrenix.nostalgic.mixin.tweak.candy.item_merge;

import mod.adrenix.nostalgic.mixin.util.candy.ItemMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Changes the item stack consumer into a separator that modifies the item stack into multiple item entities when a
     * mob is killed.
     */
    @ModifyArg(
        index = 2,
        method = "dropFromLootTable",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"
        )
    )
    private Consumer<ItemStack> nt_item_merge$modifyDropConsumer(Consumer<ItemStack> consumer)
    {
        if (CandyTweak.OLD_ITEM_MERGING.get())
            return itemStack -> ItemMixinHelper.splitStack(itemStack, consumer);

        return consumer;
    }
}
