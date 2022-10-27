package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientKeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public abstract class ModEventHandler
{
    /* Key Registration Events */

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event)
    {
        // Register key that opens config while in-game
        event.register(ClientKeyMapping.CONFIG_KEY);

        // Register key that toggles the fog while in-game
        event.register(ClientKeyMapping.FOG_KEY);
    }
}
