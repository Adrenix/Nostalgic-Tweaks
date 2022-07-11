package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
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
        if (ModConfig.Candy.oldNetherLighting() && Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension() == Level.NETHER)
            return false;
        return instance.constantAmbientLight();
    }

    /**
     * Disables sounds when attacking.
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
        method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFJ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onPlayPositionedSound(Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, long randomSeed, CallbackInfo callback)
    {
        /* Attack Sounds */

        boolean isAttack = sound == SoundEvents.PLAYER_ATTACK_KNOCKBACK ||
            sound == SoundEvents.PLAYER_ATTACK_CRIT ||
            sound == SoundEvents.PLAYER_ATTACK_NODAMAGE ||
            sound == SoundEvents.PLAYER_ATTACK_STRONG ||
            sound == SoundEvents.PLAYER_ATTACK_SWEEP ||
            sound == SoundEvents.PLAYER_ATTACK_WEAK
        ;

        if (ModConfig.Sound.oldAttack() && isAttack)
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

        BlockState state = level.getBlockState(pos);

        if (ModConfig.Candy.oldChest() && state.is(Blocks.CHEST) && isWoodenChest)
            isChestSound = true;
        else if (ModConfig.Candy.oldEnderChest() && state.is(Blocks.ENDER_CHEST) && isEnderChest)
            isChestSound = true;
        else if (ModConfig.Candy.oldTrappedChest() && state.is(Blocks.TRAPPED_CHEST) && isWoodenChest)
            isChestSound = true;

        if (isChestSound)
        {
            callback.cancel();
            return;
        }

        /*
            Bed & Door Placement Sounds:

            Disables the placement sound if the block at the given position is a bed or door.
            Controlled by the old door sound tweak and the old bed sound tweak.
         */

        boolean isBlockedSound = false;

        if (ModConfig.Sound.oldDoor() && state.getBlock() instanceof DoorBlock)
            isBlockedSound = true;
        else if (ModConfig.Sound.oldBed() && state.getBlock() instanceof BedBlock)
            isBlockedSound = true;

        if (isBlockedSound)
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

        if (ModConfig.Sound.oldStep() && !Minecraft.getInstance().hasSingleplayerServer() && isEntityStep)
        {
            Entity entity = null;

            for (Entity next : level.entitiesForRendering())
            {
                if (next instanceof ItemEntity)
                    continue;

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

            boolean isMinecraftEntity = entity.getType().getDescriptionId().contains("minecraft");
            boolean isEntityIgnored = entity instanceof Spider || entity instanceof Silverfish;
            boolean isModdedIgnored = ModConfig.Sound.ignoreModdedStep() && !isMinecraftEntity;

            if (isEntityIgnored)
                callback.cancel();
            else if (!isModdedIgnored)
            {
                BlockState standing = level.getBlockState(pos.below());

                if (standing.getMaterial().isLiquid())
                    return;
                else if (standing.is(Blocks.AIR))
                {
                    callback.cancel();
                    return;
                }

                BlockState inside = level.getBlockState(pos);
                SoundType soundType = inside.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? inside.getSoundType() : standing.getSoundType();
                this.playLocalSound(x, y, z, soundType.getStepSound(), entity.getSoundSource(), soundType.getVolume() * 0.15F, soundType.getPitch(), false);

                callback.cancel();
            }
        }
    }
}
