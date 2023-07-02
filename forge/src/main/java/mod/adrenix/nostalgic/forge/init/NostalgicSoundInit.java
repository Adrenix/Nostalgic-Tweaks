package mod.adrenix.nostalgic.forge.init;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.SoundCommonUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This helper class registers custom sounds added by the mod.
 * The common code sound event suppliers are defined here.
 */

public abstract class NostalgicSoundInit
{
    /**
     * Creates a deferred register instance so that this mod's sounds can be added to Forge's registries.
     * We do not add sounds to vanilla's registry.
     */
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NostalgicTweaks.MOD_ID);

    /* Sounds */

    public static final RegistryObject<SoundEvent> BLANK = SOUNDS.register
    (
        SoundCommonUtil.Key.BLANK, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NostalgicTweaks.MOD_ID, SoundCommonUtil.Key.BLANK))
    );

    public static final RegistryObject<SoundEvent> PLAYER_HURT = SOUNDS.register
    (
        SoundCommonUtil.Key.PLAYER_HURT, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NostalgicTweaks.MOD_ID, SoundCommonUtil.Key.PLAYER_HURT))
    );

    /* Initialization */

    /**
     * Initializes all sounds defined in the class.
     * Both the client and server need these sound events defined.
     */
    public static void init()
    {
        SoundCommonUtil.Event.BLANK = BLANK;
        SoundCommonUtil.Event.PLAYER_HURT = PLAYER_HURT;
    }
}
