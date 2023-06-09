package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.level.block.FurnaceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FurnaceBlock.class)
public abstract class FurnaceBlockMixin
{
    /**
     * Prevents the fire crackle sound from furnace blocks by muting the volume.
     * Controlled by the disable furnace tweak.
     */
    @ModifyArg(method = "animateTick", index = 5, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private float NT$onFireCrackle(float vanilla)
    {
        return ModConfig.Sound.disableFurnace() ? 0.0F : vanilla;
    }
}
