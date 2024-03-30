package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Brings back the old nether shading by tricking the client level into thinking it is shading a non-constant
     * ambient light dimension.
     */
    @ModifyExpressionValue(
        method = "getShade",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;constantAmbientLight()Z"
        )
    )
    private boolean nt_world_lighting$modifyNetherShading(boolean isConstantAmbientLight)
    {
        if (CandyTweak.OLD_NETHER_LIGHTING.get() && ((ClientLevel) (Object) this).dimension() == Level.NETHER)
            return false;

        return isConstantAmbientLight;
    }
}
