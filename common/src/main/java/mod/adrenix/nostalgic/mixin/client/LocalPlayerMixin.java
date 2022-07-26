package mod.adrenix.nostalgic.mixin.client;

import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.ILocalSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements ILocalSwing
{
    /* Dummy Constructor */

    private LocalPlayerMixin(ClientLevel level, GameProfile profile, @Nullable ProfilePublicKey key)
    {
        super(level, profile, key);
    }

    /* Shadows */

    @Shadow public abstract boolean isShiftKeyDown();

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
        Minecraft minecraft = Minecraft.getInstance();
        boolean isSwingBlocked = this.NT$isSwingBlocked();
        boolean isSingleplayer = minecraft.hasSingleplayerServer();

        this.NT$setSwingBlocked(false);

        if (ModConfig.Animation.oldSwingDropping() && isSwingBlocked && isSingleplayer)
            callback.cancel();
    }
}
