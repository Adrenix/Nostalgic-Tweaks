package mod.adrenix.nostalgic.fabric.init;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.SoundUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class NostalgicSoundInit
{
    private static final Identifier BLANK_RESOURCE = new Identifier(NostalgicTweaks.MOD_ID, SoundUtil.Key.BLANK);
    private static final SoundEvent BLANK_EVENT = new SoundEvent(BLANK_RESOURCE);

    private static final Identifier PLAYER_HURT_RESOURCE = new Identifier(NostalgicTweaks.MOD_ID, SoundUtil.Key.PLAYER_HURT);
    private static final SoundEvent PLAYER_HURT_EVENT = new SoundEvent(PLAYER_HURT_RESOURCE);

    public static void register()
    {
        SoundUtil.Event.BLANK = () -> BLANK_EVENT;
        SoundUtil.Event.PLAYER_HURT = () -> PLAYER_HURT_EVENT;

        Registry.register(Registry.SOUND_EVENT, BLANK_RESOURCE, BLANK_EVENT);
        Registry.register(Registry.SOUND_EVENT, PLAYER_HURT_RESOURCE, PLAYER_HURT_EVENT);
    }
}
