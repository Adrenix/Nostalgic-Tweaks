package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.ChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin
{
    /**
     * Always allows chest blocks to be opened regardless of whether there is a block above the chest or a cat sitting
     * on the chest.
     */
    @ModifyReturnValue(
        method = "isChestBlockedAt",
        at = @At("RETURN")
    )
    private static boolean nt_mechanics_block$isChestBlocked(boolean isChestBlocked)
    {
        return !GameplayTweak.ALWAYS_OPEN_CHEST.get() && isChestBlocked;
    }
}
