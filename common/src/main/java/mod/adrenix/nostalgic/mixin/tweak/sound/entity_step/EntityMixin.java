package mod.adrenix.nostalgic.mixin.tweak.sound.entity_step;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.mixin.util.sound.SoundMixinHelper;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    /* Shadows */

    @Shadow private Level level;

    @Shadow
    protected abstract BlockPos getPrimaryStepSoundBlockPos(BlockPos pos);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    /**
     * Prevents unique mob stepping sounds from playing and instead plays the stepping sound of the block the entity is
     * stepping on.
     */
    @WrapOperation(
        method = "walkingStepSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;playStepSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
        )
    )
    private void nt_entity_step$onWalkingStepSound(Entity entity, BlockPos blockPos, BlockState blockState, Operation<Void> playStepSound)
    {
        if (!SoundTweak.OLD_STEP.get())
        {
            playStepSound.call(entity, blockPos, blockState);
            return;
        }

        boolean isEntityIgnored = SoundMixinHelper.isEntityStepIgnored(entity);
        boolean isModdedIgnored = SoundMixinHelper.isModdedStepIgnored(entity);
        boolean isInsideFluid = !blockState.getFluidState().isEmpty();

        if (isEntityIgnored || isModdedIgnored || isInsideFluid)
            return;

        SoundType soundType = this.level.getBlockState(this.getPrimaryStepSoundBlockPos(blockPos)).getSoundType();

        this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
    }
}
