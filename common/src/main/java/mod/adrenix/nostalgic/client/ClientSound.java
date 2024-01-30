package mod.adrenix.nostalgic.client;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public abstract class ClientSound
{
    /* Register */

    public static final DeferredRegister<SoundEvent> DEFERRED_REGISTER = DeferredRegister.create(NostalgicTweaks.MOD_ID, Registries.SOUND_EVENT);

    /* Sounds */

    private static final ResourceLocation BLANK_LOCATION = new ResourceLocation(NostalgicTweaks.MOD_ID, "blank");
    private static final ResourceLocation PLAYER_HURT_LOCATION = new ResourceLocation(NostalgicTweaks.MOD_ID, "entity.player.hurt");

    public static final RegistrySupplier<SoundEvent> BLANK = DEFERRED_REGISTER.register(BLANK_LOCATION, () -> SoundEvent.createVariableRangeEvent(BLANK_LOCATION));
    public static final RegistrySupplier<SoundEvent> PLAYER_HURT = DEFERRED_REGISTER.register(PLAYER_HURT_LOCATION, () -> SoundEvent.createVariableRangeEvent(PLAYER_HURT_LOCATION));

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
