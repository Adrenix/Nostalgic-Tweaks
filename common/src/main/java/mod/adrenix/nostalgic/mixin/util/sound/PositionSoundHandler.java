package mod.adrenix.nostalgic.mixin.util.sound;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class PositionSoundHandler
{
    /* Builder */

    /**
     * Create a new sound handler at the given coordinates.
     *
     * @param level  The {@link ClientLevel} to get block data from.
     * @param x      The x-coordinate of the sound.
     * @param y      The y-coordinate of the sound.
     * @param z      The z-coordinate of the sound.
     * @param sound  The {@link SoundEvent} being played at the given coordinates.
     * @param source The {@link SoundSource} of the sound.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     * @return A new {@link PositionSoundHandler} instance.
     */
    static PositionSoundHandler create(ClientLevel level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch)
    {
        return new PositionSoundHandler(level, x, y, z, sound, source, volume, pitch);
    }

    /* Fields */

    final double x;
    final double y;
    final double z;
    float pitch;
    float volume;
    final ClientLevel level;
    final BlockState blockState;
    final BlockPos blockPos;
    SoundEvent sound;
    SoundSource source;
    boolean handled;

    /* Constructor */

    private PositionSoundHandler(ClientLevel level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch)
    {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
        this.source = source;
        this.blockPos = BlockPos.containing(x, y, z);
        this.blockState = level.getBlockState(this.blockPos);
    }

    /* Methods */

    /**
     * @return The {@link SoundEvent} managed by this handler.
     */
    public SoundEvent getSound()
    {
        return this.sound;
    }

    /**
     * @return The {@link SoundSource} managed by this handler.
     */
    public SoundSource getSource()
    {
        return this.source;
    }

    /**
     * @return The pitch of the sound managed by this handler.
     */
    public float getPitch()
    {
        return this.pitch;
    }

    /**
     * @return The volume of the sound managed by this handler.
     */
    public float getVolume()
    {
        return this.volume;
    }

    /**
     * @return The {@link RandomSource} instance from the {@link ClientLevel}.
     */
    RandomSource randomSource()
    {
        return this.level.random;
    }

    /**
     * Get a sound type from the given block position.
     *
     * @param blockPos The {@link BlockPos} to get block state data from.
     * @return The {@link SoundType} at the given block position.
     */
    SoundType getSoundTypeAt(BlockPos blockPos)
    {
        return this.level.getBlockState(blockPos).getSoundType();
    }

    /**
     * Apply changes to this handler if nothing has been handled yet.
     *
     * @param handler A {@link Predicate} that yields if changes were made to this handler.
     */
    void apply(Predicate<PositionSoundHandler> handler)
    {
        if (this.handled)
            return;

        this.handled = handler.test(this);
    }

    /**
     * Check if the current sound matches any of the given sounds.
     *
     * @param to A varargs of {@link SoundEvent} to compare against.
     * @return Whether any of the given sounds compared to the current sound.
     */
    boolean compare(SoundEvent... to)
    {
        for (SoundEvent event : to)
        {
            if (event.equals(this.sound))
                return true;
        }

        return false;
    }

    /**
     * Check if the current sound matches any of the given sounds, and if so, mutes the volume of this handler. If no
     * sounds are given, then the volume is muted and {@code true} is returned.
     *
     * @param sounds A varargs of {@link SoundEvent} to mute.
     * @return Whether the handler was muted.
     */
    boolean mute(SoundEvent... sounds)
    {
        if (sounds.length == 0 || compare(sounds))
        {
            this.volume = 0.0F;
            return true;
        }

        return false;
    }
}
