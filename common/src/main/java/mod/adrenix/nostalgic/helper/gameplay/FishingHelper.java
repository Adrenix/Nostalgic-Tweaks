package mod.adrenix.nostalgic.helper.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

/**
 * This utility class is used by both the client and server.
 */
public abstract class FishingHelper
{
    /**
     * Get the amount to bob the bobber by.
     *
     * @param bobber The {@link Entity} instance of the fishing rod bobber.
     * @return The amount to bob.
     */
    public static double getBobbingAmount(Entity bobber)
    {
        Vec3 deltaMovement = bobber.getDeltaMovement();
        BlockPos blockPos = bobber.blockPosition();
        FluidState fluidState = bobber.level().getFluidState(blockPos);
        float fluidOffset = 0.0F;

        if (fluidState.is(FluidTags.WATER))
            fluidOffset = fluidState.getHeight(bobber.level(), blockPos);

        double yOffset = bobber.getY() + deltaMovement.y - (double) blockPos.getY() - (double) fluidOffset;

        if (Math.abs(yOffset) < 0.01D)
            yOffset += Math.signum(yOffset) * 0.1D;

        return deltaMovement.y - yOffset;
    }
}
