package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.mixin.widen.IMixinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public Level level;
    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    /**
     * Multiplayer:
     *
     * Prevents any unique mob stepping sounds from playing.
     * Controlled by the old step sounds toggle.
     */
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;playStepSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    protected void onMoveSound(Entity instance, BlockPos pos, BlockState state)
    {
        if (MixinConfig.Sound.oldStep())
        {
            if (instance instanceof Spider)
                return;

            if (!state.getMaterial().isLiquid())
            {
                BlockState blockStateAbove = this.level.getBlockState(pos.above());
                SoundType soundType = blockStateAbove.is(Blocks.SNOW) ? blockStateAbove.getSoundType() : state.getSoundType();
                this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
            }
        }
        else
        {
            IMixinEntity accessor = (IMixinEntity) instance;
            accessor.invokeStepSound(pos, state);
        }
    }
}
