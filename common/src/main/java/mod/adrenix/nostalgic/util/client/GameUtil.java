package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Minecraft;

public abstract class GameUtil
{
    /**
     * Check if the current game mode is creative.
     *
     * @return Whether Minecraft is in creative mode.
     */
    public static boolean isCreativeMode()
    {
        return Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasInfiniteItems();
    }

    /**
     * Check if the current game mode is not creative.
     *
     * @return Whether Minecraft is not in creative mode.
     */
    public static boolean isNotCreativeMode()
    {
        return !isCreativeMode();
    }
}
