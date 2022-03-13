package mod.adrenix.nostalgic.mixin.client;

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

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow @Nullable public Screen screen;
    @Shadow @Nullable public ClientLevel level;

    /**
     * Prevents the hand swing animation when dropping an item.
     * Controlled the swing drop toggle.
     */
    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    protected void itemDroppingProxy(LocalPlayer player, InteractionHand hand)
    {
        if (MixinConfig.Animation.oldSwingDropping())
            return;
        player.swing(InteractionHand.MAIN_HAND);
    }

    /**
     * Redirects the "Saving world" generic screen to static classic progress saving screen.
     * Controlled by the old loading screen toggle.
     */
    @ModifyArg(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateScreenAndTick(Lnet/minecraft/client/gui/screens/Screen;)V"))
    protected Screen onSaveScreen(Screen genericScreen)
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
     * Injects dimension trackers in these two level methods so the classic loading screens can properly display dimension info.
     * Not controlled by any toggle since this injection is used for level tracking.
     */
    @Inject(method = "setLevel", at = @At(value = "HEAD"))
    protected void onSetLevel(ClientLevel levelClient, CallbackInfo callback)
    {
        if (this.level != null)
            ClassicProgressScreen.setPreviousDimension(this.level.dimension());
        ClassicProgressScreen.setCurrentDimension(levelClient.dimension());
    }

    @Inject(method = "clearLevel()V", at = @At(value = "TAIL"))
    protected void onClearLevel(CallbackInfo callback)
    {
        ClassicProgressScreen.setCurrentDimension(null);
        ClassicProgressScreen.setPreviousDimension(null);
    }
}