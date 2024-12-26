package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientAdvancements.class)
public abstract class ClientAdvancementsMixin
{
    /**
     * Prevent advancement toasts from appearing after making an achievement.
     */
    @ModifyExpressionValue(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/DisplayInfo;shouldShowToast()Z"
        )
    )
    public boolean nt_old_hud$shouldShowAdvancementToast(boolean shouldShowToast)
    {
        return !CandyTweak.HIDE_ADVANCEMENT_TOASTS.get() && shouldShowToast;
    }
}
