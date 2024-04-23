package mod.adrenix.nostalgic.mixin.tweak.candy.world_sky;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Adjusts the darkness of the sky color at night to match the old star colors.
     */
    @ModifyArg(
        index = 1,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
        )
    )
    private float nt_world_sky$setMinNightSkyColor(float min)
    {
        return switch (CandyTweak.OLD_STARS.get())
        {
            case ALPHA, BETA -> 0.005F;
            default -> min;
        };
    }
}
