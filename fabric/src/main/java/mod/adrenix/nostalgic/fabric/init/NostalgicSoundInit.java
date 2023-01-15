package mod.adrenix.nostalgic.fabric.init;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.SoundUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * This helper class registers custom sounds added by the mod.
 * The common code sound event suppliers are defined here.
 */

public abstract class NostalgicSoundInit
{
    /* Sounds */

    private static final ResourceLocation BLANK_RESOURCE = new ResourceLocation(NostalgicTweaks.MOD_ID, SoundUtil.Key.BLANK);
    private static final SoundEvent BLANK_EVENT = SoundEvent.createVariableRangeEvent(BLANK_RESOURCE);

    private static final ResourceLocation PLAYER_HURT_RESOURCE = new ResourceLocation(NostalgicTweaks.MOD_ID, SoundUtil.Key.PLAYER_HURT);
    private static final SoundEvent PLAYER_HURT_EVENT = SoundEvent.createVariableRangeEvent(PLAYER_HURT_RESOURCE);

    /* Registration */

    /**
     * Registers all sounds defined in this class.
     * Both the client and server need these sound events defined.
     */
    public static void register()
    {
        SoundUtil.Event.BLANK = () -> BLANK_EVENT;
        SoundUtil.Event.PLAYER_HURT = () -> PLAYER_HURT_EVENT;

        Registry.register(BuiltInRegistries.SOUND_EVENT, BLANK_RESOURCE, BLANK_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_HURT_RESOURCE, PLAYER_HURT_EVENT);
    }
}
