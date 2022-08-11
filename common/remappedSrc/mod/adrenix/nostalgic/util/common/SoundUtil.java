package mod.adrenix.nostalgic.util.common;

import java.util.function.Supplier;
import net.minecraft.sound.SoundEvent;

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
