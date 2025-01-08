package mod.adrenix.nostalgic.mixin.tweak.gameplay.stamina_sprint;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaHelper;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin
{
    /**
     * Ensures the local player cannot start sprinting if they are "exhausted" by the stamina sprint system. The server
     * will attempt to enforce this where possible.
     */
    @ModifyReturnValue(
        method = "canStartSprinting",
        at = @At("RETURN")
    )
    private boolean nt_stamina_sprint$modifyCanStartSprinting(boolean canSprint)
    {
        if (StaminaHelper.get((LocalPlayer) (Object) this).isExhausted())
            return false;

        return canSprint;
    }
}
