package mod.adrenix.nostalgic.forge.mixin;

import mod.adrenix.nostalgic.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityForgeMixin
{
    /**
     * Separates items from a clumped item entity into multiple item entities when a mob is killed.
     * Controlled by the item merging tweak.
     */
    @ModifyArg(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;forEach(Ljava/util/function/Consumer;)V"))
    private Consumer<ItemStack> NT$onDropFromLootTable(Consumer<ItemStack> consumer)
    {
        return ModUtil.Item.explodeStack(consumer);
    }
}
