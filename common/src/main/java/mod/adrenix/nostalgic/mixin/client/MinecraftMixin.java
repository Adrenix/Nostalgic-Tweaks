package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.client.screen.ClassicProgressScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
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
    @Shadow public abstract Window getWindow();

    // Loading the config as early as possible to prevent NPEs during mixin applications.
    static
    {
        if (CommonRegistry.cache == null)
            CommonRegistry.preloadConfiguration();
    }

    /**
     * Prevents the hand swing animation when dropping an item.
     * Controlled the swing drop tweak.
     */
    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    private void NT$itemDroppingProxy(LocalPlayer player, InteractionHand hand)
    {
        if (MixinConfig.Animation.oldSwingDropping())
            return;
        player.swing(InteractionHand.MAIN_HAND);
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
        if (!MixinConfig.Candy.oldLoadingScreens())
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
        if (MixinConfig.Candy.uncapTitleFPS())
            callback.setReturnValue(this.getWindow().getFramerateLimit());
    }
}