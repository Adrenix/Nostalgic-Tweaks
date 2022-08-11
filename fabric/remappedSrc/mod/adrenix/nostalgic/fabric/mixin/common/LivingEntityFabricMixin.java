package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.util.server.ModServerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFabricMixin extends Entity
{
    /* Dummy Constructor */

    private LivingEntityFabricMixin(EntityType<?> entity, World level)
    {
        super(entity, level);
    }

    /* Injections */

    /**
     * Multiplayer:
     *
     * Allows players to continually climb if there is a single gap between two ladders.
     * Controlled by the old ladder gap tweak.
     */
    @Redirect(method = "onClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean NT$onClimbable(BlockState instance, TagKey<Block> tagKey)
    {
        return ModServerUtil.Gameplay.isClimbable(this.world, instance, this.getBlockPos());
    }

    /**
     * Multiplayer:
     *
     * Separates items from a clumped item entity into multiple item entities when a mob is killed.
     * Controlled by the item merging tweak.
     */
    @ModifyArg
    (
        method = "dropFromLootTable",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"
        )
    )
    private Consumer<ItemStack> NT$onDropFromLootTable(Consumer<ItemStack> consumer)
    {
        return ModServerUtil.Item.explodeStack(consumer);
    }
}
