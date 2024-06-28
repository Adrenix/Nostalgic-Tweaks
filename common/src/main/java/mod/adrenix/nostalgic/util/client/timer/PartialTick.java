package mod.adrenix.nostalgic.util.client.timer;

import net.minecraft.client.Minecraft;

public abstract class PartialTick
{
    /**
     * @return Framerate based normalized progress between two ticks [0.0F, 1.0F].
     */
    public static float get()
    {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    /**
     * @return Realtime based normalized progress between two ticks [0.0F, 1.0F].
     */
    public static float realtime()
    {
        return Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
    }
}
