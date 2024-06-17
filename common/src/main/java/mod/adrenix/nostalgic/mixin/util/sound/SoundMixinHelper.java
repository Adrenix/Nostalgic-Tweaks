package mod.adrenix.nostalgic.mixin.util.sound;

import mod.adrenix.nostalgic.mixin.access.EntityAccess;
import mod.adrenix.nostalgic.mixin.util.candy.ChestMixinHelper;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * This helper class is used only by the client.
 */
public abstract class SoundMixinHelper
{
    /**
     * Check if the entity step should be ignored.
     *
     * @param entity The {@link Entity} to check.
     * @return Whether the step sound is ignored.
     */
    public static boolean isEntityStepIgnored(Entity entity)
    {
        return entity instanceof Spider || entity instanceof Silverfish || entity instanceof Bee;
    }

    /**
     * Check if the modded entity step should be ignored.
     *
     * @param entity The {@link Entity} to check.
     * @return Whether the modded step sound is ignored.
     */
    public static boolean isModdedStepIgnored(Entity entity)
    {
        return SoundTweak.IGNORE_MODDED_STEP.get() && !entity.getType().getDescriptionId().contains("minecraft");
    }

    /**
     * Handle sound logic at the given position.
     *
     * @param level  The {@link ClientLevel} to get block data from.
     * @param x      The x-coordinate of the sound.
     * @param y      The y-coordinate of the sound.
     * @param z      The z-coordinate of the sound.
     * @param sound  The {@link SoundEvent} being played at the given coordinates.
     * @param source The {@link SoundSource} of the sound.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     * @return Whether the sound was blocked or changed at the given position.
     */
    @Nullable
    public static PositionSoundHandler getHandlerAt(ClientLevel level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch)
    {
        if (level == null || sound == null)
            return null;

        PositionSoundHandler handler = PositionSoundHandler.create(level, x, y, z, sound, source, volume, pitch);

        handler.apply(SoundMixinHelper::isDisabledHandled);
        handler.apply(SoundMixinHelper::isFurnaceHandled);
        handler.apply(SoundMixinHelper::isAttackHandled);
        handler.apply(SoundMixinHelper::isGrowthHandled);
        handler.apply(SoundMixinHelper::isSquidHandled);
        handler.apply(SoundMixinHelper::isChestHandled);
        handler.apply(SoundMixinHelper::isLavaHandled);
        handler.apply(SoundMixinHelper::isSwimHandled);
        handler.apply(SoundMixinHelper::isFishHandled);
        handler.apply(SoundMixinHelper::isStepHandled);
        handler.apply(SoundMixinHelper::isDoorHandled);
        handler.apply(SoundMixinHelper::isBedHandled);
        handler.apply(SoundMixinHelper::isXpHandled);

        return handler;
    }

    /**
     * Mutes sounds if they are found within the list of disabled positioned sounds.
     */
    private static boolean isDisabledHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLED_POSITIONED_SOUNDS.get().contains(handler.sound.getLocation().toString()))
            return handler.mute();

        return false;
    }

    /**
     * Mutes attacking sounds.
     */
    private static boolean isAttackHandled(PositionSoundHandler handler)
    {
        if (!SoundTweak.OLD_ATTACK.get())
            return false;

        boolean isCrit = handler.compare(SoundEvents.PLAYER_ATTACK_CRIT);
        boolean isKnock = handler.compare(SoundEvents.PLAYER_ATTACK_KNOCKBACK);
        boolean isDamage = handler.compare(SoundEvents.PLAYER_ATTACK_NODAMAGE);
        boolean isStrong = handler.compare(SoundEvents.PLAYER_ATTACK_STRONG);
        boolean isSweep = handler.compare(SoundEvents.PLAYER_ATTACK_SWEEP);
        boolean isWeak = handler.compare(SoundEvents.PLAYER_ATTACK_WEAK);
        boolean isAttack = isCrit || isKnock || isDamage || isStrong || isSweep || isWeak;

        if (isAttack)
            handler.mute();

        return isAttack;
    }

    /**
     * Mutes the fire crackle sounds emitted from furnace and blast furnace blocks.
     */
    private static boolean isFurnaceHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_FURNACE.get() && handler.blockState.getBlock() instanceof FurnaceBlock)
            return handler.mute(SoundEvents.FURNACE_FIRE_CRACKLE);

        if (SoundTweak.DISABLE_BLAST_FURNACE.get() && handler.blockState.getBlock() instanceof BlastFurnaceBlock)
            return handler.mute(SoundEvents.BLASTFURNACE_FIRE_CRACKLE);

        return false;
    }

    /**
     * Mutes growth sounds when bone meal is used.
     */
    private static boolean isGrowthHandled(PositionSoundHandler handler)
    {
        return SoundTweak.DISABLE_GROWTH.get() && handler.mute(SoundEvents.BONE_MEAL_USE);
    }

    /**
     * Mutes squid and glow squid sounds.
     */
    private static boolean isSquidHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_SQUID.get())
        {
            if (handler.mute(SoundEvents.SQUID_AMBIENT, SoundEvents.SQUID_DEATH, SoundEvents.SQUID_HURT, SoundEvents.SQUID_SQUIRT))
                return true;
        }

        if (SoundTweak.DISABLE_GLOW_SQUID_OTHER.get())
        {
            if (handler.mute(SoundEvents.GLOW_SQUID_DEATH, SoundEvents.GLOW_SQUID_HURT, SoundEvents.GLOW_SQUID_SQUIRT))
                return true;
        }

        if (SoundTweak.DISABLE_GLOW_SQUID_AMBIENCE.get())
            return handler.mute(SoundEvents.GLOW_SQUID_AMBIENT);

        return false;
    }

    /**
     * Mutes lava ambience and lava pop sounds.
     */
    private static boolean isLavaHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_LAVA_AMBIENCE.get() && handler.mute(SoundEvents.LAVA_AMBIENT))
            return true;

        if (SoundTweak.DISABLE_LAVA_POP.get())
            return handler.mute(SoundEvents.LAVA_POP);

        return false;
    }

    /**
     * Mutes swim sounds from entities.
     */
    private static boolean isSwimHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_GENERIC_SWIM.get())
            return handler.mute(SoundEvents.GENERIC_SWIM, SoundEvents.PLAYER_SWIM);

        return false;
    }

    /**
     * Mutes fish sounds.
     */
    private static boolean isFishHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_FISH_SWIM.get() && handler.mute(SoundEvents.FISH_SWIM))
            return true;

        if (SoundTweak.DISABLE_FISH_HURT.get())
        {
            if (handler.mute(SoundEvents.COD_HURT, SoundEvents.PUFFER_FISH_HURT, SoundEvents.SALMON_HURT, SoundEvents.TADPOLE_HURT, SoundEvents.TROPICAL_FISH_HURT))
                return true;
        }

        if (SoundTweak.DISABLE_FISH_DEATH.get())
            return handler.mute(SoundEvents.COD_DEATH, SoundEvents.PUFFER_FISH_DEATH, SoundEvents.SALMON_DEATH, SoundEvents.TADPOLE_DEATH, SoundEvents.TROPICAL_FISH_DEATH);

        return false;
    }

    /**
     * Mutes door placement sounds.
     */
    private static boolean isDoorHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_DOOR_PLACE.get() && handler.blockState.getBlock() instanceof DoorBlock)
        {
            if (handler.sound == handler.blockState.getSoundType().getPlaceSound())
                return handler.mute();
        }

        return false;
    }

    /**
     * Mutes bed placement sounds.
     */
    private static boolean isBedHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_BED_PLACE.get() && handler.blockState.getBlock() instanceof BedBlock)
        {
            if (handler.sound == handler.blockState.getSoundType().getPlaceSound())
                return handler.mute();
        }

        return false;
    }

    /**
     * Mutes chest sounds or changes the sound to a door open/close sound.
     */
    private static boolean isChestHandled(PositionSoundHandler handler)
    {
        boolean isOpenSound = handler.compare(SoundEvents.CHEST_OPEN, SoundEvents.ENDER_CHEST_OPEN);
        boolean isCloseSound = handler.compare(SoundEvents.CHEST_CLOSE, SoundEvents.ENDER_CHEST_CLOSE);
        boolean isChestSound = isOpenSound || isCloseSound;

        if (!isChestSound)
            return false;

        if (SoundTweak.DISABLE_CHEST.get() && ChestMixinHelper.isOld(handler.blockState))
            handler.mute();

        if (SoundTweak.OLD_CHEST.get())
        {
            handler.sound = SoundEvents.WOODEN_DOOR_OPEN;

            if (handler.compare(SoundEvents.CHEST_CLOSE, SoundEvents.ENDER_CHEST_CLOSE))
                handler.sound = SoundEvents.WOODEN_DOOR_CLOSE;

            handler.pitch = handler.randomSource().nextFloat() * 0.1F + 0.9F;
        }

        return true;
    }

    /**
     * Changes the experience pickup sound or mutes it.
     */
    private static boolean isXpHandled(PositionSoundHandler handler)
    {
        if (SoundTweak.DISABLE_XP_PICKUP.get())
            return handler.mute(SoundEvents.EXPERIENCE_ORB_PICKUP);

        if (SoundTweak.OLD_XP.get() && handler.compare(SoundEvents.EXPERIENCE_ORB_PICKUP))
        {
            handler.sound = SoundEvents.ITEM_PICKUP;
            handler.pitch = handler.randomSource().nextFloat() - handler.randomSource().nextFloat() * 0.1F + 0.01F;

            return true;
        }

        return false;
    }

    /**
     * Changes the stepping sounds of entities. If a sound's path does not contain "entity" and "step" within the
     * identifier, then the sound is not changed unless it is included in the ignored step listing.
     */
    private static boolean isStepHandled(PositionSoundHandler handler)
    {
        String path = handler.sound.getLocation().getPath();
        boolean isEntityStep = path.contains("entity.") && path.contains(".step");
        boolean isSingleplayer = Minecraft.getInstance().hasSingleplayerServer();

        if (!SoundTweak.OLD_STEP.get() || !isEntityStep || isSingleplayer)
            return false;

        Entity entity = null;

        for (Entity next : handler.level.entitiesForRendering())
        {
            if (next instanceof ItemEntity)
                continue;

            boolean isX = MathUtil.tolerance((int) next.getX(), (int) handler.x);
            boolean isY = MathUtil.tolerance((int) next.getY(), (int) handler.y);
            boolean isZ = MathUtil.tolerance((int) next.getZ(), (int) handler.z);

            if (isX && isY && isZ)
            {
                entity = next;
                break;
            }
        }

        if (entity == null)
            return false;

        BlockState standingOn = handler.level.getBlockState(handler.blockPos.below());

        if (isEntityStepIgnored(entity) || standingOn.isAir())
            return true;

        if (isModdedStepIgnored(entity) || !standingOn.getFluidState().isEmpty())
            return false;

        SoundType soundType = handler.getSoundTypeAt(((EntityAccess) entity).nt$getPrimaryStepSoundBlockPos(handler.blockPos.below()));

        handler.source = entity.getSoundSource();
        handler.volume = soundType.getVolume() * 0.15F;
        handler.sound = soundType.getStepSound();
        handler.pitch = soundType.getPitch();

        return true;
    }
}
