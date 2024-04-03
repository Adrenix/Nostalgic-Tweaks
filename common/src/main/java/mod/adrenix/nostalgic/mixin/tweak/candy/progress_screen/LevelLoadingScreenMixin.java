package mod.adrenix.nostalgic.mixin.tweak.candy.progress_screen;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin
{
    /**
     * Prevents the loading screen from triggering an immediate narration when the screen is removed by the old progress
     * screen tweak.
     */
    @WrapWithCondition(
        method = "removed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;triggerImmediateNarration(Z)V"
        )
    )
    private boolean nt_progress_screen$preventImmediateNarration(LevelLoadingScreen screen, boolean immediate)
    {
        return !CandyTweak.OLD_PROGRESS_SCREEN.get();
    }
}
