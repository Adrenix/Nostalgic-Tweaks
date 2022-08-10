package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.IGhastAttack;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhastRenderer.class)
public abstract class GhastRendererMixin
{
    /**
     * Brings back the old ghast charging 'squishy' animation.
     * Controlled by the old ghast charging tweak.
     */
    @Inject
    (
        method = "scale(Lnet/minecraft/world/entity/monster/Ghast;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onScale(Ghast ghast, PoseStack poseStack, float partialTickTime, CallbackInfo callback)
    {
        if (!ModConfig.Animation.oldGhastCharging() || !ghast.isCharging())
            return;

        float squish = ((((IGhastAttack) ghast).getAttackCounter() + (ghast.isAlive() ? partialTickTime : 0)) + 10) / 20.0F;
        squish = Mth.clamp(squish, 0.0F, 1.0F);
        squish = 1.0F / (squish * squish * squish * squish * squish * 2.0F + 1.0F);

        float vertical = (8.0F + squish) / 2.0F;
        float horizontal = (8.0F + 1.0F / squish) / 2.0F;

        poseStack.scale(horizontal, vertical, horizontal);
        callback.cancel();
    }
}
