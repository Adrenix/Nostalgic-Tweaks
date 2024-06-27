package mod.adrenix.nostalgic.client;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public abstract class ClientSound
{
    /* Register */

    public static final DeferredRegister<SoundEvent> DEFERRED_REGISTER = DeferredRegister.create(NostalgicTweaks.MOD_ID, Registries.SOUND_EVENT);

    /* Sounds */

    private static final ResourceLocation BLANK_LOCATION = LocateResource.mod("blank");
    private static final ResourceLocation PLAYER_HURT_LOCATION = LocateResource.mod("entity.player.hurt");
    private static final ResourceLocation MUSIC_ALPHA_LOCATION = LocateResource.mod("music.gameplay.alpha");
    private static final ResourceLocation MUSIC_MENU_BETA_LOCATION = LocateResource.mod("music.menu.beta");
    private static final ResourceLocation MUSIC_MENU_BLENDED_LOCATION = LocateResource.mod("music.menu.blended");
    private static final ResourceLocation MUSIC_CREATIVE_BETA_LOCATION = LocateResource.mod("music.creative.beta");
    private static final ResourceLocation MUSIC_CREATIVE_BLENDED_LOCATION = LocateResource.mod("music.creative.blended");

    public static final RegistrySupplier<SoundEvent> BLANK = DEFERRED_REGISTER.register(BLANK_LOCATION, () -> SoundEvent.createVariableRangeEvent(BLANK_LOCATION));
    public static final RegistrySupplier<SoundEvent> PLAYER_HURT = DEFERRED_REGISTER.register(PLAYER_HURT_LOCATION, () -> SoundEvent.createVariableRangeEvent(PLAYER_HURT_LOCATION));
    public static final RegistrySupplier<SoundEvent> MUSIC_ALPHA = DEFERRED_REGISTER.register(MUSIC_ALPHA_LOCATION, () -> SoundEvent.createVariableRangeEvent(MUSIC_ALPHA_LOCATION));
    public static final RegistrySupplier<SoundEvent> MUSIC_MENU_BETA = DEFERRED_REGISTER.register(MUSIC_MENU_BETA_LOCATION, () -> SoundEvent.createVariableRangeEvent(MUSIC_MENU_BETA_LOCATION));
    public static final RegistrySupplier<SoundEvent> MUSIC_MENU_BLENDED = DEFERRED_REGISTER.register(MUSIC_MENU_BLENDED_LOCATION, () -> SoundEvent.createVariableRangeEvent(MUSIC_MENU_BLENDED_LOCATION));
    public static final RegistrySupplier<SoundEvent> MUSIC_CREATIVE_BETA = DEFERRED_REGISTER.register(MUSIC_CREATIVE_BETA_LOCATION, () -> SoundEvent.createVariableRangeEvent(MUSIC_CREATIVE_BETA_LOCATION));
    public static final RegistrySupplier<SoundEvent> MUSIC_CREATIVE_BLENDED = DEFERRED_REGISTER.register(MUSIC_CREATIVE_BLENDED_LOCATION, () -> SoundEvent.createVariableRangeEvent(MUSIC_CREATIVE_BLENDED_LOCATION));

    /* Methods */

    /**
     * Submit our registry entries to the deferred registry. This must be invoked in an initialization block to make our
     * sounds available.
     */
    public static void register()
    {
        DEFERRED_REGISTER.register();
    }
}
