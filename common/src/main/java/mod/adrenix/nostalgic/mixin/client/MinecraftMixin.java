package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.client.screen.ClassicProgressScreen;
import mod.adrenix.nostalgic.mixin.duck.ILocalSwing;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    // Loading the config as early as possible to prevent NPEs during mixin applications.
    static
    {
        if (CommonRegistry.cache == null)
            CommonRegistry.preloadConfiguration();
    }

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
            ClassicProgressScreen progressScreen = new ClassicProgressScreen(new ProgressScreen(false));
            progressScreen.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_SAVING));
            progressScreen.setPauseTicking(ClassicProgressScreen.NO_PAUSES);
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
            ClassicProgressScreen.setPreviousDimension(this.level.dimension());
        ClassicProgressScreen.setCurrentDimension(levelClient.dimension());
    }

    @Inject(method = "clearLevel()V", at = @At(value = "TAIL"))
    private void NT$onClearLevel(CallbackInfo callback)
    {
        ClassicProgressScreen.setCurrentDimension(null);
        ClassicProgressScreen.setPreviousDimension(null);
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
     * The following injection resets the swing attack animation tracker.
     * Controlled by the old swing interrupt tweak.
     */
    @Inject(method = "startAttack", at = @At("HEAD"))
    private void NT$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Animation.oldInterruptSwing() && this.player != null)
        {
            this.player.attackAnim = 0.0F;
            this.player.swingTime = 0;
        }
    }
}