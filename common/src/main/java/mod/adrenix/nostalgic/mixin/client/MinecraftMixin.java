package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.client.screen.NostalgicPauseScreen;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.client.screen.NostalgicProgressScreen;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.duck.ILocalSwing;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public Screen screen;
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow public abstract Window getWindow();
    @Shadow protected int missTime;

    /* Injections */

    /**
     * Prevents the hand swing animation when dropping an item.
     * Controlled the swing drop tweak.
     */
    @Inject
    (
        method = "handleKeybinds",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private void NT$onDropItem(CallbackInfo callback)
    {
        ILocalSwing injector = (ILocalSwing) this.player;
        if (ModConfig.Animation.oldSwingDropping() && injector != null)
            injector.NT$setSwingBlocked(true);
    }

    /**
     * Redirects the "Saving world" generic screen to static classic progress saving screen.
     * Controlled by the old loading screen tweak.
     */
    @ModifyArg
    (
        method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;updateScreenAndTick(Lnet/minecraft/client/gui/screens/Screen;)V"
        )
    )
    private Screen NT$onSaveScreen(Screen genericScreen)
    {
        if (!ModConfig.Candy.oldLoadingScreens())
            return genericScreen;

        if (genericScreen.getClass() == GenericDirtMessageScreen.class)
        {
            NostalgicProgressScreen progressScreen = new NostalgicProgressScreen(new ProgressScreen(false));
            progressScreen.setStage(Component.translatable(LangUtil.Gui.LEVEL_SAVING));
            progressScreen.setPauseTicking(NostalgicProgressScreen.NO_PAUSES);
            return progressScreen;
        }

        return genericScreen;
    }

    /**
     * The following injections insert dimension trackers in these two level methods so the classic loading screens can
     * properly display dimension info.
     *
     * Not controlled by any tweak since this injection is used for level tracking.
     */
    @Inject(method = "setLevel", at = @At(value = "HEAD"))
    private void NT$onSetLevel(ClientLevel levelClient, CallbackInfo callback)
    {
        if (this.level != null)
            NostalgicProgressScreen.setPreviousDimension(this.level.dimension());
        NostalgicProgressScreen.setCurrentDimension(levelClient.dimension());
    }

    @Inject(method = "clearLevel()V", at = @At(value = "TAIL"))
    private void NT$onClearLevel(CallbackInfo callback)
    {
        NostalgicProgressScreen.setCurrentDimension(null);
        NostalgicProgressScreen.setPreviousDimension(null);
    }

    /**
     * Uncaps the framerate limit imposed on the title screen.
     * Controlled by the unlimited title FPS tweak.
     */
    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void NT$onGetFramerateLimit(CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Candy.uncapTitleFPS())
            callback.setReturnValue(this.getWindow().getFramerateLimit());
    }

    /**
     * Redirects the game's pause menu to our own. This override respects the debug pause input.
     * Controlled by the old pause screen layout tweak.
     */
    @ModifyArg(method = "pauseGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private Screen NT$onPauseGame(Screen vanilla)
    {
        boolean isDebug = InputConstants.isKeyDown(this.getWindow().getWindow(), 292);

        if (!ModConfig.Candy.getPauseLayout().equals(TweakVersion.PauseLayout.MODERN) && !isDebug)
            return new NostalgicPauseScreen();

        return vanilla;
    }

    /**
     * The following injections reset the miss time tracker when attacking.
     * Controlled by the disabled miss timer tweak.
     */

    @Inject(method = "continueAttack", at = @At("HEAD"))
    private void NT$onContinueAttack(boolean leftClick, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableMissTime())
            this.missTime = 0;
    }

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void NT$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.disableMissTime())
            this.missTime = 0;
    }

    /**
     * Removes the player hands busy check.
     * Controlled by the disabled miss timer tweak.
     */
    @Redirect(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    private boolean NT$onCheckBusyHands(LocalPlayer instance)
    {
        return !ModConfig.Gameplay.disableMissTime() && instance.isHandsBusy();
    }
}