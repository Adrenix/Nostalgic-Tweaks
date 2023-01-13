package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockClientMixin
{
    /**
     * Changes the sound returned for certain blocks.
     * Controlled by various sound tweaks.
     */
    @Inject(method = "getSoundType", at = @At("HEAD"), cancellable = true)
    private void NT$onGetSoundType(BlockState state, CallbackInfoReturnable<SoundType> callback)
    {
        if (ModConfig.Sound.oldBed() && state.getBlock() instanceof BedBlock)
            callback.setReturnValue(SoundType.STONE);
    }
}
