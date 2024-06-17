package mod.adrenix.nostalgic.mixin.tweak.candy.death_screen;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen
{
    /* Fake Constructor */

    private DeathScreenMixin(Component title)
    {
        super(title);
    }

    /* Shadow */

    @Shadow private Component deathScore;
    @Shadow @Final @Mutable private Component causeOfDeath;

    /* Injections */

    /**
     * Changes the cause of death to {@code null}.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_death_screen$onSetCauseOfDeath(Component causeOfDeath, boolean hardcore, CallbackInfo callback)
    {
        if (CandyTweak.OLD_DEATH_SCREEN.get() && CandyTweak.HIDE_CAUSE_OF_DEATH.get())
            this.causeOfDeath = null;
    }

    /**
     * Changes the title to the old "Game over!" text.
     */
    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private static String nt_death_screen$onSetTitle(String key)
    {
        return CandyTweak.OLD_DEATH_SCREEN.get() ? Lang.Death.GAME_OVER.langKey() : key;
    }

    /**
     * Changes the exit button to the old "Title menu" text.
     */
    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private String nt_death_screen$onSetExitText(String key)
    {
        if (CandyTweak.OLD_DEATH_SCREEN.get() && key.equals("deathScreen.titleScreen"))
            return Lang.Death.TITLE_MENU.langKey();

        return key;
    }

    /**
     * Changes the death score back to the "Score: &e0" text.
     */
    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
        )
    )
    private Component nt_death_screen$onSetScoreText(Component text)
    {
        if (CandyTweak.OLD_DEATH_SCREEN.get() && CandyTweak.OLD_DEATH_SCORE.get() && text.equals(this.deathScore))
            return Lang.Death.SCORE.get();

        return text;
    }
}
