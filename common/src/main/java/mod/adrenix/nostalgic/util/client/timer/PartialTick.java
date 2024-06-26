package mod.adrenix.nostalgic.util.client.timer;

import net.minecraft.client.Minecraft;

public abstract class PartialTick
{
    /**
     * @return The normalized progress between two ticks [0.0F, 1.0F].
     */
    public static float get()
    {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }
}
