package mod.adrenix.nostalgic.mixin.client;

import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.SwingBlocker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements SwingBlocker
{
    /* Dummy Constructor */

    private LocalPlayerMixin(ClientLevel level, GameProfile profile) { super(level, profile); }

    /* Shadows */

    @Shadow private boolean crouching;
    @Shadow private boolean startedUsingItem;
    @Shadow public abstract boolean isShiftKeyDown();

    /* Swing Blocker Implementation */

    @Unique private boolean NT$swingBlocked = false;
    @Override public void NT$setSwingBlocked(boolean state) { this.NT$swingBlocked = state; }
    @Override public boolean NT$isSwingBlocked() { return this.NT$swingBlocked; }

    /* Injections */

    /**
     * Blocks the client side rendering of the swinging animation, but still sends the swing packet to the server.
     * Controlled by the old swing dropping tweak.
     */
    @Redirect(method = "swing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    private void NT$onSwing(AbstractClientPlayer instance, InteractionHand hand)
    {
        if (!this.NT$isSwingBlocked())
            super.swing(hand);
    }

    /**
     * Blocks the sending of the server bound swing packet if the old swing dropping tweak is enabled and the client
     * is connected to a world with Nostalgic Tweaks installed.
     *
     * Controlled by the old swing dropping tweak and a verified network connection.
     */
    @Inject
    (
        cancellable = true,
        method = "swing",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
        )
    )
    private void NT$onSendSwingPacket(InteractionHand hand, CallbackInfo callback)
    {
        boolean isSwingBlocked = this.NT$isSwingBlocked();
        this.NT$setSwingBlocked(false);

        if (ModConfig.Animation.oldSwingDropping() && NostalgicTweaks.isNetworkVerified() && isSwingBlocked)
            callback.cancel();
    }

    /**
     * Lets the crouching state be active in while the player is in creative mode.
     * Controlled by the old creative crouch animation tweak.
     */
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(Z)V"))
    private void NT$onAiStep(CallbackInfo callback)
    {
        if (ModConfig.Animation.oldCreativeCrouch() && this.getAbilities().flying)
            this.crouching = !this.isSwimming() && this.canEnterPose(Pose.CROUCHING) && (this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING));
    }

    /**
     * Prevents player movement issues when consuming items while the instant eat tweak is active.
     * Controlled by the instant eat tweak.
     */
    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void NT$onIsUsingItem(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.instantEat() && this.startedUsingItem && this.getUseItem().isEdible())
            callback.setReturnValue(false);
    }

    /**
     * Prevents water enter/exit sounds and underwater ambient sounds from playing.
     * Controlled by the disabled water ambience tweak.
     */
    @ModifyArg(method = "updateIsUnderwater", index = 5, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private float NT$onUpdateIsUnderwater(float volume)
    {
        return ModConfig.Sound.disableWaterAmbience() ? 0.0F : volume;
    }

    /**
     * Prevents underwater looping ambience sounds from playing.
     * Controlled by the disabled water ambience tweak.
     */
    @Redirect(method = "updateIsUnderwater", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void NT$onPlayUnderwaterAmbience(SoundManager manager, SoundInstance sound)
    {
        if (!ModConfig.Sound.disableWaterAmbience())
            manager.play(sound);
    }
}
