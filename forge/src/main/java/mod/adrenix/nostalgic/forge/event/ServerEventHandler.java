package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.SoundCommonUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.DEDICATED_SERVER
)
public abstract class ServerEventHandler
{
    /**
     * Disables and plays various sounds based on tweak states.
     */
    @SubscribeEvent
    public static void onPlaySoundAtPosition(PlayLevelSoundEvent.AtPosition event)
    {
        if (event.getSound() != null)
        {
            double x = event.getPosition().x;
            double y = event.getPosition().y;
            double z = event.getPosition().z;

            if (SoundCommonUtil.isSoundAtPositionHandled(event.getLevel(), x, y, z, event.getSound(), event.getLevel()::playLocalSound))
                event.setCanceled(true);
        }
    }
}
