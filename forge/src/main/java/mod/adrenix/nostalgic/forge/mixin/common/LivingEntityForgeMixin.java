package mod.adrenix.nostalgic.forge.mixin.common;

import mod.adrenix.nostalgic.util.server.BlockServerUtil;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    private LivingEntityForgeMixin(EntityType<?> entity, Level level)
    {
        super(entity, level);
    }

    /* Injections */

    /**
     * Multiplayer:
     * <p>
     * Allows players to continually climb if there is a single gap between two ladders. Controlled by the old ladder
     * gap tweak.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Redirect(
        method = "onClimbable",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;isPresent()Z"
        )
    )
    private boolean NT$onClimbable(Optional<BlockPos> ladder)
    {
        return BlockServerUtil.isClimbable(this.level(), this.getFeetBlockState(), this.blockPosition()) ||
            ladder.isPresent();
    }

    /**
     * Multiplayer:
     * <p>
     * Separates items from a clumped item entity into multiple item entities when a mob is killed. Controlled by the
     * item merging tweak.
     */
    @ModifyArg(
        method = "dropFromLootTable",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;forEach(Ljava/util/function/Consumer;)V"
        )
    )
    private Consumer<ItemStack> NT$onDropFromLootTable(Consumer<ItemStack> consumer)
    {
        return ItemServerUtil.splitConsumer(consumer);
    }
}
