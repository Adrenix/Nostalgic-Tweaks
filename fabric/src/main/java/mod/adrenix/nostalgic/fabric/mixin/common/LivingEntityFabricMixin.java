package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.util.server.BlockServerUtil;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFabricMixin extends Entity
{
    /* Dummy Constructor */

    private LivingEntityFabricMixin(EntityType<?> entity, Level level)
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
        return BlockServerUtil.isClimbable(this.level(), instance, this.blockPosition());
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
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"
        )
    )
    private Consumer<ItemStack> NT$onDropFromLootTable(Consumer<ItemStack> consumer)
    {
        return ItemServerUtil.splitConsumer(consumer);
    }
}
