package mod.adrenix.nostalgic.mixin.tweak.animation.ghast_charge;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.util.animation.GhastChargeMixinHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GhastRenderer.class)
public abstract class GhastRendererMixin
{
    /**
     * Prevents the default Ghast animation and brings back the old "squishy" animation.
     */
    @WrapWithCondition(
        method = "scale(Lnet/minecraft/world/entity/monster/Ghast;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
        )
    )
    private boolean nt_ghast_charge$shouldGhastScale(PoseStack poseStack, float x, float y, float z, Ghast ghast, PoseStack arg2, float partialTick)
    {
        if (!AnimationTweak.OLD_GHAST_CHARGING.get() || !ghast.isCharging())
            return true;

        GhastChargeMixinHelper.applySquish(ghast, poseStack, partialTick);

        return false;
    }
}
