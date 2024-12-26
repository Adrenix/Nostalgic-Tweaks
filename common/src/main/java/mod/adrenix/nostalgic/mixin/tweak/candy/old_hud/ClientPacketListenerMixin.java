package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    /**
     * Prevents recipe notification toasts from appearing when unlocking a new recipe.
     */
    @ModifyExpressionValue(
        method = "method_34011",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/Recipe;showNotification()Z"
        )
    )
    private boolean nt_old_hud$shouldShowRecipeToast(boolean showNotification)
    {
        return !CandyTweak.HIDE_RECIPE_TOASTS.get() && showNotification;
    }
}
