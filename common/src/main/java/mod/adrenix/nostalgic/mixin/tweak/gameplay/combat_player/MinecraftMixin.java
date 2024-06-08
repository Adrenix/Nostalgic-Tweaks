package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow protected int missTime;

    /* Injections */

    /**
     * Resets the miss time tracker when starting an attack.
     */
    @Inject(
        method = "startAttack",
        at = @At("HEAD")
    )
    private void nt_combat_player$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        if (GameplayTweak.DISABLE_MISS_TIMER.get())
            this.missTime = 0;
    }

    /**
     * Resets the miss time tracker when continuing an attack.
     */
    @Inject(
        method = "continueAttack",
        at = @At("HEAD")
    )
    private void nt_combat_player$onContinueAttack(boolean leftClick, CallbackInfo callback)
    {
        if (GameplayTweak.DISABLE_MISS_TIMER.get())
            this.missTime = 0;
    }
}
