package mod.adrenix.nostalgic.mixin.tweak.candy.world_fog;

import mod.adrenix.nostalgic.mixin.util.candy.world.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Tracks the red sky color for void fog and applies void fog changes if needed.
     */
    @ModifyArg(
        index = 0,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double nt_world_fog$onSetSkyColorRed(double red)
    {
        if (ModTweak.ENABLED.get())
            VoidFogRenderer.setSkyRed((float) red);
        else
            return red;

        return VoidFogRenderer.isRendering() ? VoidFogRenderer.getSkyRed() : red;
    }

    /**
     * Tracks the green sky color for void fog and applies void fog changes if needed.
     */
    @ModifyArg(
        index = 1,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double nt_world_fog$onSetSkyColorGreen(double green)
    {
        if (ModTweak.ENABLED.get())
            VoidFogRenderer.setSkyGreen((float) green);
        else
            return green;

        return VoidFogRenderer.isRendering() ? VoidFogRenderer.getSkyGreen() : green;
    }

    /**
     * Tracks the blue sky color for void fog and applies void fog changes if needed.
     */
    @ModifyArg(
        index = 2,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double nt_world_fog$onSetSkyColorBlue(double blue)
    {
        if (ModTweak.ENABLED.get())
            VoidFogRenderer.setSkyBlue((float) blue);
        else
            return blue;

        return VoidFogRenderer.isRendering() ? VoidFogRenderer.getSkyBlue() : blue;
    }

    /**
     * Adds void fog particles to the client level if the correct conditions are met.
     */
    @Inject(
        method = "doAnimateTick",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
        )
    )
    private void nt_world_fog$onAddBiomeParticles(int posX, int posY, int posZ, int range, RandomSource randomSource, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            VoidFogRenderer.addParticles(randomSource);
    }
}
