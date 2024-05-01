package mod.adrenix.nostalgic.mixin.util.animation;

import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
import mod.adrenix.nostalgic.util.common.array.CycleIndex;
import net.minecraft.world.entity.player.Player;

/**
 * This utility class is used only by the client.
 */
public abstract class BobbingMixinHelper
{
    /* Fields */

    private static final float[] ROTATION_VALUES = new float[] { 0.0F, 0.0F, 0.0F, 0.0F };
    private static final CycleIndex CYCLE_ROTATION = new CycleIndex(0, 3, 0, true);

    /* Methods */

    /**
     * Sets the camera pitch using previously calculated values.
     *
     * @param player The {@link Player} that will be cast with {@link CameraPitching}.
     */
    public static void setCameraPitch(Player player)
    {
        CameraPitching cameraPitching = (CameraPitching) player;
        cameraPitching.nt$setPrevCameraPitch(cameraPitching.nt$getCameraPitch());

        double deltaY = player.getDeltaMovement().y;
        float rotation = (float) (Math.atan(-deltaY * 0.20000000298023224D) * 15.0D);

        ROTATION_VALUES[CYCLE_ROTATION.getAndCycle()] = rotation;

        // Fixes a bug that occurs when standing on a slime block (and prevents other cyclical scenarios)
        boolean isCycledOnce = false;
        boolean isCycledTwice = false;
        boolean isAtRest = ROTATION_VALUES[0] == ROTATION_VALUES[1] && ROTATION_VALUES[0] == ROTATION_VALUES[2] && ROTATION_VALUES[0] == ROTATION_VALUES[3];

        for (int i = 1; i < ROTATION_VALUES.length; i++)
        {
            if (isAtRest)
                break;

            boolean isCopied = ROTATION_VALUES[0] == ROTATION_VALUES[i];
            boolean isRepeated = ROTATION_VALUES[1] == ROTATION_VALUES[2] && ROTATION_VALUES[1] == ROTATION_VALUES[3];

            if (isCopied || isRepeated)
            {
                isCycledOnce = true;
                break;
            }
        }

        for (int i = 2; i < ROTATION_VALUES.length; i++)
        {
            if (isAtRest)
                break;

            boolean isCopied = ROTATION_VALUES[1] == ROTATION_VALUES[i];
            boolean isRepeated = ROTATION_VALUES[0] == ROTATION_VALUES[2] && ROTATION_VALUES[0] == ROTATION_VALUES[3];

            if (isCopied || isRepeated)
            {
                isCycledTwice = true;
                break;
            }
        }

        // Apply rotation to camera pitching
        if (player.onGround() || player.getHealth() <= 0.0F || (isCycledOnce && isCycledTwice))
            rotation = 0.0F;

        float current = cameraPitching.nt$getCameraPitch();

        cameraPitching.nt$setCameraPitch(current + ((rotation - current) * 0.8F));
    }
}
