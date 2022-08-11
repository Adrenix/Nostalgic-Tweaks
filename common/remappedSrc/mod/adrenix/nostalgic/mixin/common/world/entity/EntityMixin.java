package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.widen.IMixinEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    /* Shadow */

    @Shadow public World level;
    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    /**
     * Multiplayer:
     *
     * Prevents any unique mob stepping sounds from playing.
     * Controlled by the old step sounds tweak.
     */
    @Redirect
    (
        method = "move",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;playStepSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
        )
    )
    private void NT$onMoveSound(Entity instance, BlockPos pos, BlockState state)
    {
        if (ModConfig.Sound.oldStep())
        {
            boolean isMinecraftEntity = instance.getType().getTranslationKey().contains("minecraft");
            boolean isEntityIgnored = instance instanceof SpiderEntity || instance instanceof SilverfishEntity;
            boolean isModdedIgnored = ModConfig.Sound.ignoreModdedStep() && !isMinecraftEntity;

            if (isEntityIgnored || isModdedIgnored)
                return;

            if (!state.getMaterial().isLiquid())
            {
                BlockState blockStateAbove = this.level.getBlockState(pos.up());
                BlockSoundGroup soundType = blockStateAbove.isOf(Blocks.SNOW) ? blockStateAbove.getSoundGroup() : state.getSoundGroup();
                this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
            }
        }
        else
        {
            IMixinEntity accessor = (IMixinEntity) instance;
            accessor.NT$invokeStepSound(pos, state);
        }
    }
}
