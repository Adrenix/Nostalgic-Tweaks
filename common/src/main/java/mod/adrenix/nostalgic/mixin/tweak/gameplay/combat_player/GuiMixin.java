package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /**
     * Disables the rendering of the "ready to attack" indicator status.
     */
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
        method = "renderCrosshair",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;)V"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
        )
    )
    private <T> T nt_combat_player$modifyCrosshairAttackIndicator(T indicatorStatus)
    {
        return GameplayTweak.DISABLE_COOLDOWN.get() ? (T) AttackIndicatorStatus.OFF : indicatorStatus;
    }
}
