package mod.adrenix.nostalgic.forge.mixin.common;

import mod.adrenix.nostalgic.util.server.ModServerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityForgeMixin extends Entity
{
    /* Dummy Constructor */

    private LivingEntityForgeMixin(EntityType<?> entity, World level)
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
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Redirect(method = "onClimbable", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"))
    private boolean NT$onClimbable(Optional<BlockPos> ladderPos)
    {
        return ModServerUtil.Gameplay.isClimbable(this.world, this.getBlockStateAtPos(), this.getBlockPos());
    }

    /**
     * Multiplayer:
     *
     * Separates items from a clumped item entity into multiple item entities when a mob is killed.
     * Controlled by the item merging tweak.
     */
    @ModifyArg(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;forEach(Ljava/util/function/Consumer;)V"))
    private Consumer<ItemStack> NT$onDropFromLootTable(Consumer<ItemStack> consumer)
    {
        return ModServerUtil.Item.explodeStack(consumer);
    }
}
