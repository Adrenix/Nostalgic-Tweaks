package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;
    @Shadow public abstract void playLocalSound(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay);

    /**
     * Brings back the old nether shading.
     * Controlled by the old lighting tweak.
     */
    @Redirect
    (
        method = "getShade",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;constantAmbientLight()Z"
        )
    )
    private boolean NT$onGetNetherShade(DimensionSpecialEffects instance)
    {
        if (MixinConfig.Candy.oldNetherLighting() && Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension() == Level.NETHER)
            return false;
        return instance.constantAmbientLight();
    }

    /**
     * Disables on sounds when attacking.
     * Controlled by the sound attack tweak.
     *
     * Disables chest sounds on opening.
     * Controlled by various old chest tweaks.
     *
     * Disables unique mob stepping sounds in multiplayer.
     * Controlled by the old mob steps tweak.
     */
    @Inject
    (
        method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onPlayPositionedSound(Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, CallbackInfo callback)
    {
        /* Attack Sounds */

        boolean isAttack = sound == SoundEvents.PLAYER_ATTACK_KNOCKBACK ||
            sound == SoundEvents.PLAYER_ATTACK_CRIT ||
            sound == SoundEvents.PLAYER_ATTACK_NODAMAGE ||
            sound == SoundEvents.PLAYER_ATTACK_STRONG ||
            sound == SoundEvents.PLAYER_ATTACK_SWEEP ||
            sound == SoundEvents.PLAYER_ATTACK_WEAK
        ;

        if (MixinConfig.Sound.oldAttack() && isAttack)
        {
            callback.cancel();
            return;
        }

        /* Chest Sounds */

        BlockPos pos = new BlockPos(x, y, z);
        ClientLevel level = this.minecraft.level;
        boolean isWoodenChest = sound == SoundEvents.CHEST_OPEN || sound == SoundEvents.CHEST_CLOSE;
        boolean isEnderChest = sound == SoundEvents.ENDER_CHEST_OPEN || sound == SoundEvents.ENDER_CHEST_CLOSE;
        boolean isChestSound = false;

        if (level == null)
            return;

        if (MixinConfig.Candy.oldChest() && level.getBlockState(pos).is(Blocks.CHEST) && isWoodenChest)
            isChestSound = true;
        else if (MixinConfig.Candy.oldEnderChest() && level.getBlockState(pos).is(Blocks.ENDER_CHEST) && isEnderChest)
            isChestSound = true;
        else if (MixinConfig.Candy.oldTrappedChest() && level.getBlockState(pos).is(Blocks.TRAPPED_CHEST) && isWoodenChest)
            isChestSound = true;

        if (isChestSound)
        {
            callback.cancel();
            return;
        }

        /*
          Entity Walking Sounds:

          The following logic is intended for multiplayer support.

          While this does work in ideal environments, there may be points during play where sounds can become
          inconsistent if there is significant latency or if there is a path without the 'entity.' and '.step'
          identifiers.
         */

        boolean isEntityStep = sound.getLocation().getPath().contains("entity.") && sound.getLocation().getPath().contains(".step");

        if (MixinConfig.Sound.oldStep() && !Minecraft.getInstance().hasSingleplayerServer() && isEntityStep)
        {
            Entity entity = null;

            for (Entity next : level.entitiesForRendering())
            {
                boolean isX = NostalgicUtil.Numbers.tolerance((int) next.getX(), (int) x);
                boolean isY = NostalgicUtil.Numbers.tolerance((int) next.getY(), (int) y);
                boolean isZ = NostalgicUtil.Numbers.tolerance((int) next.getZ(), (int) z);

                if (isX && isY && isZ)
                {
                    entity = next;
                    break;
                }
            }

            if (entity == null)
                return;

            if (entity instanceof Spider || entity instanceof Silverfish)
                callback.cancel();
            else
            {
                BlockState state = level.getBlockState(pos.below());

                if (state.getMaterial().isLiquid())
                    return;
                else if (state.is(Blocks.AIR))
                {
                    callback.cancel();
                    return;
                }

                BlockState blockState = level.getBlockState(pos);
                SoundType soundType = blockState.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? blockState.getSoundType() : state.getSoundType();
                this.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), soundType.getStepSound(), entity.getSoundSource(), soundType.getVolume() * 0.15F, soundType.getPitch(), false);

                callback.cancel();
            }
        }
    }
}
