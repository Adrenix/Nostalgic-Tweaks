package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.Tesselator;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.SwingConfig;
import mod.adrenix.nostalgic.client.screen.NostalgicPauseScreen;
import mod.adrenix.nostalgic.client.screen.NostalgicProgressScreen;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.duck.SwingBlocker;
import mod.adrenix.nostalgic.util.client.AnimationUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.client.SwingType;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public Screen screen;
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public HitResult hitResult;
    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Final private Window window;
    @Shadow @Final public Options options;
    @Shadow public abstract Window getWindow();
    @Shadow protected int missTime;

    /* Injections */

    /**
     * Changes the game's OS window title.
     * Controlled by various game window title tweaks.
     */
    @Inject(method = "updateTitle", at = @At("RETURN"))
    private void NT$onUpdateTitle(CallbackInfo callback)
    {
        if (!ModConfig.Candy.enableWindowTitle())
            return;

        if (ModConfig.Candy.matchVersionOverlay())
            this.window.setTitle(ModConfig.Candy.getOverlayText().replaceAll("§.", ""));
        else
            this.window.setTitle(ModConfig.Candy.getWindowTitle().replaceAll("§.", ""));
    }

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
        SwingBlocker injector = (SwingBlocker) this.player;
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

    /**
     * If the player is connected to an N.T verified world, then this allows left-clicking to interact with some blocks.
     * Controlled by various left click block tweaks.
     */
    @Inject
    (
        method = "startAttack",
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
        )
    )
    private void NT$onLeftClickBlock(CallbackInfoReturnable<Boolean> callback)
    {
        BlockHitResult result = (BlockHitResult) this.hitResult;
        boolean isNull = this.level == null || this.player == null || this.gameMode == null || result == null;

        if (!NostalgicTweaks.isNetworkVerified() || isNull || this.player.isCreative() || this.player.isShiftKeyDown())
            return;

        BlockState state = this.level.getBlockState(result.getBlockPos());
        Block block = state.getBlock();
        boolean isUsing = false;

        if (ModConfig.Gameplay.leftClickDoor())
        {
            if (block instanceof DoorBlock || block instanceof TrapDoorBlock || block instanceof FenceGateBlock)
                isUsing = true;
        }

        if (ModConfig.Gameplay.leftClickLever())
        {
            if (block instanceof LeverBlock)
                isUsing = true;
        }

        if (ModConfig.Gameplay.leftClickButton())
        {
            if (block instanceof ButtonBlock)
                isUsing = true;
        }

        if (isUsing)
            this.gameMode.useItemOn(this.player, InteractionHand.MAIN_HAND, result);
    }

    /**
     * The following injection resets the miss time tracker when staring an attack and resets the swing attack
     * animation tracker.
     *
     * Both changes are controlled respectively by the disabled miss timer tweak and old swing interrupt tweak.
     */
    @Inject(method = "startAttack", at = @At("HEAD"))
    private void NT$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        AnimationUtil.swingType = SwingType.LEFT_CLICK;

        if (ModConfig.Animation.oldInterruptSwing() && this.player != null)
        {
            this.player.attackAnim = 0.0F;
            this.player.swingTime = 0;
        }

        if (ModConfig.Gameplay.disableMissTime())
            this.missTime = 0;
    }

    /**
     * Sets the animation utility swing type tracker field to right-click when the player uses an item.
     */
    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void NT$onStartUseItem(CallbackInfo callback)
    {
        AnimationUtil.swingType = SwingType.RIGHT_CLICK;
    }

    /**
     * Sets the animation utility swing type back to left if left-click speed on right-click interact is enabled.
     * The classic swing tweak being enabled will prevent this.
     */
    @Inject
    (
        method = "startUseItem",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")),
        at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V")
    )
    private void NT$onStartUseItemOn(CallbackInfo callback)
    {
        if (SwingConfig.isLeftSpeedOnBlockInteract() && !ModConfig.Animation.oldClassicSwing())
            AnimationUtil.swingType = SwingType.LEFT_CLICK;
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

    /**
     * Overrides the logic for rendering the FPS pie.
     * Controlled by the old debug screen and debug pie tweak.
     */
    @Inject(method = "shouldRenderFpsPie", at = @At("HEAD"), cancellable = true)
    private void NT$onShouldRenderFpsPie(CallbackInfoReturnable<Boolean> callback)
    {
        boolean isDebugging = !ModConfig.Candy.getDebugScreen().equals(TweakVersion.Generic.MODERN);

        if (ModConfig.Candy.displayPieChart() && isDebugging && this.options.renderDebug && !this.options.hideGui)
            callback.setReturnValue(true);
    }

    /**
     * Adds a semitransparent black background to the FPS pie chart.
     * Controlled by the old FPS pie background tweak.
     */
    @Inject
    (
        method = "renderFpsMeter",
        at = @At
        (
            shift = At.Shift.AFTER,
            ordinal = 0,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"
        )
    )
    private void NT$onRenderFpsMeter(GuiGraphics graphics, ProfileResults profilerResults, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldPieBackground())
            return;

        int color = 0xCF000000;
        int x = this.window.getWidth() - 160 - 10;
        int y = this.window.getHeight() - 320;
        RenderUtil.fill(Tesselator.getInstance().getBuilder(), x - 176.0F, x + 176.0F, y - 96.0f - 16.0f, y + 320.0F, color);
    }
}