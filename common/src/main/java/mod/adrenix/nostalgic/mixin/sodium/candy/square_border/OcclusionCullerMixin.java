package mod.adrenix.nostalgic.mixin.sodium.candy.square_border;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
import net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform;
import mod.adrenix.nostalgic.helper.candy.level.ServerLevelHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OcclusionCuller.class)
public abstract class OcclusionCullerMixin
{
    /* Shadows */

    @Shadow
    private static int nearestToZero(int min, int max)
    {
        return 0;
    }

    /* Injections */

    /**
     * Allows the old square border tweak to work with Sodium's occlusion culler.
     */
    @ModifyReturnValue(
        remap = false,
        method = "isWithinRenderDistance",
        at = @At(value = "RETURN")
    )
    private static boolean nt_sodium_square_border$isWithinRenderDistance(boolean isWithinRenderDistance, CameraTransform camera, RenderSection section, float maxDistance)
    {
        if (!CandyTweak.OLD_SQUARE_BORDER.get())
            return isWithinRenderDistance;

        int secX = section.getOriginX() - camera.intX;
        int secZ = section.getOriginZ() - camera.intZ;
        int chunkX = nearestToZero(secX, secX + 16) - (int) camera.fracX;
        int chunkZ = nearestToZero(secZ, secZ + 16) - (int) camera.fracZ;

        return ServerLevelHelper.isChunkInRange(chunkX, chunkZ, secX, secZ, (int) maxDistance);
    }
}
