package mod.adrenix.nostalgic.util.common;

import net.minecraft.sounds.SoundEvent;
import java.util.function.Supplier;

/**
 * Each mod loader handles sound events differently.
 * This utility acts as a helper to bridge to the two mod loaders so code does not to be duplicated.
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
}
