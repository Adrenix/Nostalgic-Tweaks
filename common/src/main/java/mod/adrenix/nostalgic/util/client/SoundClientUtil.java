package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SoundClientUtil
{
    /**
     * Consumer interface for playing a local sound.
     */
    public interface PlaySound
    {
        void accept(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay);
    }

    /**
     * Disables various sounds or plays various sounds depending on various tweak states. If this method returns a state
     * of {@code true}, then the sound callback/event should be canceled.
     *
     * @param level       The current client level.
     * @param x           The x-position of the sound.
     * @param y           The y-position of the sound.
     * @param z           The z-position of the sound.
     * @param soundHolder A holder with a sound event.
     * @param playSound   A consumer function that will play the given sound.
     * @return Whether the sound at the position was handled.
     */
    public static boolean isSoundAtPositionHandled(ClientLevel level, double x, double y, double z, Holder<SoundEvent> soundHolder, PlaySound playSound)
    {
        if (soundHolder == null)
            return false;

        SoundEvent sound = soundHolder.value();

        /* Attack Sounds */

        // @formatter:off
        boolean isAttack = sound == SoundEvents.PLAYER_ATTACK_KNOCKBACK ||
            sound == SoundEvents.PLAYER_ATTACK_CRIT ||
            sound == SoundEvents.PLAYER_ATTACK_NODAMAGE ||
            sound == SoundEvents.PLAYER_ATTACK_STRONG ||
            sound == SoundEvents.PLAYER_ATTACK_SWEEP ||
            sound == SoundEvents.PLAYER_ATTACK_WEAK;

        if (ModConfig.Sound.oldAttack() && isAttack)
            return true;

        /* Squid Sounds */

        boolean isSquid = sound == SoundEvents.SQUID_AMBIENT ||
            sound == SoundEvents.SQUID_DEATH ||
            sound == SoundEvents.SQUID_HURT ||
            sound == SoundEvents.SQUID_SQUIRT;

        if (ModConfig.Sound.disableSquid() && isSquid)
            return true;

        boolean isGlowSquid = sound == SoundEvents.GLOW_SQUID_DEATH ||
            sound == SoundEvents.GLOW_SQUID_HURT ||
            sound == SoundEvents.GLOW_SQUID_SQUIRT;

        if (ModConfig.Sound.disableGlowSquidOther() && isGlowSquid)
            return true;

        if (ModConfig.Sound.disableGlowSquidAmbience() && sound == SoundEvents.GLOW_SQUID_AMBIENT)
            return true;

        /* Fish Sounds */

        if (ModConfig.Sound.disableFishSwim() && sound == SoundEvents.FISH_SWIM)
            return true;

        boolean isFishHurt = sound == SoundEvents.COD_HURT ||
            sound == SoundEvents.PUFFER_FISH_HURT ||
            sound == SoundEvents.SALMON_HURT ||
            sound == SoundEvents.TADPOLE_HURT ||
            sound == SoundEvents.TROPICAL_FISH_HURT;

        if (ModConfig.Sound.disableFishHurt() && isFishHurt)
            return true;

        boolean isFishDeath = sound == SoundEvents.COD_DEATH ||
            sound == SoundEvents.PUFFER_FISH_DEATH ||
            sound == SoundEvents.SALMON_DEATH ||
            sound == SoundEvents.TADPOLE_DEATH ||
            sound == SoundEvents.TROPICAL_FISH_DEATH;

        if (ModConfig.Sound.disableFishDeath() && isFishDeath)
            return true;

        // @formatter:on
        /* Chest Sounds */

        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState state = level.getBlockState(pos);

        boolean isWoodChestSound = sound == SoundEvents.CHEST_OPEN || sound == SoundEvents.CHEST_CLOSE;
        boolean isEnderChestSound = sound == SoundEvents.ENDER_CHEST_OPEN || sound == SoundEvents.ENDER_CHEST_CLOSE;
        boolean isChestDisabled = false;

        if (ModConfig.Sound.disableChest() && state.is(Blocks.CHEST) && isWoodChestSound)
            isChestDisabled = true;
        else if (ModConfig.Sound.disableEnderChest() && state.is(Blocks.ENDER_CHEST) && isEnderChestSound)
            isChestDisabled = true;
        else if (ModConfig.Sound.disableTrappedChest() && state.is(Blocks.TRAPPED_CHEST) && isWoodChestSound)
            isChestDisabled = true;

        if (isChestDisabled)
            return true;

        boolean isOldChest = false;

        if (ModConfig.Sound.oldChest() && state.is(Blocks.CHEST) && isWoodChestSound)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && state.is(Blocks.ENDER_CHEST) && isEnderChestSound)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && state.is(Blocks.TRAPPED_CHEST) && isWoodChestSound)
            isOldChest = true;

        if (isOldChest)
        {
            SoundEvent chestSound = SoundEvents.WOODEN_DOOR_OPEN;
            RandomSource randomSource = level.random;
            float random = randomSource.nextFloat() * 0.1F + 0.9F;

            if (sound == SoundEvents.CHEST_CLOSE || sound == SoundEvents.ENDER_CHEST_CLOSE)
                chestSound = SoundEvents.WOODEN_DOOR_CLOSE;

            playSound.accept(x, y, z, chestSound, SoundSource.BLOCKS, 1.0F, random, false);

            return true;
        }

        /*
            Bed & Door Placement Sounds:

            Disables the placement sound if the block at the given position is a bed or door.
            Controlled by the old door sound tweak and the old bed sound tweak.
         */

        boolean isBlockedSound = false;
        boolean isPlacingSound = sound == SoundEvents.WOOD_PLACE || sound == SoundEvents.METAL_PLACE;

        if (ModConfig.Sound.disableDoor() && isPlacingSound && state.getBlock() instanceof DoorBlock)
            isBlockedSound = true;
        else if (ModConfig.Sound.disableBed() && state.getBlock() instanceof BedBlock)
            isBlockedSound = true;

        if (isBlockedSound)
            return true;

        /*
            Entity Walking Sounds:

            The following logic is intended for multiplayer support.

            While this does work in ideal environments, there may be points during play where sounds can become
            inconsistent if there is significant latency or if there is a path without the 'entity.' and '.step'
            identifiers.
         */

        boolean isEntityStep = sound.getLocation().getPath().contains("entity.") && sound.getLocation()
            .getPath()
            .contains(".step");

        if (ModConfig.Sound.oldStep() && !Minecraft.getInstance().hasSingleplayerServer() && isEntityStep)
        {
            Entity entity = null;

            for (Entity next : level.entitiesForRendering())
            {
                if (next instanceof ItemEntity)
                    continue;

                boolean isX = MathUtil.tolerance((int) next.getX(), (int) x);
                boolean isY = MathUtil.tolerance((int) next.getY(), (int) y);
                boolean isZ = MathUtil.tolerance((int) next.getZ(), (int) z);

                if (isX && isY && isZ)
                {
                    entity = next;
                    break;
                }
            }

            if (entity == null)
                return false;

            boolean isMinecraftEntity = entity.getType().getDescriptionId().contains("minecraft");
            boolean isEntityIgnored = entity instanceof Spider || entity instanceof Silverfish;
            boolean isModdedIgnored = ModConfig.Sound.ignoreModdedStep() && !isMinecraftEntity;

            if (isEntityIgnored)
                return true;
            else if (!isModdedIgnored)
            {
                BlockState standing = level.getBlockState(pos.below());

                if (!standing.getFluidState().isEmpty())
                    return false;
                else if (standing.is(Blocks.AIR))
                    return true;

                BlockState inside = level.getBlockState(pos);
                SoundType soundType = inside.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? inside.getSoundType() : standing.getSoundType();

                playSound.accept(x, y, z, soundType.getStepSound(), entity.getSoundSource(), soundType.getVolume() * 0.15F, soundType.getPitch(), false);

                return true;
            }
        }

        return false;
    }
}
