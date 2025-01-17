package mod.adrenix.nostalgic.mixin.tweak.animation.boat;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import net.minecraft.client.model.AbstractBoatModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBoatModel.class)
public abstract class AbstractBoatModelMixin
{
    /* Shadows & Unique */

    @Shadow @Final private ModelPart leftPaddle;
    @Shadow @Final private ModelPart rightPaddle;
    @Unique private final FlagHolder nt$hideLeftPaddle = FlagHolder.off();
    @Unique private final FlagHolder nt$hideRightPaddle = FlagHolder.off();

    /* Injections */

    /**
     * Prevents the boat rowing animation.
     */
    @WrapWithCondition(
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/AbstractBoatModel;animatePaddle(FILnet/minecraft/client/model/geom/ModelPart;)V"
        )
    )
    private boolean nt_animation_boat$shouldShowPaddleAnimation(float rowingTime, int side, ModelPart part)
    {
        if (AnimationTweak.HIDE_BOAT_ROWING.get())
        {
            if (this.nt$hideLeftPaddle.ifDisabledThenEnable())
                this.leftPaddle.visible = false;

            if (this.nt$hideRightPaddle.ifDisabledThenEnable())
                this.rightPaddle.visible = false;

            return false;
        }
        else
        {
            if (this.nt$hideLeftPaddle.ifEnabledThenDisable())
                this.leftPaddle.visible = true;

            if (this.nt$hideRightPaddle.ifEnabledThenDisable())
                this.rightPaddle.visible = true;
        }

        return true;
    }
}
