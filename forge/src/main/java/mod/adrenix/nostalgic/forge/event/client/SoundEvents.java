package mod.adrenix.nostalgic.forge.event.client;

import mod.adrenix.nostalgic.util.client.SoundClientUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.event.PlayLevelSoundEvent;

public abstract class SoundEvents
{
    /**
     * Disables and plays various sounds based on tweak states.
     */
    public static void changeSoundAtPosition(PlayLevelSoundEvent.AtPosition event)
    {
        if (event.getLevel() instanceof ClientLevel level && event.getSound() != null)
        {
            double x = event.getPosition().x;
            double y = event.getPosition().y;
            double z = event.getPosition().z;

            if (SoundClientUtil.isSoundAtPositionHandled(level, x, y, z, event.getSound(), level::playLocalSound))
                event.setCanceled(true);
        }
    }
}
