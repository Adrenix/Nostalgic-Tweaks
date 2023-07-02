package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * Each mod loader handles sound events differently. This utility acts as a helper to bridge to the two mod loaders, so
 * code does not to be duplicated.
 */

public abstract class SoundUtil
{
    /**
     * The keys of specific sounds.
     * Defined in the mod's <code>sounds.json</code>.
     */
    public static class Key
    {
        public static final String PLAYER_HURT = "entity.player.hurt";
        public static final String BLANK = "blank";
    }

    /**
     * Suppliers to provide sound events.
     * These are defined during mod loader initialization.
     */
    public static class Event
    {
        public static Supplier<SoundEvent> PLAYER_HURT;
        public static Supplier<SoundEvent> BLANK;
    }

    /**
     * Consumer interface for playing a local sound.
     */
    public interface PlaySound
    {
        void accept(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay);
    }

    /* Utility */

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
    public static boolean isSoundAtPositionHandled(Level level, double x, double y, double z, Holder<SoundEvent> soundHolder, PlaySound playSound)
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

        BlockPos blockPos = BlockPos.containing(x, y, z);
        BlockState blockState = level.getBlockState(blockPos);

        boolean isWoodChestSound = sound == SoundEvents.CHEST_OPEN || sound == SoundEvents.CHEST_CLOSE;
        boolean isEnderChestSound = sound == SoundEvents.ENDER_CHEST_OPEN || sound == SoundEvents.ENDER_CHEST_CLOSE;
        boolean isChestDisabled = false;

        if (ModConfig.Sound.disableChest() && blockState.is(Blocks.CHEST) && isWoodChestSound)
            isChestDisabled = true;
        else if (ModConfig.Sound.disableEnderChest() && blockState.is(Blocks.ENDER_CHEST) && isEnderChestSound)
            isChestDisabled = true;
        else if (ModConfig.Sound.disableTrappedChest() && blockState.is(Blocks.TRAPPED_CHEST) && isWoodChestSound)
            isChestDisabled = true;

        if (isChestDisabled)
            return true;

        boolean isOldChest = false;

        if (ModConfig.Sound.oldChest() && blockState.is(Blocks.CHEST) && isWoodChestSound)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && blockState.is(Blocks.ENDER_CHEST) && isEnderChestSound)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && blockState.is(Blocks.TRAPPED_CHEST) && isWoodChestSound)
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
            Controlled by the old door sound tweak and old bed sounds.
         */

        boolean isBlockedSound = false;
        boolean isPlacingSound = sound == SoundEvents.WOOD_PLACE || sound == SoundEvents.METAL_PLACE;

        if (ModConfig.Sound.disableDoor() && isPlacingSound && blockState.getBlock() instanceof DoorBlock)
            isBlockedSound = true;
        else if (ModConfig.Sound.disableBed() && blockState.getBlock() instanceof BedBlock)
            isBlockedSound = true;

        return isBlockedSound;
    }
}
