package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    /**
     * Prevents recipe notification toasts from appearing when unlocking a new recipe.
     */
    @WrapWithCondition(
        method = "handleRecipeBookAdd",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/toasts/RecipeToast;addOrUpdate(Lnet/minecraft/client/gui/components/toasts/ToastManager;Lnet/minecraft/world/item/crafting/display/RecipeDisplay;)V"
        )
    )
    private boolean nt_old_hud$shouldShowRecipeToast(ToastManager toastManager, RecipeDisplay recipeDisplay)
    {
        return !CandyTweak.HIDE_RECIPE_TOASTS.get();
    }
}
