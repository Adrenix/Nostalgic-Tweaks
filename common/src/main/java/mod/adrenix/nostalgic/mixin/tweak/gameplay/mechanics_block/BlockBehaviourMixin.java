package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin
{
    /**
     * Prevents a block's loot table from being used to generate block drops for any block defined in the self block
     * drop tweak. This is primarily used to simulate the old ore drops before the implementation of raw metals.
     */
    @ModifyReturnValue(
        method = "getDrops",
        at = @At(
            ordinal = 1,
            value = "RETURN"
        )
    )
    private List<ItemStack> nt_mechanics_block$modifyBlockDrops(List<ItemStack> drops, BlockState blockState)
    {
        if (GameplayTweak.SELF_BLOCK_DROPS.get().containsBlock(blockState.getBlock()))
        {
            drops.clear();
            drops.add(blockState.getBlock().asItem().getDefaultInstance());
        }

        return drops;
    }
}
