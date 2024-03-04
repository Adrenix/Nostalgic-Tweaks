package mod.adrenix.nostalgic.util.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;

public abstract class GameUtil
{
    /**
     * @return The current version of the game.
     */
    public static String getVersion()
    {
        return SharedConstants.getCurrentVersion().getName();
    }

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
