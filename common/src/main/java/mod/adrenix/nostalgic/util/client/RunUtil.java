package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;

/**
 * Config Runnables:
 *
 * Some tweaks require more work for a change to take place.
 *
 * For example, the star buffer needs to be redone if the old stars tweak is toggled.
 * A runnable is defined in the LevelRendererMixin class that provides instructions on what to do when a change is
 * made to the tweak.
 *
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class RunUtil
{
    // On-save Runnables
    public static final ArrayList<Runnable> onSave = new ArrayList<>();

    // Reload States

    public static boolean reloadChunks = false;
    public static boolean reloadResources = false;

    static
    {
        onSave.add(() ->
        {
            Minecraft minecraft = Minecraft.getInstance();

            if (reloadResources)
            {
                reloadResources = false;
                reloadChunks = false;
                minecraft.reloadResourcePacks();
            }
            else if (reloadChunks)
            {
                reloadChunks = false;
                minecraft.levelRenderer.allChanged();
            }
        });
    }
}
