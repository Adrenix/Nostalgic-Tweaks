package mod.adrenix.nostalgic.mixin.client;

import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.ILocalSwing;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Hand;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerEntity implements ILocalSwing
{
    /* Dummy Constructor */

    private LocalPlayerMixin(ClientWorld level, GameProfile profile, @Nullable ProfilePublicKey key)
    {
        super(level, profile, key);
    }

    /* Shadows */

    @Shadow private boolean crouching;
    @Shadow private boolean startedUsingItem;
    @Shadow public abstract boolean isSneaking();

    /* Client Side Swing Ducking */

    @Unique private boolean NT$swingBlocked = false;
    @Override public void NT$setSwingBlocked(boolean state) { this.NT$swingBlocked = state; }
    @Override public boolean NT$isSwingBlocked() { return this.NT$swingBlocked; }

    /* Injections */

    /**
     * Blocks the client side rendering of the swinging animation, but still sends the swing packet to the server.
     * Controlled by the old swing dropping tweak.
     */
    @Redirect(method = "swing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    private void NT$onSwing(AbstractClientPlayerEntity instance, Hand hand)
    {
        if (!this.NT$isSwingBlocked())
            super.swingHand(hand);
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
    private void NT$onSendSwingPacket(Hand hand, CallbackInfo callback)
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
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private void NT$onAiStep(CallbackInfo callback)
    {
        if (ModConfig.Animation.oldCreativeCrouch() && this.getAbilities().flying)
            this.crouching = !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
    }

    /**
     * Prevents player movement issues when consuming items while the instant eat tweak is active.
     * Controlled by the instant eat tweak.
     */
    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void NT$onIsUsingItem(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.instantEat() && this.startedUsingItem && this.getActiveItem().isFood())
            callback.setReturnValue(false);
    }
}
