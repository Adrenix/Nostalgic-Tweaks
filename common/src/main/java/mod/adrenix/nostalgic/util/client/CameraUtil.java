package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;

public abstract class CameraUtil
{
    /**
     * @return The game renderer's main {@link Camera} instance.
     */
    public static Camera get()
    {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    /**
     * @return Whether the camera is in first-person mode.
     */
    public static boolean isFirstPerson()
    {
        return Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    /**
     * Check if the camera is in a fluid.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether the camera entity is in a fluid.
     */
    public static boolean isInFluid(Camera camera)
    {
        return camera.getFluidInCamera() != FogType.NONE;
    }

    /**
     * Check if the camera is currently blinded.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether the camera entity has the blindness effect applied.
     */
    public static boolean isBlind(Camera camera)
    {
        if (camera.getEntity() instanceof LivingEntity entity)
            return entity.hasEffect(MobEffects.BLINDNESS);

        return false;
    }

    /**
     * Check if the camera can see the sky.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether the sky is visible to the camera.
     */
    public static boolean canSeeSky(Camera camera)
    {
        return camera.getEntity().level().canSeeSky(camera.getBlockPosition());
    }

    /**
     * Checks if the current fog is "thick" based on game context.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether the game considers the player to be surrounded by fog.
     */
    public static boolean isFoggy(Camera camera)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return false;

        int x = Mth.floor(camera.getPosition().x());
        int y = Mth.floor(camera.getPosition().y());

        return level.effects().isFoggyAt(x, y) || minecraft.gui.getBossOverlay().shouldCreateWorldFog();
    }
}
