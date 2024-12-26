package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialSteps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tutorial.class)
public abstract class TutorialMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;

    /* Injections */

    /**
     * Prevents the game from running the tutorial which prevents the tutorial toasts from appearing.
     */
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void nt_old_hud$onTutorialTick(CallbackInfo callback)
    {
        if (CandyTweak.HIDE_TUTORIAL_TOASTS.get() && this.minecraft.options.tutorialStep != TutorialSteps.NONE)
        {
            this.minecraft.options.tutorialStep = TutorialSteps.NONE;
            this.minecraft.options.save();
        }
    }
}
