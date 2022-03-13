package mod.adrenix.nostalgic.util;

import net.minecraft.sounds.SoundEvent;
import java.util.function.Supplier;

public abstract class SoundUtil
{
    public static class Key
    {
        public static final String PLAYER_HURT = "entity.player.hurt";
        public static final String BLANK = "blank";
    }

    public static class Event
    {
        public static Supplier<SoundEvent> PLAYER_HURT;
        public static Supplier<SoundEvent> BLANK;
    }
}
