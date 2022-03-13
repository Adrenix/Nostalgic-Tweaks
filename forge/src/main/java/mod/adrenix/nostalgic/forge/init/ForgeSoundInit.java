package mod.adrenix.nostalgic.forge.init;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.SoundUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public abstract class ForgeSoundInit
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NostalgicTweaks.MOD_ID);

    public static final RegistryObject<SoundEvent> BLANK = SOUNDS.register(SoundUtil.Key.BLANK, () ->
        new SoundEvent(new ResourceLocation(NostalgicTweaks.MOD_ID, SoundUtil.Key.BLANK))
    );

    public static final RegistryObject<SoundEvent> PLAYER_HURT = SOUNDS.register(SoundUtil.Key.PLAYER_HURT, () ->
        new SoundEvent(new ResourceLocation(NostalgicTweaks.MOD_ID, SoundUtil.Key.PLAYER_HURT))
    );

    static
    {
        SoundUtil.Event.BLANK = BLANK;
        SoundUtil.Event.PLAYER_HURT = PLAYER_HURT;
    }
}
